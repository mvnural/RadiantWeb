package edu.uga.radiant.ajax;

import java.util.Map;

import org.apache.log4j.Logger;
import org.jdom.Document;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import edu.uga.cs.wstool.parser.sawadl.WADLParser;
import edu.uga.cs.wstool.parser.sawsdl.SAWSDLParser;
import edu.uga.cs.wstool.parser.xml.XMLParser;
import edu.uga.radiant.printTree.LoadXml;
import edu.uga.radiant.util.RadiantToolConfig;

public class LoadWSXml extends ActionSupport {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String type;
	private String errormsg;
	private String innerXmlHtml;
	
	public String execute() {
		
	    Logger logger = RadiantToolConfig.getLogger();
		StringBuffer buf = new StringBuffer();
		Document doc = null;
		errormsg = "";
        
		@SuppressWarnings("rawtypes")
		Map session = ActionContext.getContext().getSession();
		XMLParser wsparser = (SAWSDLParser)session.get("wsdlparser");
		if (wsparser != null){
			doc = wsparser.getDoc();
			type = "wsdl";
		}else{
			wsparser = (WADLParser)session.get("wadlparser");
			if (wsparser != null){
				doc = wsparser.getDoc();
				type = "wadl";
			}else{
				errormsg = "session expired or not login";
			}
		}
		if (type == null) type = "";
		logger.debug("type = " + type);
		if (!errormsg.equals("")){
			return ERROR;
		}
		
        try {
        	
        	buf.append("<div style=\"width:1024px;\">");
            if (type.equals("wsdl")){
            	LoadXml.loadWSDLXml(doc, buf);
            }else if (type.equals("wadl")){
            	LoadXml.loadWADLXml(doc, buf);
            }
            buf.append("</div>");
    		innerXmlHtml = buf.toString();
        	
        } catch (Exception e) {
			e.printStackTrace();
			errormsg = e.toString();
			// record error log
			logger.error(e.toString());
			return ERROR;
		}
	    return SUCCESS;
	
	}
	
	public void setErrormsg(String errormsg) {
		this.errormsg = errormsg;
	}

	public String getErrormsg() {
		return errormsg;
	}

	public void setInnerXmlHtml(String innerXmlHtml) {
		this.innerXmlHtml = innerXmlHtml;
	}

	public String getInnerXmlHtml() {
		return innerXmlHtml;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
