package edu.uga.cs.radiant.struts2.action;

import java.util.Properties;
import java.util.Vector;


import com.opensymphony.xwork2.ActionSupport;
import edu.uga.radiant.util.DataBaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
//import java.sql.Statement;
import java.sql.SQLException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
public class Forgotpassword extends ActionSupport
{
public Forgotpassword()
{
}
private static final long serialVersionUID = 1L;
private String email;
private String errorMesg;
private Vector<String> loginError;
private Vector<String> vecError;
private String accountType;
private String messageType;
public String getEmail() {
return email;
}
public void setEmail(String email) {
this.email = email;
}
public String execute()
{
//Connection connection = null;
//Statement statement = null;
loginError = new Vector<String>();
vecError = new Vector<String>();
ResultSet rs;
PreparedStatement pstmt = null;
DataBaseConnection dbcn = null;
dbcn = new DataBaseConnection();
Connection conn = dbcn.getConnection(); 

if(email.length() == 0)
{
errorMesg = "Email should not be Empty!";
loginError.add(errorMesg);
vecError.add(errorMesg);
messageType = "error";
return ERROR;
}
if(!(email.contains("@") && email.contains(".com")))
{
errorMesg = "Enter a valid Email address!";
loginError.add(errorMesg);
vecError.add(errorMesg);
messageType = "error";
return ERROR;
}
String pass;
try
{
	/*
//System.out.println("Connection to Driver");
Class.forName("org.hsqldb.jdbcDriver");
//System.out.println("Connection to Driver Successfull");
//System.out.println("Connection to DataBase");
connection = DriverManager.getConnection("jdbc:hsqldb:file:/home/aravindk/Desktop/HSQLDB/radiantNew", "SA", "");
//System.out.println("Connection to DataBase Successfull");
 * 
 */
	String query = "select * from USER where EMAIL = ?"; 
	pstmt = conn.prepareStatement(query);
	pstmt.setString(1, email);		
	// get result
	rs = pstmt.executeQuery();
  
	// get result
	rs.next();
	

//PreparedStatement statm = connection.prepareStatement(query);
//statm.setString(1, email);
//rs = statm.executeQuery();
	
if(!rs.next())
{
errorMesg = "Email address does not Exists!";
loginError.add(errorMesg);
vecError.add(errorMesg);
messageType = "error";
return ERROR;
}
else
{
pass = rs.getString("PASS");
}
Properties props = new Properties();
//props.put("mail.smtp.auth", "true");
props.put("mail.smtp.user", "radiantweb@uga.edu");
props.put("mail.smtp.host", "mailgateway.uga.edu");
//props.put("mail.smtp.port", "587");
Session session = Session.getDefaultInstance(props);
try
{
Message message = new MimeMessage(session);
message.setFrom(new InternetAddress("radiantweb@uga.edu"));
message.setRecipients(Message.RecipientType.TO,
InternetAddress.parse("airavind@gmail.com"));
message.setSubject("Radiantweb Password");
message.setText("Your password is " +pass);
Transport.send(message);
System.out.println("Done");
} 
catch (MessagingException e) 
{
throw new RuntimeException(e);
}
}
/*catch(ClassNotFoundException error)
{
System.out.println("Error " + error.getMessage());
errorMesg = "Database Error";
loginError.add(errorMesg);
vecError.add(errorMesg);
messageType = "error";
return ERROR;
}*/
catch(SQLException error)
{
System.out.println("Error " + error.getMessage());
errorMesg = "Database Error";
loginError.add(errorMesg);
vecError.add(errorMesg);
messageType = "error";
return ERROR;
}
finally
{
dbcn.close();
/*
if(connection != null)
try
{

//connection.close();
}
catch(SQLException ignore)
{
}
if(statement != null)
try
{
statement.close();
}
catch(SQLException ignore)
{
}
*/
}
return "success";
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
public String getAccountType() {
return accountType;
}
public void setAccountType(String accountType) {
this.accountType = accountType;
}
public String getMessageType() {
return messageType;
}
public void setMessageType(String messageType) {
this.messageType = messageType;
}
}
