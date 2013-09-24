package edu.uga.cs.lumina.discovery.action;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import edu.uga.cs.lumina.discovery.util.URLlink;
import edu.uga.radiant.util.DataBaseConnection;
import edu.uga.radiant.util.QueryManager;
import edu.uga.radiant.util.RadiantToolConfig;

/**
 * 
 * @author Long
 *
 * This action is used to show all of the sawsdl or wsdl file which is stored in database and display as a tree view 
 *
 */
public class GetWsTree extends ActionSupport {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * the link inforamtion of wsdl
	 */
	private ArrayList<URLlink> wsdls;
	
	/**
	 * the link inforamtion of sawsdl
	 */
	private ArrayList<URLlink> sawsdls;
	
	/**
	 * the database connection
	 */
	private DataBaseConnection dbcn = new DataBaseConnection();
	
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
	

	public String execute() {
		
		wsdls = new ArrayList<URLlink>();
		sawsdls = new ArrayList<URLlink>();
		Connection conn = dbcn.getConnection();
		
		@SuppressWarnings("rawtypes")
		Map session = ActionContext.getContext().getSession();
		
		if (session.get("login") != "true"){
			session.remove("login");
			session.remove("userID");
			session.remove("accountType");
			errorMesg = "Please login first! if you don't have account, apply a new one. ";
			loginError.add(errorMesg);
			setMessageType("error");
			dbcn.close();
			return LOGIN;
		}
		
		try {
		
			ArrayList<String> wsdlname = QueryManager.getWSDLList(conn);
			ArrayList<String> sawsdlname = QueryManager.getSAWSDLList(conn);
			
			for (String wsdl : wsdlname){
				URLlink link = new URLlink();
				link.setName(wsdl + ".wsdl");
				wsdls.add(link);
			}
			
			for (String sawsdl : sawsdlname){
				URLlink link = new URLlink();
				link.setName(sawsdl + ".sawsdl");
				sawsdls.add(link);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			
			// record error log
			Logger logger = RadiantToolConfig.getLogger();
			logger.error(e.toString());
			
		}
		
		return SUCCESS;
	}


	public void setWsdls(ArrayList<URLlink> wsdls) {
		this.wsdls = wsdls;
	}


	public ArrayList<URLlink> getWsdls() {
		return wsdls;
	}


	public void setSawsdls(ArrayList<URLlink> sawsdls) {
		this.sawsdls = sawsdls;
	}


	public ArrayList<URLlink> getSawsdls() {
		return sawsdls;
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