package edu.uga.cs.lumina.discovery.action;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import edu.uga.cs.lumina.discovery.util.ErrorMessage;
import edu.uga.radiant.util.DataBaseConnection;
import edu.uga.radiant.util.QueryManager;
import edu.uga.radiant.util.RadiantToolConfig;

/**
 * 
 * @author Long
 * 
 * this Struts2 action is used to get the wsdl or sawsdl file from database to client(user)
 */
public class DownloadWSDL extends ActionSupport{
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * the file input stream
	 */
	private InputStream fileInputStream;
	
	/**
	 * file name
	 */
	private String fileName;
	
	/**
	 * error message
	 */
	private ErrorMessage error;
	
	/**
	 * the collection of error message
	 */
	private Vector<ErrorMessage> vecError;
 
	/**
	 * The error message
	 */
	private String errorMesg = "";
	
	/**
	 * The collection of login error message
	 */
	private Vector<String> loginError;
	
	/**
	 * indicate the message type, ex. success or error
	 */
	private String messageType = "";
	
	/**
	 * the Struts2 action
	 */
	public String execute(){
		
		// the database connection
		DataBaseConnection dbcn = null;
		dbcn = new DataBaseConnection();
		Connection conn = dbcn.getConnection();
		
		@SuppressWarnings("rawtypes")
		Map session = ActionContext.getContext().getSession();
		
		if (session.get("login") != "true"){
			session.remove("login");
			session.remove("userID");
			session.remove("accountType");
			errorMesg = "Please login first! if you don't have account, apply a new one. ";
			loginError.add(errorMesg);
			messageType = "error";
			dbcn.close();
			return LOGIN;
		}
		
		try {
			
			int pt = fileName.lastIndexOf(".");
			String name = fileName.substring(0, pt);
			String type = fileName.substring(pt + 1, fileName.length());
			
			String xml = QueryManager.getWSDL(conn, name, type);
			
			fileInputStream = new ByteArrayInputStream(xml.getBytes("utf-8"));
			
		} catch (Exception e) {
			e.printStackTrace();
			error.setErrormessage(e.toString());
			vecError = new Vector<ErrorMessage>();
			vecError.add(error);
			dbcn.close();
			
			// record error log
			Logger logger = RadiantToolConfig.getLogger();
			logger.error(e.toString());
			
			return ERROR;
		}
		
		dbcn.close();
	    return SUCCESS;
	}
	
	public InputStream getFileInputStream() {
		return fileInputStream;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
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

	public void setLoginError(Vector<String> loginError) {
		this.loginError = loginError;
	}

	public Vector<String> getLoginError() {
		return loginError;
	}
}