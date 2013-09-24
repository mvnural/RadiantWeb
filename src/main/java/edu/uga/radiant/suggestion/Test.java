package edu.uga.radiant.suggestion;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import edu.uga.cs.wstool.parser.sawsdl.OperationOBJ;
import edu.uga.cs.wstool.parser.sawsdl.SAWSDLParser;
import edu.uga.cs.wstool.parser.sawsdl.SimpleTypeOBJ;
import edu.uga.radiant.ajax.LoadOWL;
import edu.uga.radiant.ontology.OntologyManager;
import edu.uga.radiant.stringmetrics.CompareTerm;
import edu.uga.radiant.util.SortValueMap;

public class Test {
	
	public static SortValueMap<SuggestionOBJ, Double> getMatchClass(OntologyManager mgr, String name, String description, ArrayList<String> ParamIRI) throws URISyntaxException{
		SortValueMap<SuggestionOBJ, Double> result = new SortValueMap<SuggestionOBJ, Double>();
		SimpleStringMatcher matcher = new SimpleStringMatcher(mgr);
		result = matcher.getOpSuggestion(name, description, ParamIRI);
		return result;
	}
	
	public static SortValueMap<SuggestionOBJ, Double> getMatchClass(LuceneIndex lucene, String name, String description, int num, String type) throws CorruptIndexException, ParseException, IOException, URISyntaxException{
		SortValueMap<SuggestionOBJ, Double> result = new SortValueMap<SuggestionOBJ, Double>();
		ArrayList<SuggestionOBJ> score = lucene.getMatchClass(description, 10, LuceneIndex.ParameterContent);
		for (SuggestionOBJ sug : score){
			double labelScore = CompareTerm.getSimilarity(sug.getConceptLabel(), name);
			double totalScore = 0.8 * sug.getScore() + 0.2 * labelScore;
			sug.setScore(totalScore);
			result.put(sug, totalScore);
		}
		return result;
	}

	public static void main(String[] args) throws Exception{
		
		OntologyManager mgr = null;
		ArrayList<String> paramIRI = null;
        try {
            mgr = new OntologyManager("file:/D:/webService.owl");
            paramIRI = LoadOWL.getOwlParamSuperClasses(mgr);
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }
		LuceneIndex lucene = new LuceneIndex(mgr);
		
		// access by file system
    	URL fileURL = new URL("file:/D:/clustalw2.sawsdl");
    	SAWSDLParser semP = new SAWSDLParser(fileURL);
		
    	long start = System.currentTimeMillis();
        for (OperationOBJ oper : semP.getAllOperations().values()){
        	if (oper.getName().equals("run")){
        		System.out.println("operation name = " + oper.getName());
        		
    			for (SimpleTypeOBJ simple : SAWSDLParser.getAllSimpleType(oper.getInput())){
    				ArrayList<SuggestionOBJ> score1 = lucene.getMatchClass(simple.getDescription(), 10, LuceneIndex.ParameterContent);
    				int count = 0;
    				for (SuggestionOBJ sug : score1){
    					if (count > 5) break;
    					System.out.println("test1 simple type name = " + simple.getName() + ", IRI = " + sug.getConceptIRI() + ", score = " + sug.getScore());
    					count++;
    				}
    				
    				//SortValueMap<SuggestionOBJ, Double> score2 = getMatchClass(lucene, simple.getName(), simple.getDescription(), 10, LuceneIndex.AllContent);
    				//count = 0;
    				//for (SuggestionOBJ sug : score2.keyList()){
    				//	if (count > 5) break;
    				//	System.out.println("test2 simple type name = " + simple.getName() + ", IRI = " + sug.getConceptIRI() + ", score = " + sug.getScore());
    				//	count++;
    				//}
    				
    				//SortValueMap<SuggestionOBJ, Double> score3 = getMatchClass(mgr, simple.getName(), simple.getDescription(), paramIRI);
    				//count = 0;
    				//for (SuggestionOBJ sug : score3.keyList()){
    				//	if (count > 5) break;
        			//	System.out.println("old simple type name = " + simple.getName() + ", IRI = " + sug.getConceptIRI() + ", score = " + sug.getScore());
    				//	count++;
    				//}
    				
    			}
    			
    			/*
    			for (SimpleTypeOBJ simple : SAWSDLParser.getAllSimpleType(oper.getOutput())){
    				ArrayList<SuggestionOBJ> score1 = lucene.getMatchClass(simple.getDescription(), 10, LuceneIndex.ParameterContent);
    				Map<SuggestionOBJ, Double> score2 = getMatchClass(mgr, simple.getName(), simple.getDescription(), paramIRI);
    				int count = 0;
    				for (SuggestionOBJ sug : score1){
    					if (count > 5) break;
    					System.out.println("new simple type name = " + simple.getName() + ", IRI = " + sug.getConceptIRI() + ", score = " + sug.getScore());
    					count++;
    				}
    				count = 0;
    				for (SuggestionOBJ sug : score2.keySet()){
    					if (count > 5) break;
        				System.out.println("old simple type name = " + simple.getName() + ", IRI = " + sug.getConceptIRI() + ", score = " + sug.getScore());
    					count++;
    				}
    			}
    			*/
        	} // if
        }
		long end = System.currentTimeMillis();
        System.out.println("latency = " + (end - start));

	}
	
}
