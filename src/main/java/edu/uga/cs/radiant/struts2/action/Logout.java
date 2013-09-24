package edu.uga.cs.radiant.struts2.action;

import java.util.*;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ActionContext;


/**
 * @author Long
 * The action class is used to clear the user's login information and logout
 */
public class Logout extends ActionSupport{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String execute() throws Exception {   
		
		@SuppressWarnings("rawtypes")
		Map session = ActionContext.getContext().getSession();
		session.remove("login");
		session.remove("userID");
		session.remove("username");
		session.remove("accountType");

		
		return SUCCESS; 	
	}
	
}
