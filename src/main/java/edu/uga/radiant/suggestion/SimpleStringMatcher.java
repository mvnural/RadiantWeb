/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uga.radiant.suggestion;

import edu.uga.cs.wstool.parser.sawsdl.ComplexTypeOBJ;
import edu.uga.cs.wstool.parser.sawsdl.SAWSDLParser;
import edu.uga.cs.wstool.parser.sawsdl.SimpleTypeOBJ;
import edu.uga.cs.wstool.parser.xml.XMLParser;
import edu.uga.radiant.ontology.OntologyManager;
import edu.uga.radiant.ontology.Triple;
import edu.uga.radiant.stringmetrics.CompareDefinition;
import edu.uga.radiant.stringmetrics.CompareTerm;
import edu.uga.radiant.stringmetrics.CompareTermDef;
import edu.uga.radiant.util.SortValueMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;

/**
 *
 * @author Yung-Long Li
 */


public class SimpleStringMatcher {
	
	public static final String recommenderUrl = "http://rest.bioontology.org/recommender";
	
	private static int ComplexTypeWeight = 5;
	private static int SimpleTypeWeight = 1;

    private OntologyManager manager;
    
    public SimpleStringMatcher(OntologyManager mgr){
    	manager = mgr;
    }
    
    /**
     * Given the operation term of the WSDL return the scores of similarity for all the operation terms in the Ontology
     * Parameter terms from the ontology are all the sub classes of the term "operation.concept" in 
     * RaidantProperties.properties file.
     * @param EleName the name of element to find the similarity for
     * @param EleDoc the document of element to find the similarity for
     * @param ont Ontology that needs to be suggested
     * @param operationIRI the owl concept which is a super class of all parameter concepts 
     * @return SortValueMap contain suggestion object and score
     */
    public SortValueMap<SuggestionOBJ,Double> getOpSuggestion(String EleName, String EleDoc, ArrayList<String> operationIRI) throws URISyntaxException {
    	
    	SortValueMap<SuggestionOBJ, Double> suggestion = new SortValueMap<SuggestionOBJ, Double>();
    	
    	GetOntologyConcepts test = new GetOntologyConcepts();
        OWLOntology ont = manager.getOntology();
        Set<OWLClass> lst = new HashSet<OWLClass>();
        if (operationIRI != null && operationIRI.size() != 0){	// if there is a 
        	for (String op : operationIRI){
            	lst.addAll(GetOntologyConcepts.getOperationConcetps(ont, op));   //OntologyHandler.getOperationConcetps(ont);
            }
        }
        if (lst.size() == 0){
        	lst.addAll(manager.getAllOWLclass().values());
        }
        
        for(OWLClass cl : lst){
            String clName = test.getLabel(cl, ont);
            String clsDesc = manager.getOntologyConceptDescription(cl);
            // get the similarity score
            if(clsDesc == null){
                clsDesc = manager.getClassDefinition(cl);
            }
            // store in suggestion object
            SuggestionOBJ suggestobj = new SuggestionOBJ();
            suggestobj.setConceptIRI(cl.getIRI().toString());
            suggestobj.setConceptLabel(clName);
            suggestobj.setConceptDoc(clsDesc);
            double labelScore = CompareTerm.getSimilarity(EleName, clName);//qgd.getSimilarity(wsdlEleNa`me,clName);
            double descScore = CompareDefinition.getSimilarity(EleDoc, clsDesc);//qgd.getSimilarity(wsdlEleDoc, clsDesc);
            double labeldescScore = CompareTermDef.getSimilarity(EleName, clsDesc);
            double labeldescScore1 = CompareTermDef.getSimilarity(clName, EleDoc);
            labeldescScore = (labeldescScore + labeldescScore1)/2;
            
            double totalScore = getTotalScore(labelScore, descScore, labeldescScore);
            suggestobj.setScore(totalScore);
            
            // store score in suggestion obj
            suggestion.put(suggestobj, totalScore);
        }
        
        return suggestion;
        
    }
    
    
    
    
	/**
     * Given the parameter term of the WSDL return the scores of similarity for all the parameter terms in the Ontology
     * Parameter terms from the ontology are all the sub classes of the term "parameter.concept" in 
     * RaidantProperties.properties file.
     * @param EleName : the name of element to find the similarity for
     * @param EleDoc : the document of element to find the similarity for
     * @param ont : Ontology that needs to be suggested
     * @param paramIRI : the owl concept which is a super class of all parameter concepts 
     * @return SortValueMap contain suggestion object and score
	 * @throws JDOMException 
	 * @throws URISyntaxException 
     */
    public SortValueMap<SuggestionOBJ, Double> getComplexSuggestion(XMLParser wsparser, int eleID, String complexName, String complexDoc, String operation, ArrayList<String> paramIRI) throws JDOMException, URISyntaxException {
		
    	ComplexTypeOBJ cplex = (ComplexTypeOBJ) ((SAWSDLParser)wsparser).getAnnotationObjectMap().get(eleID);
    	
    	double ratio = checkAnnotateOfLeafNodes(cplex);
    	
    	
    	// get the leaf nodes of complex types
    	Set<Node> leafNodes = SimpleStringMatcher.dataTransform(cplex, manager);
        
    	
    	
    	// get candidates
        Set<SuggestionOBJ> candidates = SimpleStringMatcher.getSchemaMappingSuggestion(leafNodes, manager);
		
        // sort score
        SortValueMap<SuggestionOBJ, Double> score = new SortValueMap<SuggestionOBJ, Double>();
		for (SuggestionOBJ obj : candidates){
			OWLClass cls = manager.getConceptClass(obj.getConceptIRI());
			double labelScore = CompareTerm.getSimilarity(cplex.getName(), manager.getClassLabel(cls));//qgd.getSimilarity(wsdlEleNa`me,clName);
            double descScore = CompareDefinition.getSimilarity(cplex.getDescription(), manager.getClassDefinition(cls));//qgd.getSimilarity(wsdlEleDoc, clsDesc);
            double labeldescScore = CompareTermDef.getSimilarity(cplex.getName(), manager.getClassDefinition(cls));
            double labeldescScore1 = CompareTermDef.getSimilarity(manager.getClassLabel(cls), cplex.getDescription());
            labeldescScore = (labeldescScore + labeldescScore1)/2;
            double newScore = SimpleStringMatcher.getTotalScore(labelScore, descScore, labeldescScore);
            Double totalScore = Math.sqrt(newScore * obj.getScore());
            score.put(obj, totalScore);
		}
		
		return score;
	}
    
	/**
     * Mapping the leaf nodes of a complex type object and calculate the mapping score 
     * of candidate concept and complex type
     * @param leaves the leaf semantic concepts 
     * @param mgr ontology manager
     * @return Set of suggestion 
     */
    public static Set<SuggestionOBJ> getSchemaMappingSuggestion(Set<Node> leaves, OntologyManager mgr){
    	
    	Set<SuggestionOBJ> result = new HashSet<SuggestionOBJ>();
		HashMap<OWLClass, Integer> score = new HashMap<OWLClass, Integer>();
		
		// calculate total weight
		int Totalweight = 0;
		for (Node node : leaves){
			if (node.isComplexType()){
				Totalweight = Totalweight + SimpleStringMatcher.ComplexTypeWeight;
			}else{
				Totalweight = Totalweight + SimpleStringMatcher.SimpleTypeWeight;
			}
		}
		
		int counter = 0;
		for (Node leaf : leaves){
			Set<Triple> triples = mgr.getRestrictTriplesByChild(leaf.getCls());
        	if (triples != null){
        		for (Triple triple : triples){
        			if (score.containsKey(triple.getParent())){
            			if (leaf.isComplexType()){
            				counter = score.get(triple.getParent()) + SimpleStringMatcher.ComplexTypeWeight;
            			}else{
            				counter = score.get(triple.getParent()) + SimpleStringMatcher.SimpleTypeWeight;
            			}
            			score.put(triple.getParent(), counter);
            		}else{
    					if (leaf.isComplexType()){
            				counter = SimpleStringMatcher.ComplexTypeWeight;
            			}else{
            				counter = SimpleStringMatcher.SimpleTypeWeight;
            			}
    					score.put(triple.getParent(), counter);
    				}
            	}
        	}
        }
		
		// calculate total score
		for (OWLClass cls : score.keySet()){
			//if (score.get(cls) < (Totalweight / 2)) continue;
			SuggestionOBJ obj = new SuggestionOBJ();
			obj.setConceptLabel(mgr.getClassLabel(cls));
			obj.setConceptDoc(mgr.getClassDefinition(cls));
			obj.setConceptIRI(cls.getIRI().toString());
			obj.setScore((double)score.get(cls) / Totalweight);
			result.add(obj);
		}
		return result;
	}
    
    
    /**
     * Given the parameter term of the WSDL/WADL return the scores of similarity for all the parameter terms in the Ontology
     * Parameter terms from the ontology are all the sub classes of the term "parameter.concept" in 
     * RaidantProperties.properties file.
     * @param EleName : the name of element to find the similarity for
     * @param EleDoc : the document of element to find the similarity for
     * @param ont : Ontology that needs to be suggested
     * @param paramIRI : the owl concept which is a super class of all parameter concepts 
     * @return SortValueMap contain suggestion object and score
     */
    public SortValueMap<SuggestionOBJ,Double> getParamSuggestion(String EleName, String EleDoc, ArrayList<String> paramIRI) throws URISyntaxException {
    	
        SortValueMap<SuggestionOBJ, Double> suggestion = new SortValueMap<SuggestionOBJ, Double>();
    	
    	GetOntologyConcepts test = new GetOntologyConcepts();
        OWLOntology ont = manager.getOntology();
        Set<OWLClass> lst = new HashSet<OWLClass>();
        if (paramIRI != null && paramIRI.size() != 0){	// if there is a 
        	for (String param : paramIRI){
            	lst.addAll(GetOntologyConcepts.getParameterConcetps(ont, param));   //OntologyHandler.getOperationConcetps(ont);
            }
        }
        if (lst.size() == 0){
        	lst.addAll(manager.getAllOWLclass().values());
        }
        
        for(OWLClass cl : lst){
        	String clName = test.getLabel(cl, ont);
            String clsDesc = manager.getOntologyConceptDescription(cl);
            if(clsDesc == null){
                clsDesc = manager.getClassDefinition(cl);
            }
            SuggestionOBJ suggestobj = new SuggestionOBJ();
            suggestobj.setConceptLabel(clName);
            suggestobj.setConceptDoc(clsDesc);
            suggestobj.setConceptIRI(cl.getIRI().toString());
            double labelScore = CompareTerm.getSimilarity(EleName, clName);//qgd.getSimilarity(wsdlEleNa`me,clName);
            double descScore = CompareDefinition.getSimilarity(EleDoc, clsDesc);//qgd.getSimilarity(wsdlEleDoc, clsDesc);
            double labeldescScore = CompareTermDef.getSimilarity(EleName, clsDesc);
            double labeldescScore1 = CompareTermDef.getSimilarity(clName, EleDoc);
            labeldescScore = (labeldescScore + labeldescScore1)/2;
            double totalScore = getTotalScore(labelScore, descScore, labeldescScore);
            
            // store score in suggestion obj
            suggestion.put(suggestobj, totalScore);
        }
        
        return suggestion;

    }
    
    /**
     * given the wsdl description and return the recommend ontology
     * @param corpus wsdl description
     * @return suggestion of ontology
     * @throws URISyntaxException
     */
    public SortValueMap<SuggestionOBJ, Double> getRecommendOntology(String corpus) throws URISyntaxException {
    	
    	SortValueMap<SuggestionOBJ, Double> suggestion = new SortValueMap<SuggestionOBJ, Double>();
    	
		try {
			HttpClient client = new HttpClient();
			PostMethod method = new PostMethod(recommenderUrl);

			System.out.println("Calling Web service...");

			// Configure the form parameters
			method.addParameter("text", corpus);
			method.addParameter("format", "simpleText");
			method.addParameter("apikey", "bd95ce53-f900-43f8-a555-a02e9a358ce4");

			// Execute the POST method
			int statusCode = client.executeMethod(method);
			System.out.println("Execute service");
			if (statusCode != -1) {
				try {
					String contents = method.getResponseBodyAsString();
					System.out.println(contents);
					String[] lines = contents.split("\n");
					double count = 5.0; // Counter for just returning top 5 results
					for (String line : lines) {
						// System.out.println(line);

						String[] lineparts = line.split("\t");
						// the format returned by the simpleText paramter is tab
						// delimited ofrmat of number, ontology name, and the
						// number of annotaitons found in that ontology
						// ontologynames.(lineparts[1]); // The foret retuned by
						// the
						SuggestionOBJ sug = new SuggestionOBJ();
						String ontologyName = lineparts[1];
						String ontologyId = lineparts[2];
						String url = getOntologyUrl(ontologyId);
						sug.setConceptIRI(url);
						sug.setConceptLabel(ontologyName);
						sug.setConceptDoc(ontologyId);
						if (url != null && (!url.endsWith(".obo") || !url.endsWith("obo") || !url.endsWith("OBO") || !url.endsWith(".OBO"))) {
							suggestion.put(sug, count);
							if (count-- == 0) break;
						}
					}
					method.releaseConnection();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return suggestion;
    }
    
    /**
     * given the ontology id then return the url of ontology
     * @param id ontology id
     * @return String url of ontology
     * @throws IOException 
     * @throws JDOMException 
     */
	public String getOntologyUrl(String id) throws IOException, JDOMException {
	
		String ontourl = "";
		String urlString = "http://rest.bioontology.org/bioportal/ontologies/" + id.trim() + "?apikey=bd95ce53-f900-43f8-a555-a02e9a358ce4";

		URL url = new URL(urlString);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		InputStream input = con.getInputStream();
		Document doc = XMLParser.generateDocumentation(input);
		Element root = XMLParser.getRootElement(doc);
		Namespace ns = root.getNamespace();
		
        Element data = root.getChild("data", ns);
        if(data != null)
        {
            Element ontBean = data.getChild("ontologyBean", ns);
            if(ontBean != null){
                Element location = ontBean.getChild("downloadLocation", ns);
                if(location != null){
                	ontourl = location.getText().trim();
                }
                else{
                	ontourl = null;
                }
            }
            else{
            	ontourl = null;
            }
        }
        else{
        	ontourl = null;
        }
        return ontourl;
		
	}
    
    /**
     * Given score of definition, title and crossing score then return the final score.
     * @param labelScore : the score of label or title
     * @param descScore : the score of definition or description
     * @param labeldescScore : the average score of crossing comparing 
     * @return double final score
     */
    public static double getTotalScore(double labelScore, double descScore, double labeldescScore) {
		double result = 0.0D;
		double descWeight = 0;
        double labelWeight = 0;
        double labeldescWeight = 0;
        if(descScore > 0.4){
            descWeight = 0.8;
            labelWeight = 0.1;
            labeldescWeight = 0.1;
        }
        else{
            descWeight = 0.6;
            labelWeight = 0.2;
            labeldescWeight = 0.2;
        }
        result = (descWeight*descScore + labelWeight*labelScore + labeldescScore*labeldescWeight) / (descWeight + labelWeight + labeldescWeight);
        return result;
	}
    
    /**
     * transform the complex type to the set of Node(another hierarchy which has OWLClass and OWLProperty)
     * @param cplex complex type of wsdl
     * @param mgr ontology manager
     * @return set of Node
     */
    public static Set<Node> dataTransform(ComplexTypeOBJ cplex, OntologyManager mgr){
    	Set<Node> output = new HashSet<Node>();
    	for (SimpleTypeOBJ simple : cplex.getSimples()){
			if (simple.getModelReference() != null) {
				OWLClass cls = mgr.getConceptClass(simple.getModelReference().toString());
				Node node = new Node();
				node.setCls(cls);
				node.setComplexType(false);
				if (cls != null) output.add(node);
			}
		}
    	for (ComplexTypeOBJ clex : cplex.getComplextypes()){
    		if (clex.getModelReference() != null) {
    			OWLClass cls = mgr.getConceptClass(clex.getModelReference().toString());
    			Node node = new Node();
    			node.setCls(cls);
    			node.setComplexType(true);
    			if (cls != null) output.add(node);
    		}
		}
    	
    	return output;
    }
    
    /**
     * check the complex type to see if it is well annotated by enough OWLClass.
     * @param cplex the complex type
     * @return the ratio which indicate how many node is annotated 
     */
    private double checkAnnotateOfLeafNodes(ComplexTypeOBJ cplex) {
		
    	double ratio = 0.0;
    	
    	
    	// not implement
    	
    	
    	return ratio;
    	
	}
    
    /**
     * function test
     * 
     */
    public static void main(String [] args) throws OWLOntologyCreationException{
        
    }

	

	
}
