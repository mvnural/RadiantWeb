package edu.uga.radiant.ajax;

import java.util.Map;

import org.apache.log4j.Logger;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import edu.uga.radiant.ontology.OntologyManager;
import edu.uga.radiant.util.RadiantToolConfig;

public class SetOntologyOptions extends ActionSupport {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String errormsg;
	private String operation;
	private String param;
	
	@SuppressWarnings("unchecked")
	public String execute() {
		
	    Logger logger = RadiantToolConfig.getLogger();
		@SuppressWarnings("rawtypes")
		Map session = ActionContext.getContext().getSession();
		OntologyManager mgr = (OntologyManager) session.get("OntologyManager");
		
		errormsg = "";
		if (operation == null) operation = "";
		if (param == null) param = "";
		operation = operation.trim();
		param = param.trim();
		
		boolean found = false;
        if(operation.indexOf("http:", 0) != -1){	// IRI
        	if (mgr != null){
        		if(mgr.getAllOWLclass().get(operation) != null){
                    // store operation concept to session
                    session.put("operation", operation);
                    found = true;
                    logger.debug("operation found");
                }
        	}
        }else{	// Label
        	if (mgr != null){
        		if (mgr.getAllClassLabels().get(operation) != null){
        			// store operation concept to session  
                    session.put("operation", mgr.getAllClassLabels().get(operation).getIRI().toString());
                    found = true;
                    logger.debug("operation found");
        		}
        	}
        }
        if(!found){
        	errormsg = errormsg + "Error: Operation label not found in the whole ontology \n";
        }
		
        
        found = false;
        if(param != null && param.length() != 0 ){
            
            if(param.indexOf("http:", 0) != -1){	//IRI
            	if (mgr != null){
            		if(mgr.getAllOWLclass().get(param) != null){
                        // store operation concept to session
                        session.put("parameter", param);
                        found = true;
                        logger.debug("param found");
                    }
            	}
            }else{	// Label
            	if (mgr != null){
            		if (mgr.getAllClassLabels().get(param) != null){
            			// store operation concept to session  
                        session.put("parameter", mgr.getAllClassLabels().get(param).getIRI().toString());
                        found = true;
                        logger.debug("param found");
            		}
            	}
            }
            if(!found){
                errormsg = errormsg + "Error: Parameters labels not found in ontology";
            }
            if ((operation != null) && (param != null)){
		    	session.put("operation", operation);
		    	session.put("parameter", param);
		    }else{
		    	errormsg = errormsg + "operation or parameter is empty";
		    }
	    }
        return SUCCESS;
	}

	public void setErrormsg(String errormsg) {
		this.errormsg = errormsg;
	}

	public String getErrormsg() {
		return errormsg;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getOperation() {
		return operation;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getParam() {
		return param;
	}
	
	
	

}
