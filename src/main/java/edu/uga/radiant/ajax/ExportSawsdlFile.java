package edu.uga.radiant.ajax;
 
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import edu.uga.cs.lumina.discovery.util.ErrorMessage;
import edu.uga.cs.wstool.parser.sawadl.WADLParser;
import edu.uga.cs.wstool.parser.sawsdl.SAWSDLParser;
import edu.uga.cs.wstool.parser.xml.XMLParser;
import edu.uga.radiant.util.RadiantToolConfig;
 
public class ExportSawsdlFile extends ActionSupport{
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private InputStream fileInputStream;
	
	/** default download file name 
     */
	private String fileName;
	
	/** response error message
     */
	private ErrorMessage errorMesg;
	
	/** response error vector
     */
	private Vector<ErrorMessage> vecError;
	
	/** response message type(ex. success, error)
     */
	private String messageType;
 
	public String execute(){
		
	    Logger logger = RadiantToolConfig.getLogger();
	    
		@SuppressWarnings("rawtypes")
		Map session = ActionContext.getContext().getSession();
		
		/*
		if (session.get("login") != "true"){
			session.remove("login");
			session.remove("userID");
			errorMesg = new ErrorMesg();
			errorMesg.setErrormessage("Please login first! if you don't have account, apply a new one. ");
			logError = new Vector<ErrorMesg>();
			logError.add(errorMesg);
			messageType = "error";
			return LOGIN;
		}
		*/
		
		String WsType = "";
		fileName = (String)session.get("wsname");
		
		logger.debug("wsdl parser = " + session.get("wsdlparser"));
		
		XMLParser wsparser = (SAWSDLParser)session.get("wsdlparser");
		WsType = "wsdl";
		if (wsparser == null){
			wsparser = (WADLParser)session.get("wadlparser");
			WsType = "wadl";
		}
		
		if (wsparser == null){
			errorMesg = new ErrorMessage();
			errorMesg.setErrormessage("The session is expired. please login again!");
			vecError = new Vector<ErrorMessage>();
			vecError.add(errorMesg);
			return ERROR;
		}
		
		try {
			if (WsType.equals("wsdl")){
				fileInputStream = new ByteArrayInputStream(XMLParser.updateToXml(((SAWSDLParser)wsparser).getCurrentDocument()));
			}else if (WsType.equals("wadl")){
				fileInputStream = new ByteArrayInputStream(XMLParser.updateToXml(((WADLParser)wsparser).getCurrentDoc()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			//put error message into vecError show on error.jsp
			errorMesg = new ErrorMessage();
			errorMesg.setErrormessage(e.toString());
			vecError = new Vector<ErrorMessage>();
			vecError.add(errorMesg);
			
			// record error log
			logger.error(e.toString());
			
			return ERROR;
		}

	    return SUCCESS;
	}
	
	public InputStream getFileInputStream() {
		return fileInputStream;
	}

	public void setVecError(Vector<ErrorMessage> vecError) {
		this.vecError = vecError;
	}

	public Vector<ErrorMessage> getVecError() {
		return vecError;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}
}
