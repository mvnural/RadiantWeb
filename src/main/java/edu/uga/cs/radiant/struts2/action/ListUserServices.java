package edu.uga.cs.radiant.struts2.action;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import edu.uga.cs.lumina.discovery.util.WSDLSystem;
import edu.uga.cs.wstool.parser.sawsdl.SAWSDLParser;
import edu.uga.radiant.util.DataBaseConnection;
import edu.uga.radiant.util.DataManager;
import edu.uga.radiant.util.QueryManager;
import edu.uga.radiant.util.RadiantToolConfig;

public class ListUserServices extends ActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String errormsg;
	private List<Service> userServices;
	
	
	public String execute() {
		
		errormsg = "";

		
		@SuppressWarnings("rawtypes")
		Map session = ActionContext.getContext().getSession();
		if(session.get("login") == null || !session.get("login").equals("true")){
			errormsg = "User not logged in.";
			return ERROR;
		}
		int userId = (Integer) session.get("userID");
		
		
		try {
			
			userServices = QueryManager.getUserServices(userId);
			
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

	public List<Service> getUserServices() {
		return userServices;
	}

	public void setUserServices(List<Service> userServices) {
		this.userServices = userServices;
	}

	public void setErrormsg(String errormsg) {
		this.errormsg = errormsg;
	}

	public String getErrormsg() {
		return errormsg;
	}

}

