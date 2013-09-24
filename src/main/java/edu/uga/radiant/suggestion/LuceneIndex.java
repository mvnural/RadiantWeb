package edu.uga.radiant.suggestion;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import edu.uga.cs.wstool.parser.sawsdl.OperationOBJ;
import edu.uga.cs.wstool.parser.sawsdl.SAWSDLParser;
import edu.uga.cs.wstool.parser.sawsdl.SimpleTypeOBJ;
import edu.uga.radiant.ajax.LoadOWL;
import edu.uga.radiant.ontology.OntologyManager;

public class LuceneIndex {

	public static final String OperationContent = "OperationContent";
	public static final String ParameterContent = "ParameterContent";
	public static final String AllContent = "AllContent";
	public static final String Label = "Label";
	public static final String URI = "URI";
	
	private StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
	
	private Directory index = new RAMDirectory();
	
	private IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_35, analyzer);
	
	public LuceneIndex(OntologyManager mgr) throws CorruptIndexException, LockObtainFailedException, IOException, URISyntaxException{
		// write Whole OWLClass info into index structure
		IndexWriter w = new IndexWriter(index, config);
		for (OWLClass cls : mgr.getAllOWLclass().values()){
			addConceptDoc(w, cls.getIRI().toString(), mgr.getClassDefinition(cls), mgr.getClassLabel(cls), LuceneIndex.AllContent);
		}
		// write operation OWLClass index structure
		ArrayList<String> OperationIRIs = LoadOWL.getOwlOperationSuperClasses(mgr);
		HashSet<OWLClass> op = new HashSet<OWLClass>();
		for (String iri : OperationIRIs){
			OWLClass opSuperCls = mgr.getConceptClass(iri);
			for (Set<OWLClass> set : mgr.getSubClasses(opSuperCls)){
				op.addAll(set);
			}
		}
		for (OWLClass cls : op){
			addConceptDoc(w, cls.getIRI().toString(), mgr.getClassDefinition(cls), mgr.getClassLabel(cls), LuceneIndex.OperationContent);
		}
		// write parameter OWLClass index structure
		ArrayList<String> ParamIRIs = LoadOWL.getOwlParamSuperClasses(mgr);
		HashSet<OWLClass> pa = new HashSet<OWLClass>();
		for (String iri : ParamIRIs){
			OWLClass paSuperCls = mgr.getConceptClass(iri);
			for (Set<OWLClass> set : mgr.getSubClasses(paSuperCls)){
				pa.addAll(set);
			}
		}
		for (OWLClass cls : pa){
			addConceptDoc(w, cls.getIRI().toString(), mgr.getClassDefinition(cls), mgr.getClassLabel(cls), LuceneIndex.ParameterContent);
		}
		w.close();
	}
	
	public String getTopMatchClass(String quertString) throws ParseException, CorruptIndexException, IOException, URISyntaxException{
		ArrayList<SuggestionOBJ> sug = getMatchClass(quertString, 1, LuceneIndex.AllContent);
		if (sug != null && sug.size() > 0){
			return sug.get(0).getConceptIRI();
		}else{
			return null;
		}
	}
	
	public String getTopOperationMatchClass(String quertString) throws ParseException, CorruptIndexException, IOException, URISyntaxException{
		ArrayList<SuggestionOBJ> sug = getMatchClass(quertString, 1, LuceneIndex.OperationContent);
		if (sug != null && sug.size() > 0){
			return sug.get(0).getConceptIRI();
		}else{
			return getTopMatchClass(quertString);
		}
	}
	
	public String getTopParamMatchClass(String quertString) throws ParseException, CorruptIndexException, IOException, URISyntaxException{
		ArrayList<SuggestionOBJ> IRIs = getMatchClass(quertString, 1, LuceneIndex.AllContent);
		if (IRIs != null && IRIs.size() > 0){
			return IRIs.get(0).getConceptIRI();
		}else{
			return getTopMatchClass(quertString);
		}
	}
	
	public ArrayList<SuggestionOBJ> getMatchClass(String quertString, int rankNum, String field) throws ParseException, CorruptIndexException, IOException, URISyntaxException{
		ArrayList<SuggestionOBJ> results = new ArrayList<SuggestionOBJ>();
		if (quertString == null || quertString.equals("")) return null;
		quertString = replaceChar(quertString);
		Query q = new QueryParser(Version.LUCENE_35, field, analyzer).parse(quertString);
		int TopRank = rankNum;
		IndexSearcher searcher = new IndexSearcher(IndexReader.open(index));
		TopScoreDocCollector collector = TopScoreDocCollector.create(TopRank, true);
		searcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			SuggestionOBJ sobj = new SuggestionOBJ();
			sobj.setConceptIRI(d.get(LuceneIndex.URI));
			sobj.setConceptLabel(d.get(LuceneIndex.Label));
			sobj.setScore(hits[i].score);
			results.add(sobj);
		}
		searcher.close();
		return results;
	}
	
	private static void addConceptDoc(IndexWriter writer, String uri, String description, String label, String contentField) throws IOException, URISyntaxException {
		if (description == null) description = "";
		description = replaceChar(description);
		Document doc = new Document();
		doc.add(new Field(LuceneIndex.URI, uri, Field.Store.YES, Field.Index.ANALYZED));
		doc.add(new Field(LuceneIndex.Label, label, Field.Store.YES, Field.Index.ANALYZED));
		doc.add(new Field(contentField, description, Field.Store.YES, Field.Index.ANALYZED));
		writer.addDocument(doc);
	}
	
	public static String replaceChar(String input){
		input = input.replaceAll("[`~!@#$%^&*()_+={}:;\"',<.>?/]", "");
		input = input.replace("[", "");
		input = input.replace("]", "");            
        return input;
	}
	
	public static void main(String[] args) throws Exception{
		
		OntologyManager mgr = null;
        try {
            mgr = new OntologyManager("file:/D:/webService.owl");
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }
		LuceneIndex lucene = new LuceneIndex(mgr);
		
		// access by file system
    	URL fileURL = new URL("file:/D:/clustalw2.sawsdl");
    	SAWSDLParser semP = new SAWSDLParser(fileURL);
		
    	long start = System.currentTimeMillis();
        for (OperationOBJ oper : semP.getAllOperations().values()){
        	System.out.println("operation name = " + oper.getName());
			for (SimpleTypeOBJ simple : SAWSDLParser.getAllSimpleType(oper.getInput())){
				//System.out.println("simple type description = " + simple.getDescription());
				String IRI = lucene.getTopParamMatchClass(simple.getDescription());
				System.out.println("simple type name = " + simple.getName() + ", IRI = " + IRI);
			}
			for (SimpleTypeOBJ simple : SAWSDLParser.getAllSimpleType(oper.getOutput())){
				String IRI = lucene.getTopParamMatchClass(simple.getDescription());
				System.out.println("simple type name = " + simple.getName() + ", IRI = " + IRI);
			}
        }
		long end = System.currentTimeMillis();
        System.out.println("latency = " + (end - start));
	}
	
	
}
