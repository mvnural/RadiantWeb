package edu.uga.cs.radiant.struts2.action;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import edu.uga.radiant.util.DataBaseConnection;
import edu.uga.radiant.util.RadiantConstant;
import edu.uga.radiant.util.RadiantToolConfig;

/**
 * @author Long
 * The action class is used to check the user login information
 */
public class Login extends ActionSupport{
  
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String username = null;	
	private String password = null;
	private String checkword = null;
	private String searchWord;
	private String page;
	private String errorMesg;
	private Vector<String> loginError;
	private Vector<String> vecError;
	private String accountType;
	private String messageType;
		  
	@SuppressWarnings("unchecked")
	public String execute()throws Exception{
		
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		String SQL = "";
		DataBaseConnection dbcn = null;
		dbcn = new DataBaseConnection();
		Connection conn = dbcn.getConnection(); 
		loginError = new Vector<String>();
		vecError = new Vector<String>();
		username = username.toLowerCase();
		long userID = -1;
		
		SQL = "Select id, pass, type from user where username = ? ;";	
		
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, username);		
			// get result
			rs = pstmt.executeQuery();
		  
			// get result
			rs.next();
			if (rs.getRow() == 0) {
				errorMesg = "No such account!";
				loginError.add(errorMesg);
				vecError.add(errorMesg);
				messageType = "error";
				dbcn.close();
				return ERROR;
			}
			userID = rs.getLong(1);
			checkword = rs.getString(2);
			accountType = rs.getString(3);
			if (checkword == null){
				errorMesg = "The username or password is incorrect!";
				loginError.add(errorMesg);
				vecError.add(errorMesg);
				messageType = "error";
				dbcn.close();
				return ERROR;
			}
			
			if((!getPassword().equals(checkword))){
				
				errorMesg = "The username or password is incorrect!";
				loginError.add(errorMesg);
				vecError.add(errorMesg);
				messageType = "error";
				dbcn.close();
				return ERROR;
			
			}else{
				
				@SuppressWarnings("rawtypes")
				Map session = ActionContext.getContext().getSession();
				session.put("login", "true");
				session.put("userID", userID);
				session.put("username", username);
				session.put("accountType", accountType);
				
				if (accountType.equals(RadiantConstant.ACCOUNT_ADMIN)){
					
					// load admin page
					
				}else {
					
					// load regular page
					
				}
				
				dbcn.close();
				return SUCCESS;
			}				
		}catch (Exception e) {
			  e.printStackTrace();
			  errorMesg = e.toString();
			  loginError.add(errorMesg);
			  vecError.add(errorMesg);
			  messageType = "error";
			  dbcn.close();
			  
			  // record error log				
			  Logger logger = RadiantToolConfig.getLogger();
			  logger.error(e.toString());
			  
			  return ERROR;
		}
	}
  
	public String getUsername(){
		return username;
	}
  
	public void setUserID(String username){
		this.username = username;  
	}
  
	public String getPassword(){
		return password;  
	}	  

	public void setPassword(String password){	
		this.password = password;    
	}
	
	public String getPage(){
		return page;  
	}	  

	public void setPage(String page){	
		this.page = page;    
	}
	
	public String getSearchWord(){
		return searchWord;  
	}	  

	public void setSearchWord(String searchWord){	
		this.searchWord = searchWord;    
	}
	
	public Vector<String> getLoginError() {  
		return loginError;      
	}
	    
	public void setLoginError(Vector<String> loginError) {     
		this.loginError = loginError;      
	}
	
	public void setVecError(Vector<String> vecError) {
		this.vecError = vecError;
	}

	public Vector<String> getVecError() {
		return vecError;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getMessageType() {
		return messageType;
	}
	
}