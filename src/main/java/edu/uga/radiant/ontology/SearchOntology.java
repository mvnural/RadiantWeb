package edu.uga.radiant.ontology;

import edu.uga.radiant.stringmetrics.CompareTerm;
import edu.uga.radiant.util.SortValueMap;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 *
 * @author Chaitu
 */
public class SearchOntology {
    
	private OntologyManager manager;
	
    private static Map<String, SortValueMap<String,Double>> searchResult = new HashMap<String, SortValueMap<String,Double>>();
    
    
    public SearchOntology(OntologyManager mgr){
    	manager = mgr;
    }
    
    public SortValueMap<String, Double> search(String term){
        if(searchResult != null && !searchResult.isEmpty() && searchResult.keySet().contains(term)){
            return searchResult.get(term);
        }else{
        	SortValueMap<String, Double> resultScore = new SortValueMap<String, Double>();
    		if (manager != null){
        		for (String key : manager.getAllClassLabels().keySet()){
        			String label = key;
        			String iri = manager.getAllClassLabels().get(key).getIRI().toString();
        			double labScore = CompareTerm.getSimilarity(label.toLowerCase(), term.toLowerCase());
                    double iriScore = CompareTerm.getSimilarity(iri, term);
                    if(labScore > 0.1 || iriScore > 0.1){
                    	resultScore.put(iri, labScore);
                    }
        		}
        	}
            searchResult.put(term, resultScore);
            return resultScore;
        }
    }
    
    public static void main(String[] args) throws URISyntaxException, OWLOntologyCreationException{
        
    }
}