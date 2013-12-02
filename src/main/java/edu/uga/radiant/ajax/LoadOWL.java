package edu.uga.radiant.ajax;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import edu.uga.radiant.ontology.OntologyManager;
import edu.uga.radiant.printTree.LoadOWLTree;
import edu.uga.radiant.util.RadiantToolConfig;

public class LoadOWL extends ActionSupport {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** response error message
     */
	private String errormsg;
	
	/** owl file URL or file name, get from annotation page
     */
	private String owlloc;
	
	/** to distinguish the page for discovery page or annotation page
     */
	private String page;
	
	/** the OWL information HTML text
     */
	private String ontologyinfo;
	
	/** the OWL treeview HTML text
     */
	private String classes_hierarchy;
	//private String property_hierarchy;
	
	/** the temporary OWL file from client
     */
	private File OWLFile;
	
	/** the active input tab index of owl
     */
	private String tabIndex;
	
	@SuppressWarnings("unchecked")
	public String execute() {
		
	    Logger logger = RadiantToolConfig.getLogger();
		String extension = ".owl";
		String extension1 = ".OWL";
		StringBuffer buf = new StringBuffer();
		
		OntologyManager mgr = null;
		OWLOntology ontology = null;
        String owlPath = "";
        //String importPath = "";
		
		@SuppressWarnings("rawtypes")
		Map session = ActionContext.getContext().getSession();
		//String owlPath = (String)session.get("OWLPath");
		//String importPath = (String) session.get("OWLImportPath");
        String importURL = (String) session.get("OWLImportURL");
        errormsg = "";
        if (page == null) page = "";
        
        // get contextPath ex.Radiant
		HttpServletRequest req = ServletActionContext.getRequest();
		String contextPath = req.getContextPath().split("/")[1];
        
		logger.debug("page = " + page);
		logger.debug("owlloc = " + owlloc);
		if (OWLFile != null) logger.debug("OWLFile size = " + OWLFile.getTotalSpace());
		
		try {
			
			mgr = null;
			if (owlloc.indexOf("http:") != -1){	
	        	
				importURL = owlloc;
	        	mgr = OntologyManager.getInstance(importURL);
	        	session.put("OntologyManager", mgr);
			
			}else{
				
	        	if (OWLFile != null){
	        		
	        	    logger.debug("owlloc = " + owlloc);
	        	    logger.debug("tabIndex = " + tabIndex);
	        		
	        		if (owlloc.endsWith(extension) || owlloc.endsWith(extension1)){
	        			
	        			int end = LoadOWL.class.getResource("").toString().indexOf(contextPath);
	    				String fullFileURL = LoadOWL.class.getResource("").toString().substring(0, end) + contextPath + "/" + "XMLBox/OWLBox/";
	    				
	    				File owlfile = new File(new URI(fullFileURL + owlloc));
	    				FileCopy(OWLFile, owlfile);
	    				
	    				owlPath = fullFileURL + owlloc;
	    				session.put("OWLPath", owlPath);
	    				logger.debug("THe owl path is : * " + owlPath);
	    		        
	    				mgr = OntologyManager.getInstance(owlPath);
	    				if (tabIndex.equals("0")){
	    					session.put("OntologyManager", mgr);
	    				}else{
	    					session.put("OntologyManager" + tabIndex, mgr);
	    				}
	    				
	        		}else{
	        			errormsg = "File is not owl file.";
	        		}
	        	}else{
	        		errormsg = "Session is expired and you need to load ontology again.";
	        	}
	        }
			logger.debug("errormsg = " + errormsg);
			logger.debug("mgr = " + mgr);
			
			//LuceneIndex owlIndex = new LuceneIndex(mgr);
			//session.put("LuceneIndex", owlIndex);
			
			if (errormsg.equals("") && (mgr != null)){
				ontology = mgr.getOntology();
		    	String ontId = ontology.getOntologyID().getOntologyIRI().getFragment();
		    	if (ontId == null) ontId = "";
		    	session.remove("operation");
		    	session.remove("parameter");
		    	if (page.equals("wsdl")){
		    		ontologyinfo = "<span style=\"padding:2px;vertical-align: middle;\"> Search: </span><div style=\"padding-bottom:4px;display: inline;vertical-align: middle;margin-top: auto;margin: auto;\"><input type=\"text\" id=\"ontsearchterm\" name=\"ontsearchterm\" class=\"ui-corner-all ui-autocomplete-input\" style=\"vertical-align: middle;\" onkeydown=\"getList(this.value);\" autocomplete=\"off\" role=\"textbox\" aria-autocomplete=\"list\" aria-haspopup=\"true\"><img id=\"searchontology\" src=\"styles/images/search.gif\" alt=\"\" style=\"padding-left: 5px; height: 20px; vertical-align: middle;\" onclick=\"searchOntology();\"></div><span id=\"setoptions\" class=\"ui-button ui-corner-all\" style=\"color:white;background:#616D7E;padding: 6px;margin-left:3px;margin-right:3px;float: right;margin: auto;\">Set Ontology Options</span>";
		    		//ontologyinfo = "<span style=\"width:25%;float:left;padding:2px\"> Search: </span><div style=\"padding-bottom:4px;float:left\"><input type=\"text\" id=\"ontsearchterm\" name=\"ontsearchterm\" class=\"ui-corner-all\" style=\"float:left;\" onkeydown=\"getList(this.value);\" /><img id=\"searchontology\" src=\"styles/images/search.gif\" alt=\"\" style=\"padding-right: 5px; height: 20px; padding-bottom: 2px;\" onclick=\"searchOntology();\" /></div><span id=\"setoptions\" class=\"ui-button ui-corner-all\" style=\"color:white;background:#616D7E;padding:3px;margin-left:3px;margin-right:3px;\" >Set Options</span></div>";
			    }else{
		    		ontologyinfo = "<span style=\"width:40%;float:left;padding:2px\">" + ontId + "</span><div style=\"padding-bottom:4px;float:left\"><input type=\"text\" id=\"ontsearchterm\" name=\"ontsearchterm\" class=\"ui-corner-all\" style=\"float:left;\" onkeydown=\"getList(this.value);\" /><img id=\"searchontology\" src=\"styles/images/search.gif\" alt=\"\" style=\"padding-right: 5px; height: 20px; padding-bottom: 2px;\" onclick=\"searchOntology();\" /></div><span id=\"setoptions\" class=\"ui-button ui-corner-all\" style=\"color:white;background:#616D7E;padding:3px;margin-left:3px;margin-right:3px;\" >Set Options</span></div>";
			    }
		    	LoadOWLTree.drawTree(buf, mgr);
		    	classes_hierarchy = buf.toString();	
		    	logger.debug("classes_hierarchy = " + classes_hierarchy);
		    	
			}else{
				if (errormsg.equals("")) errormsg = "Ontology load fails.";
				if (tabIndex.equals("0")){
					session.remove("OntologyManager");
					logger.debug("remove OntologyManager");
				}else{
					session.remove("OntologyManager" + tabIndex);
					logger.debug("remove OntologyManager" + tabIndex);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			errormsg = e.toString();
			logger.error(e.toString());
		}
		
		return SUCCESS;
	}

	private void FileCopy(File oWLFile, File owlfile) throws IOException {
		InputStream in = new FileInputStream(oWLFile);
		OutputStream out = new FileOutputStream(owlfile);
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0){
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}
	
	public static ArrayList<String> getOwlOperationSuperClasses(OntologyManager mgr){
		ArrayList<String> operationIRI = new ArrayList<String>();
		RadiantToolConfig config = new RadiantToolConfig();
		operationIRI.add(config.getOperationConcept());
		operationIRI.add(config.getOperationConcept2());
		boolean operationFound = false;
		for(String op : operationIRI){
			if (mgr.getOntology().containsClassInSignature(IRI.create(op))){
				operationFound = true;
			}
		}
		if (operationFound == false){
			operationIRI = new ArrayList<String>();
		}
		return operationIRI;
	}
	
	public static ArrayList<String> getOwlParamSuperClasses(OntologyManager mgr){
		ArrayList<String> paramIRI = new ArrayList<String>();
		RadiantToolConfig config = new RadiantToolConfig();
		paramIRI.add(config.getParameterConcept());
		boolean paramFound = false;
		for (String param : paramIRI){
			if (mgr.getOntology().containsClassInSignature(IRI.create(param))){
				paramFound = true;
			}
		}
		if (paramFound == false){
			paramIRI = new ArrayList<String>();
		}
		return paramIRI;
	}

	public void setErrormsg(String errormsg) {
		this.errormsg = errormsg;
	}

	public String getErrormsg() {
		return errormsg;
	}

	public void setClasses_hierarchy(String classes_hierarchy) {
		this.classes_hierarchy = classes_hierarchy;
	}

	public String getClasses_hierarchy() {
		return classes_hierarchy;
	}

	public void setOntologyinfo(String ontologyinfo) {
		this.ontologyinfo = ontologyinfo;
	}

	public String getOntologyinfo() {
		return ontologyinfo;
	}

	public void setOwlloc(String owlloc) {
		this.owlloc = owlloc;
	}

	public String getOwlloc() {
		return owlloc;
	}

	public void setOWLFile(File oWLFile) {
		OWLFile = oWLFile;
	}
	/*
	public void setProperty_hierarchy(String property_hierarchy) {
		this.property_hierarchy = property_hierarchy;
	}

	public String getProperty_hierarchy() {
		return property_hierarchy;
	}
	*/
	public void setPage(String page) {
		this.page = page;
	}

	public String getPage() {
		return page;
	}

	public void setTabIndex(String tabIndex) {
		this.tabIndex = tabIndex;
	}

	public String getTabIndex() {
		return tabIndex;
	}

	// to prevent the file is inside reply message
	//public File getOwlFile() {
	//	return owlFile;
	//}
		
}
