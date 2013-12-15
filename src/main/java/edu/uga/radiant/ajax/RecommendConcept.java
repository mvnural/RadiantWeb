package edu.uga.radiant.ajax;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.inject.Inject;
import edu.uga.radiantweb.freemarker.ConfigurationFactory;
import freemarker.template.*;
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

    @Inject("freemarkerConfiguration")
    private ConfigurationFactory freemarkerConfig;
	
	@SuppressWarnings("unchecked")
	public String execute() {
		
	    Logger logger = RadiantToolConfig.getLogger();
		OntologyManager mgr = null;
		SortValueMap<SuggestionOBJ, Double> sug = null;
        ArrayList<String> operationIRI;
        ArrayList<String> paramIRI;
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

                if((sug == null) || (sug.size() == 0)){
                    errormsg = "No match suggestion!";
                    return ERROR;
                }else {
                    Map templateModel = new HashMap();
                    List<SuggestionOBJ> suggestions = new ArrayList<SuggestionOBJ>();
                    templateModel.put("suggestions", suggestions);
                    templateModel.put("elementType", EleType);
                    templateModel.put("elementName", EleName);
                    templateModel.put("tabIndex",tabidx);
                    int counter = 0;
                    for (SuggestionOBJ suggestionObject : sug.keySet()) {
                        counter++;
                        if (counter > 15) break;
                        suggestions.add(suggestionObject);
                    }

                    StringWriter buf = new StringWriter();
                    try {
                        /**
                         * For testing purposes only. This should be moved out from here.
                         *
                         * */

                        Template temp = freemarkerConfig.getConfig().getTemplate("RecommendConceptResults.ftl");
                        temp.process(templateModel, buf);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (TemplateException e) {
                        e.printStackTrace();
                    }
                    logger.debug("buf = " + buf.toString());
                    innerHtml = buf.toString();
                }
	        	
			}else{
	        	errormsg = "Error:ontology not loaded or session is expired.";
	        }
			
			

			
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
