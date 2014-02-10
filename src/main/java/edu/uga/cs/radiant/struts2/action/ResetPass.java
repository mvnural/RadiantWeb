package edu.uga.cs.radiant.struts2.action;

import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.lang.Math.random;
import static java.lang.Math.round;
import static org.apache.commons.lang.StringUtils.leftPad;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
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

import com.opensymphony.xwork2.ActionSupport;

import edu.uga.radiant.util.DataBaseConnection;

public class ResetPass extends ActionSupport
{
	
	public ResetPass()
	{
		
	}
	
	private static final long serialVersionUID = 1L;
	private String password;
	private String newpass;
	private String rnewpass;
	private String errorMesg;
	private Vector<String> loginError;
	private Vector<String> vecError;
	private String messageType;
	
	
	
	
	public String execute()
	{
		
		loginError = new Vector<String>();
		vecError = new Vector<String>();
		ResultSet rs= null;
		PreparedStatement pstmt = null;
		DataBaseConnection dbcn = null;
		dbcn = new DataBaseConnection();
		Connection conn = dbcn.getConnection();
		int action = 0;
		
		
		Map session = ActionContext.getContext().getSession();
					
		int userId = (Integer) session.get("userID");
		
		
		if(newpass.length() == 0)
		{
		errorMesg = "Password should not be Empty!";
		loginError.add(errorMesg);
		vecError.add(errorMesg);
		messageType = "error";
		return ERROR;
		}
		
	
		if(!(newpass.equals(rnewpass)))
		{
		errorMesg = "Password does not match!";
		loginError.add(errorMesg);
		vecError.add(errorMesg);
		messageType = "error";
		return ERROR;
		}
		
		
		 String query = "Select pass from user where ID = ? ;";    
		 
		 try 
			{
			 pstmt = conn.prepareStatement(query);
	            pstmt.setInt(1, userId);  
	            rs = pstmt.executeQuery();
	            rs.next();
	            
	            
	            String checkword = rs.getString("pass"); 
	     
	            String check = jceSha(this.password);
	          
	            if (!(checkword.equals(check)))
	            {
	                errorMesg = "Wrong Password Entered!!";
	                loginError.add(errorMesg);
	                vecError.add(errorMesg);
	                messageType = "error";
	                dbcn.close();
	                return ERROR;
	            }
	           
			} 
			catch (SQLException e) 
			{
				e.printStackTrace();
			} 
			 
		String newpass = this.getRnewpass();
		newpass = jceSha(newpass);
		
		String sql= "UPDATE USER SET PASS = '"+newpass+"' WHERE ID = '"+userId+"'";
		
		
		try 
		{
			action = conn.createStatement().executeUpdate(sql);
			conn.setAutoCommit(true);
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		} 
		 
		if(action >= 1) 
		{ 
		//System.out.println("Pass Saved in DataBase"); 
		} 
		else 
		{ 
		//System.out.println("Cannot Save Pass in DB"); 
		} 
		
		
		return "success";
		
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNewpass() {
		return newpass;
	}

	public void setNewpass(String newpass) {
		this.newpass = newpass;
	}

	public String getRnewpass() {
		return rnewpass;
	}

	public void setRnewpass(String rnewpass) {
		this.rnewpass = rnewpass;
	}

	public String getErrorMesg() {
		return errorMesg;
	}

	public void setErrorMesg(String errorMesg) {
		this.errorMesg = errorMesg;
	}

	public Vector<String> getLoginError() {
		return loginError;
	}

	public void setLoginError(Vector<String> loginError) {
		this.loginError = loginError;
	}

	public Vector<String> getVecError() {
		return vecError;
	}

	public void setVecError(Vector<String> vecError) {
		this.vecError = vecError;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public static String jceSha (String pass)
	{
	  try 
	{
	  MessageDigest md = MessageDigest.getInstance("SHA1");
	  String input = pass;
	  md.update(input.getBytes()); 
	  byte[] output = md.digest();
	  //System.out.println();
	  //System.out.println("SHA1(\""+input+"\") =");
	  String result= bytesToHex(output);
	  
	  return(result);
	} 
	  catch (Exception e) 
	  {
	  System.out.println("Exception: "+e);
	  return("Exception Error occured");
	  }
	}
	public static String bytesToHex(byte[] b)
	{
	   char hexDigit[] = {'0', '1', '2', '3', '4', '5', '6', '7',
	                      '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
	   StringBuffer buf = new StringBuffer();
	   for (int j=0; j<b.length; j++) 
	   {
	      buf.append(hexDigit[(b[j] >> 4) & 0x0f]);
	      buf.append(hexDigit[b[j] & 0x0f]);
	   }
	   return buf.toString();
	}
	public static String gen(int length) {
	    StringBuffer sb = new StringBuffer();
	    for (int i = length; i > 0; i -= 12) {
	      int n = min(12, abs(i));
	      sb.append(leftPad(Long.toString(round(random() * pow(36, n)), 36), n, '0'));
	    }
	    return sb.toString();
	  }
	

}
