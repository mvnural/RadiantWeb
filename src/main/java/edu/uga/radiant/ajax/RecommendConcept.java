package edu.uga.radiant.ajax;

import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.Logger;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import edu.uga.cs.wstool.parser.sawadl.WADLParser;
import edu.uga.cs.wstool.parser.sawsdl.SAWSDLParser;
import edu.uga.cs.wstool.parser.xml.XMLParser;
import edu.uga.radiant.ontology.OntologyManager;
import edu.uga.radiant.suggestion.SimpleStringMatcher;
import edu.uga.radiant.suggestion.SuggestionOBJ;
import edu.uga.radiant.util.RadiantToolConfig;
import edu.uga.radiant.util.SortValueMap;

/**
 * 
 * 
 * 
 * @author Long
 *
 */
public class RecommendConcept extends ActionSupport {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** response error message
     */
	private String errormsg;
	
	/** WSDL element identifier of SAWSDLParser
     */
	private int EleID;
	
	/** WSDL element name
     */
	private String EleName;
	
	/** WSDL element type (ex. operation, simple type)
     */
	private String EleType;
	
	/** WSDL element description
     */
	private String EleDoc;
	
	/** WSDL operation name
     */
	private String operation;
	
	/** the form option HTML text
     */
	private String innerHtml;
	
	/** the tab index of owl
     */
	private String tabIndex;
	
	@SuppressWarnings("unchecked")
	public String execute() {
		
	    Logger logger = RadiantToolConfig.getLogger();
		OntologyManager mgr = null;
		SortValueMap<SuggestionOBJ, Double> sug = null;
		ArrayList<String> operationIRI = new ArrayList<String>();
		ArrayList<String> paramIRI = new ArrayList<String>();
		@SuppressWarnings("rawtypes")
		Map session = ActionContext.getContext().getSession();
		
		if (tabIndex.equals("0")){
			mgr = (OntologyManager)session.get("OntologyManager");
		}else{
			mgr = (OntologyManager)session.get("OntologyManager" + tabIndex);
		}
		//String type = "";
		XMLParser wsparser = (SAWSDLParser)session.get("wsdlparser");
		if (wsparser != null){
			//type = "wsdl";
		}else{
			wsparser = (WADLParser)session.get("wadlparser");
			if (wsparser != null){
				//type = "wadl";
			}else{
				errormsg = "session expired or not login";
			}
		}
		
		operationIRI = (ArrayList<String>)session.get("operation");
		paramIRI = (ArrayList<String>)session.get("parameter");
		errormsg = "";
		
		StringBuffer buf = new StringBuffer();
		
		try {
			
			if(mgr != null){
				
				if ((EleName.equals("")) && (EleDoc.equals(""))){
					errormsg = "name and typing words are empty";
					return ERROR;
				}
				
				if ((operationIRI == null) || (paramIRI == null)){
					operationIRI = LoadOWL.getOwlOperationSuperClasses(mgr);
					session.put("operation", operationIRI);
					paramIRI = LoadOWL.getOwlParamSuperClasses(mgr);
					session.put("parameter", paramIRI);
				}
				
				SimpleStringMatcher matcher = new SimpleStringMatcher(mgr);
				if(EleType.equalsIgnoreCase("operation") || EleType.equalsIgnoreCase("method")){
	        		sug = matcher.getOpSuggestion(EleName, EleDoc, operationIRI);
	        	}else if(EleType.equalsIgnoreCase("param")){
	            	sug = matcher.getParamSuggestion(EleName, EleDoc, paramIRI);
	        	}else if(EleType.equalsIgnoreCase("complex")){
	            	sug = matcher.getComplexSuggestion(wsparser, EleID, EleName, EleDoc, operation, paramIRI);
	        	}else if(EleType.equalsIgnoreCase("simple")){
	            	sug = matcher.getParamSuggestion(EleName, EleDoc, paramIRI);
	        	}
				
				logger.debug("sug size = " + sug.size());
				int tabidx = Integer.parseInt(tabIndex) + 1;
				
	        	buf.append("<p>Choose the best recommendation for this element : (Owl" + tabidx + " classes)</p>");
	        	buf.append("<div style=\"overflow:auto;\">");
	        	buf.append("<form name=\"recommend concept\" id=\"" + EleType + ":" + EleName + "\">");
	        	
	        	if((sug == null) || (sug.size() == 0)){
	        		errormsg = "No match suggestion!";
	        		return ERROR;
	        	}
	        	
	        	if((sug != null) && (sug.size() != 0)){
	        	    buf.append("<fieldset>");
	        	    int counter = 0;
	        	    for(SuggestionOBJ suggestobj : sug.keySet()){
	        	        counter++;
	        	        if (counter > 15) break;
	        	    	String value = suggestobj.getConceptLabel();
	        	        String description = suggestobj.getConceptDoc();
	        	        String owlid = suggestobj.getConceptIRI();
	        	        String valueId = owlid;
	        	        String score = "";
	        	        if (sug.get(suggestobj).toString().length() > 6){
	        	        	score = sug.get(suggestobj).toString().substring(0, 6);
	        	        }else{
	        	        	score = sug.get(suggestobj).toString();
	        	        }
	        	        
	        	        buf.append("<input id=\"" + value + "\" name=\"option\" value='" + valueId + "' type=\"radio\" title=\"" + description + "\" /><label title=\"" + suggestobj.getConceptDoc() + "\" >" + value + " : <b>" + score + "</b></label><br>");
	            	}
	        	   
	        		buf.append("</form>");
	        		buf.append("</div>");
	        		
	        	}else{
	        		errormsg = "No match suggestion!";
	        	}
	        	
			}else{
	        	errormsg = "Error:ontology not loaded or session is expired.";
	        }
			
			
			logger.debug("buf = " + buf.toString());
			
			
			innerHtml = buf.toString();
			
		} catch (Exception e) {
			e.printStackTrace();
			errormsg = e.toString();
			// record error log
			logger.error(e.toString());
		}
		
		return SUCCESS;
	}

	public void setErrormsg(String errormsg) {
		this.errormsg = errormsg;
	}

	public String getErrormsg() {
		return errormsg;
	}

	public void setEleName(String eleName) {
		EleName = eleName;
	}

	public String getEleName() {
		return EleName;
	}

	public void setEleType(String eleType) {
		EleType = eleType;
	}

	public String getEleType() {
		return EleType;
	}

	public void setEleDoc(String eleDoc) {
		EleDoc = eleDoc;
	}

	public String getEleDoc() {
		return EleDoc;
	}

	public void setInnerHtml(String innerHtml) {
		this.innerHtml = innerHtml;
	}

	public String getInnerHtml() {
		return innerHtml;
	}

	public void setEleID(int eleID) {
		EleID = eleID;
	}

	public int getEleID() {
		return EleID;
	}

	public void setTabIndex(String tabIndex) {
		this.tabIndex = tabIndex;
	}

	public String getTabIndex() {
		return tabIndex;
	}
	
	
}
