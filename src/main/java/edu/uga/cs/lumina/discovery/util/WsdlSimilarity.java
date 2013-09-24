package edu.uga.cs.lumina.discovery.util;


import static ontology.similarity.Hungarian.hungarian;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import ontology.similarity.Hungarian;
import ontology.similarity.TypeCompatibility;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import edu.uga.cs.wstool.parser.sawsdl.SimpleTypeOBJ;
import edu.uga.radiant.stringmetrics.CompareDefinition;
import edu.uga.radiant.stringmetrics.InvalidNGramException;
import edu.uga.radiant.util.RadiantToolConfig;

public class WsdlSimilarity {

	private static double[] w = {0.3, 0.3, 0.1, 0.3};


    public static double oPSim(WsOpReader req, WsOpReader ws) throws Exception {
    	
	    Logger logger = RadiantToolConfig.getLogger();
    	//out.println(w[0] + "*" + inpSim(r.inputs, s.inputs) + " + " + w[1] + "*" + oupSim(r.outputs, s.outputs) + " + " + w[2] + "*" + synSim(r.operationName, s.operationName) + " + " + w[3] + "*" + conSim(r.functionality, s.functionality));
    	double inSim = inpSim(req, ws);
    	double outSim = oupSim(req, ws);
    	double nameSyn = synSim(req.getOperationName(), ws.getOperationName());
    	double funSim = defSim(req.getFunctionalityDes(), ws.getFunctionalityDes());
    	logger.debug("inSim = " + inSim);
    	logger.debug("outSim = " + outSim);
    	logger.debug("nameSyn = " + nameSyn);
    	logger.debug("funSim = " + funSim);
    	
    	return w[0] * inSim + w[1] * outSim + w[2] * nameSyn + w[3] * funSim;
    } // oPSim 

    public static double inpSim(WsOpReader req, WsOpReader ws) throws Exception{
    	
    	List<SimpleTypeOBJ> request = WsOpReader.getAllInputSimpleType(req);
    	List<SimpleTypeOBJ> service = WsOpReader.getAllInputSimpleType(ws);
    	
    	if (request.size() == 0 | service.size() == 0 ) return 0.0;
    	double[][] matrix = new double[request.size()][service.size()];
        for (int i = 0; i < request.size(); i++) {
        	for (int j = 0; j < service.size() ; j++) {
                double synsim = synSim(request.get(i).getName(), service.get(j).getName());
        		double consim = defSim(request.get(i).getDescription(), service.get(j).getDescription());
                double typesim = TypeCompatibility.CompatibilityMatch(request.get(i).getType(), service.get(j).getType());
        	    if (service.get(j).isRequired()){
                    matrix[i][j] = synsim*0.2 + typesim*0.2 + consim*0.6;
        	    }else{
                    matrix[i][j] = (synsim*0.2 + typesim*0.2 + consim*0.6) * 0.3;
        	    }
            }
        }
        double max = (request.size() > service.size()) ? hungarian(Hungarian.transpose(matrix)): hungarian(matrix);  
        return (max);
    
    } // inpSim
    
    public static double oupSim(WsOpReader req, WsOpReader ws) throws Exception{
    	
    	List<SimpleTypeOBJ> request = WsOpReader.getAllOutputSimpleType(req);
    	List<SimpleTypeOBJ> service = WsOpReader.getAllOutputSimpleType(ws);
    	
    	if (request.size() == 0 | service.size() == 0 ) return 0.0;
    	double[][] matrix = new double[request.size()][service.size()];
        for (int i = 0; i < request.size(); i++) {
        	for (int j = 0; j < service.size() ; j++) {
        		double synsim = synSim(request.get(i).getName(), service.get(j).getName());
        		double consim = defSim(request.get(i).getDescription(), service.get(j).getDescription());
                double typesim = TypeCompatibility.CompatibilityMatch(request.get(i).getType(), service.get(j).getType());
                if (service.get(j).isRequired()){
                    matrix[i][j] = synsim*0.2 + typesim*0.2 + consim*0.6;
        	    }else{
                    matrix[i][j] = (synsim*0.2 + typesim*0.2 + consim*0.6) * 0.3;
        	    }
                matrix[i][j] = synsim*0.2 + typesim*0.2 + consim*0.6;
            }
        }
        double max = (request.size() > service.size()) ? hungarian(Hungarian.transpose(matrix)) : hungarian(matrix);  
        return max;
        
    } // oupSIm
    
    public static double synSim(String r_opname, String s_opname) throws InvalidNGramException, URISyntaxException{
    	double syn = CompareDefinition.getSimilarity(r_opname, s_opname);
    	return syn;
    } //synSim
    
    public static double defSim(String req, String ws) throws IOException, URISyntaxException, OWLOntologyCreationException{
    	
    	double score = 0.0D; 
    	// check concept safe or not
    	if (req == null || ws == null) return 0.0D;
    	if (req.equals("") || ws.equals("")) return 0.0D;
    	
    	score = CompareDefinition.getSimilarity(req, ws);
    	
    	return score;
    	
    } //conSim
    	
} // Similarity_new
