package edu.uga.cs.lumina.discovery.util;


import static ontology.similarity.Hungarian.hungarian;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ontology.similarity.ConceptSimilarity;
import ontology.similarity.Hungarian;
import ontology.similarity.TypeCompatibility;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import edu.uga.cs.wstool.parser.sawsdl.ComplexTypeOBJ;
import edu.uga.cs.wstool.parser.sawsdl.SAWSDLParser;
import edu.uga.cs.wstool.parser.sawsdl.SimpleTypeOBJ;
import edu.uga.radiant.stringmetrics.CompareDefinition;
import edu.uga.radiant.stringmetrics.InvalidNGramException;
import edu.uga.radiant.util.RadiantToolConfig;

public class SawsdlSimilarity {

	private static double[] w = {0.3, 0.3, 0.1, 0.3};
	private static double threshold = 0.8D; 

    public static double oPSim(WsOpReader req, WsOpReader ws, String owlURI) throws Exception {

        Logger logger = RadiantToolConfig.getLogger(); 
        
    	double inSim = inpSim(req, ws, owlURI);
    	double outSim = oupSim(req, ws, owlURI);
    	double nameSyn = synSim(req.getOperationName(), ws.getOperationName());
    	double funSim = conSim(req.getFunctionality(), ws.getFunctionality(), owlURI);
    	logger.debug("inSim = " + inSim);
    	logger.debug("outSim = " + outSim);
    	logger.debug("nameSyn = " + nameSyn);
    	logger.debug("funSim = " + funSim);
    	
    	return w[0] * inSim + w[1] * outSim + w[2] * nameSyn + w[3] * funSim;
    } // oPSim 

    public static double inpSim(WsOpReader req, WsOpReader ws, String owlURI) throws Exception{
    	
    	// set the request complex types 
    	List<ComplexTypeOBJ> ReqComplexs = new ArrayList<ComplexTypeOBJ>();
    	for(ComplexTypeOBJ obj : req.getInput().getComplexType()){
    		ReqComplexs.addAll(getNestedComplexType(obj));
    	}
    	
    	// set the web service complex types
    	List<ComplexTypeOBJ> WsComplexs = new ArrayList<ComplexTypeOBJ>();
    	for(ComplexTypeOBJ obj : ws.getInput().getComplexType()){
    		WsComplexs.addAll(getNestedComplexType(obj));
    	}
    	
    	// scores
    	List<Double> complexScores = new ArrayList<Double>();
    	
    	// start to compare the complex type, if there exist
    	if(ReqComplexs.size() > 0 && WsComplexs.size() > 0){
    		
    		// the temp list for store the unmatched complex type
    		Set<ComplexTypeOBJ> ReqTemp = new HashSet<ComplexTypeOBJ>();
    		Set<ComplexTypeOBJ> WsTemp = new HashSet<ComplexTypeOBJ>();
    		
        	// iterate the web service complex type
        	for (ComplexTypeOBJ wcplex : WsComplexs){
    			
        		// the wcplex is the sub complex type of the best matched 
        		if (WsTemp.contains(wcplex)) continue;
        		
        		// the best matched score
        		double maxScore = 0.0D;
        		
        		// the best matched complex type
    			ComplexTypeOBJ index = null;
    			
    			// iterate the request complex type
        		for (ComplexTypeOBJ rcplex : ReqComplexs){
        			
        			// the rcplex is the sub complex type of the best matched 
            		if (ReqTemp.contains(rcplex)) continue;
        			
        			// compare concept
            		double score = conSim(wcplex.getModelReference(), rcplex.getModelReference(), owlURI);
            		if (score > threshold){
            			if (score > maxScore){	// get the best matched concept
            				maxScore = score;
            				// put the previous matched complex type into temp unmatched concept
            				if (index != null){
            					ReqTemp.add(index);
            					ReqTemp.addAll(getNestedComplexType(index));
            				}
            				// reset the best matched complex type
            				index = rcplex;
            			}
            		}else{	// if score is under threshold, then put into temp unmatched list
            			ReqTemp.add(rcplex);
            			ReqTemp.addAll(getNestedComplexType(rcplex));
            		}
        		}
        		if (maxScore == 0){	// no score is over threshold
        			// put web service into temp unmatched complex type
        			WsTemp.add(wcplex);
        			WsTemp.addAll(getNestedComplexType(wcplex));
        		}else{
        			complexScores.add(maxScore);
        		}
        	}
        	
        	// set the remain complex type
        	ReqComplexs = ToList(ReqTemp);
        	WsComplexs = ToList(WsTemp);
    	}
    	// get average complex type score
    	Double totalScore = 0.0D;
    	for (Double s : complexScores){
    		totalScore = totalScore + s;
    	}
    	Double averageComplex = 0.0D;
    	if (complexScores.size() > 0) averageComplex = totalScore / complexScores.size();
    	
    	// get score simple types of rest complex type 
    	List<SimpleTypeOBJ> request = new ArrayList<SimpleTypeOBJ>();
    	request.addAll(req.getInput().getSimpleType());
    	for (ComplexTypeOBJ complex : ReqComplexs){
    		request.addAll(SAWSDLParser.getAllSimpleType(complex));
    	}
    	List<SimpleTypeOBJ> service = new ArrayList<SimpleTypeOBJ>();
    	service.addAll(ws.getInput().getSimpleType());
    	for (ComplexTypeOBJ complex : WsComplexs){
    		service.addAll(SAWSDLParser.getAllSimpleType(complex));
    	}
    	
    	if (request.size() == 0 | service.size() == 0 ) return 0.0;
    	double[][] matrix = new double[request.size()][service.size()];
        for (int i = 0; i < request.size(); i++) {
        	for (int j = 0; j < service.size() ; j++) {
                double synsim = synSim(request.get(i).getName(), service.get(j).getName());
        		double consim = conSim(request.get(i).getModelReference(), service.get(j).getModelReference(), owlURI);
                double typesim = TypeCompatibility.CompatibilityMatch(request.get(i).getType(), service.get(j).getType());
                double weight = 1.0D;
                // if it is an optional parameter
                if (!service.get(j).isRequired()) weight = 0.33D;
                matrix[i][j] = weight * (synsim*0.2 + typesim*0.2 + consim*0.6);
        	}
        }
        double simpleScore = (request.size() > service.size()) ? hungarian(Hungarian.transpose(matrix)): hungarian(matrix);  
        
        // return score
        if (req.getInput().getComplexType().size() == 0){
        	// request has no complex type 
        	return simpleScore;
        }else if(request.size() == 0){
        	// request has no simple type 
        	return averageComplex;
        }else{
        	// request has complex type
        	return simpleScore*0.5 + averageComplex*0.5;
        }
    
    } // inpSim
    
	public static double oupSim(WsOpReader req, WsOpReader ws, String owlURI) throws Exception{
    	// set the request complex types 
    	List<ComplexTypeOBJ> ReqComplexs = new ArrayList<ComplexTypeOBJ>();
    	for(ComplexTypeOBJ obj : req.getOutput().getComplexType()){
    		ReqComplexs.addAll(getNestedComplexType(obj));
    	}
    	
    	// set the web service complex types
    	List<ComplexTypeOBJ> WsComplexs = new ArrayList<ComplexTypeOBJ>();
    	for(ComplexTypeOBJ obj : ws.getOutput().getComplexType()){
    		WsComplexs.addAll(getNestedComplexType(obj));
    	}
    	
    	// scores
    	List<Double> complexScores = new ArrayList<Double>();
    	
    	// start to compare the complex type, if there exist
    	if(ReqComplexs.size() > 0 && WsComplexs.size() > 0){
    		
    		// the temp list for store the unmatched complex type
    		Set<ComplexTypeOBJ> ReqTemp = new HashSet<ComplexTypeOBJ>();
    		Set<ComplexTypeOBJ> WsTemp = new HashSet<ComplexTypeOBJ>();
    		
        	// iterate the web service complex type
        	for (ComplexTypeOBJ wcplex : WsComplexs){
    			
        		// the wcplex is the sub complex type of the best matched 
        		if (WsTemp.contains(wcplex)) continue;
        		
        		// the best matched score
        		double maxScore = 0.0D;
        		
        		// the best matched complex type
    			ComplexTypeOBJ index = null;
    			
    			// iterate the request complex type
        		for (ComplexTypeOBJ rcplex : ReqComplexs){
        			
        			// the rcplex is the sub complex type of the best matched 
            		if (ReqTemp.contains(rcplex)) continue;
        			
        			// compare concept
            		double score = conSim(wcplex.getModelReference(), rcplex.getModelReference(), owlURI);
            		if (score > threshold){
            			if (score > maxScore){	// get the best matched concept
            				maxScore = score;
            				// put the previous matched complex type into temp unmatched concept
            				if (index != null){
            					ReqTemp.add(index);
            					ReqTemp.addAll(getNestedComplexType(index));
            				}
            				// reset the best matched complex type
            				index = rcplex;
            			}
            		}else{	// if score is under threshold, then put into temp unmatched list
            			ReqTemp.add(rcplex);
            			ReqTemp.addAll(getNestedComplexType(rcplex));
            		}
        		}
        		if (maxScore == 0){	// no score is over threshold
        			// put web service into temp unmatched complex type
        			WsTemp.add(wcplex);
        			WsTemp.addAll(getNestedComplexType(wcplex));
        		}else{
        			complexScores.add(maxScore);
        		}
        	}
        	
        	// set the remain complex type
        	ReqComplexs = ToList(ReqTemp);
        	WsComplexs = ToList(WsTemp);
    	}
    	// get average complex type score
    	Double totalScore = 0.0D;
    	for (Double db : complexScores){
    		totalScore = totalScore + db;
    	}
    	Double averageComplex = 0.0D;
    	if (complexScores.size() > 0) averageComplex = totalScore / complexScores.size();
    	
    	// get score simple types of rest complex type 
    	List<SimpleTypeOBJ> request = new ArrayList<SimpleTypeOBJ>();
    	request.addAll(req.getOutput().getSimpleType());
    	for (ComplexTypeOBJ complex : ReqComplexs){
    		request.addAll(SAWSDLParser.getAllSimpleType(complex));
    	}
    	List<SimpleTypeOBJ> service = new ArrayList<SimpleTypeOBJ>();
    	service.addAll(ws.getOutput().getSimpleType());
    	for (ComplexTypeOBJ complex : WsComplexs){
    		service.addAll(SAWSDLParser.getAllSimpleType(complex));
    	}
    	
    	if (request.size() == 0 || service.size() == 0 ) return 0.0;
    	double[][] matrix = new double[request.size()][service.size()];
        for (int i = 0; i < request.size(); i++) {
        	for (int j = 0; j < service.size() ; j++) {
        		double synsim = synSim(request.get(i).getName(), service.get(j).getName());
        		double consim = conSim(request.get(i).getModelReference(), service.get(j).getModelReference(), owlURI);
                double typesim = TypeCompatibility.CompatibilityMatch(request.get(i).getType(), service.get(j).getType());
                double weight = 1.0D;
                // if it is an optional parameter
                if (!service.get(j).isRequired()) weight = 0.33D;
                matrix[i][j] = weight * (synsim*0.2 + typesim*0.2 + consim*0.6);
        	}
        }
        double simpleScore = (request.size() > service.size()) ? hungarian(Hungarian.transpose(matrix)): hungarian(matrix);  
        
        // return score
        if (req.getOutput().getComplexType().size() == 0){
        	// request has no complex type 
        	return simpleScore;
        }else if(request.size() == 0){
        	// request has no simple type 
        	return averageComplex;
        }else{
        	// request has complex type
        	return simpleScore*0.5 + averageComplex*0.5;
        }
        
    } // oupSIm
    
    public static double synSim(String r_opname, String s_opname) throws InvalidNGramException, URISyntaxException{
    	double syn = CompareDefinition.getSimilarity(r_opname, s_opname);
    	return syn;
    } //synSim
    
    public static double conSim(IRI r, IRI s, String owlURI) throws IOException, URISyntaxException, OWLOntologyCreationException{
    	
    	double score = 0.0D;
    	// check concept safe or not
    	if (r == null || s == null) return 0.0D;
    	
    	// get concept class
    	OWLClass reqClass = ConceptSimilarity.getConceptClass(r.toString(), owlURI);
    	OWLClass serClass = ConceptSimilarity.getConceptClass(s.toString(), owlURI);
    	
    	if ((reqClass == null) || (serClass == null)) return 0.0D;
    	
    	score = ConceptSimilarity.getConceptSimScore(reqClass, serClass, owlURI);
    	
    	return score;
    	
    } //conSim
    
    public static List<ComplexTypeOBJ> getNestedComplexType(ComplexTypeOBJ obj){
    	List<ComplexTypeOBJ> result = new ArrayList<ComplexTypeOBJ>();
    	result.add(obj);
    	List<ComplexTypeOBJ> subCplex = new ArrayList<ComplexTypeOBJ>();
    	subCplex = obj.getComplexTypes();
    	while (subCplex.size() > 0){
    		List<ComplexTypeOBJ> nextLevel = new ArrayList<ComplexTypeOBJ>();
    		for (ComplexTypeOBJ cplex : subCplex){
    			result.add(cplex);
    			nextLevel.addAll(cplex.getComplexTypes());
    		}
    		subCplex = nextLevel;
    	}
    	return result;
    }
    
    private static List<ComplexTypeOBJ> ToList(Set<ComplexTypeOBJ> set) {
		ArrayList<ComplexTypeOBJ> result = new ArrayList<ComplexTypeOBJ>();
		result.addAll(set);
		return result;
	}
    
    public static void main(String args[]) {
		
	}
    	
} // Similarity_new
