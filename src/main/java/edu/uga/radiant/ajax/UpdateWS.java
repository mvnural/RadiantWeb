package edu.uga.radiant.ajax;

import java.util.Map;


import org.apache.log4j.Logger;
import org.jdom.Element;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import edu.uga.cs.wstool.parser.sawadl.WADLParser;
import edu.uga.cs.wstool.parser.sawsdl.SAWSDLParser;
import edu.uga.cs.wstool.parser.xml.XMLParser;
import edu.uga.radiant.ontology.OntologyManager;
import edu.uga.radiant.util.RadiantToolConfig;

public class UpdateWS extends ActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String errormsg;
	private String type;
	private int id;
	private String name;
	private String action;
	private String attribute;
	private String value;
	private String label;
	private boolean error = false;
	
	
	@SuppressWarnings("unchecked")
	public String execute() {
		
		String WsType = "";
		errormsg = "";
		if (action == null) action = "";
		
		@SuppressWarnings("rawtypes")
		Map session = ActionContext.getContext().getSession();
		XMLParser wsparser = (SAWSDLParser)session.get("wsdlparser");
		WsType = "wsdl";
		if (wsparser == null){
			wsparser = (WADLParser) session.get("wadlparser");
			WsType = "wadl";
		}
		OntologyManager mgr = (OntologyManager)session.get("OntologyManager");
		
		try {
			
			System.out.println("ws type = " + WsType);
			System.out.println("id = " + id);
			System.out.println("name = " + name);
			System.out.println("type = " + type);
			System.out.println("action = " + action);
			System.out.println("attribute = " + attribute);
			System.out.println("value = " + value);
			
			if (WsType.equals("wsdl")){
				String msg = "";
				Element ele = ((SAWSDLParser) wsparser).getAnnotationObjectMap().get(id).getElement();
				OWLClass cls = mgr.getConceptClass(value);
				if (cls != null){
					label = mgr.getClassLabel(cls);
				}else{
					label = "";
				}
				if (action.equals("remove")){
					if (type.equals("operation")){
						// change the jdom element if it is a wsdl operation
						msg = ((SAWSDLParser) wsparser).removeSAWSDLOperationAnnotation(ele, attribute, value);
					}else{
						// change the jdom element
						msg = ((SAWSDLParser) wsparser).removeSAWSDLAnnotation(ele, attribute, value);
					}
					if (!msg.contains("Success")){
						// if it has an error
						errormsg = msg;
						return ERROR;
					}else{
						// change the annotation object 
						((SAWSDLParser) wsparser).getAnnotationObjectMap().get(id).setSemanticConcept(null);
					}
					session.put("wsdlparser", wsparser);
				}else if (action.equals("add")){
					if (attribute.equals("modelReference")){
						if (type.equals("operation")){
							msg = ((SAWSDLParser) wsparser).annotateWSDLOperaion(ele, attribute, value, SAWSDLParser.sawsdlNS);
						}else if (type.equals("complex")){
							msg = ((SAWSDLParser) wsparser).annotateWSDLComplexType(ele, attribute, value, SAWSDLParser.sawsdlNS);
						}else if (type.equals("simple")){
							msg = ((SAWSDLParser) wsparser).annotateWSDLSimpleType(ele, attribute, value, SAWSDLParser.sawsdlNS);
						}
						if (msg.contains("Success")){
							// change the annotation object 
							((SAWSDLParser) wsparser).getAnnotationObjectMap().get(id).setSemanticConcept(IRI.create(value));
						}
					}else if (attribute.equals("liftingSchemaMapping")){
						msg = ((SAWSDLParser) wsparser).annotateWSDLSimpleType(ele, attribute, value, SAWSDLParser.sawsdlNS);
					}else if (attribute.equals("loweringSchemaMapping")){
						msg = ((SAWSDLParser) wsparser).annotateWSDLSimpleType(ele, attribute, value, SAWSDLParser.sawsdlNS);
					}
					if (!msg.contains("Success")){
						errormsg = msg;
						return ERROR;
					}
					session.put("wsdlparser", wsparser);
				}
			}else if (WsType.equals("wadl")){
				OWLClass cls = mgr.getConceptClass(value);
				if (cls != null){
					label = mgr.getClassLabel(cls);
				}else{
					label = "";
				}
				String msg = "";
				if (action.equals("remove")){
					Element ele = null;
					
					// remove annotation
					if (type.equals("param")){
						Map<String, Element> paramMap = ((WADLParser) wsparser).getParams();
						ele = paramMap.get(name);
					}else if (type.equals("method")){
						String method = "";
						if (name.indexOf(":") != -1){
							method = name.substring(0, name.indexOf(":"));
						}else{
							method = name;
						}
						ele = ((WADLParser) wsparser).getMethod(method);
					}
					msg = ((WADLParser) wsparser).removeSAWADLAnnotation(ele, attribute, value);
					//System.out.println("remove msg = " + msg);
					if (!msg.contains("Success")){
						errormsg = msg;
						return ERROR;
					}
					
					// update wsparser
					session.put("wadlparser", wsparser);
					
				}else if (action.equals("add")){
					
					// annotate element
					if (type.equals("param")){
						msg = ((WADLParser) wsparser).annotateWADLParam(name, attribute, value);
					}else if (type.equals("method")){
						String method = "";
						if (name.indexOf(":") != -1){
							method = name.substring(0, name.indexOf(":"));
						}else{
							method = name;
						}
						msg = ((WADLParser) wsparser).annotateWADLMethod(method, attribute, value);
					}
					//System.out.println("add msg = " + msg);
					if (!msg.contains("Success")){
						errormsg = msg;
						return ERROR;
					}
					
					// update wsparser
					session.put("wadlparser", wsparser);
				}
			}
			return SUCCESS; 
			
		}catch (Exception e) {
			e.printStackTrace();
			errormsg = e.toString();
			// record error log
			Logger logger = RadiantToolConfig.getLogger();
			logger.error(e.toString());
			return ERROR;
			
		}
		
	}
	
	public void setError(boolean error) {
		this.error = error;
	}
	
	public boolean isError() {
		return error;
	}

	public void setErrormsg(String errormsg) {
		this.errormsg = errormsg;
	}

	public String getErrormsg() {
		return errormsg;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getAction() {
		return action;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}

