package edu.uga.cs.radiant.struts2.action;
import java.util.Map;
import java.util.Vector;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.lang.ClassNotFoundException;
public class Createuser extends ActionSupport
{
private String name;
private String email;
private String organization = null;
private String userId;
private String password;
private String retypepassword;
private String errorMesg;
private Vector<String> loginError;
private Vector<String> vecError;
private String accountType;
private String messageType;
public Createuser()
{
}
public String getName() {
return name;
}
public void setName(String name) {
this.name = name;
}
public String getEmail() {
return email;
}
public void setEmail(String email) {
this.email = email;
}
public String getOrganization() {
return organization;
}
public void setOrganization(String organization) {
this.organization = organization;
}
public String getUserId() {
return userId;
}
public void setUserId(String userId) {
this.userId = userId;
}
public String getPassword() {
return password;
}
public void setPassword(String password) {
this.password = password;
}
public String getRetypepassword() {
return retypepassword;
}
public void setRetypepassword(String retypepassword) {
this.retypepassword = retypepassword;
}
public String execute()
{
Connection connection = null;
Statement statement = null;
loginError = new Vector<String>();
vecError = new Vector<String>();

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

else if(userId.length() == 0)
{
errorMesg = "Username should not be Empty!";
loginError.add(errorMesg);
vecError.add(errorMesg);
messageType = "error";
return ERROR;
}
else if(password.length() == 0)
{
errorMesg = "password should not be Empty!";
loginError.add(errorMesg);
vecError.add(errorMesg);
messageType = "error";
return ERROR;
}
else if (!password.equalsIgnoreCase(retypepassword))
{
errorMesg = "Two Passwords Did not Match!";
loginError.add(errorMesg);
vecError.add(errorMesg);
messageType = "error";
return ERROR;
}
//	else
//	{
//	System.out.println("Name -> " + name);	
//	System.out.println("EMail -> " + email);	
//	System.out.println("Organization -> " + organization);	
//	System.out.println("UserId -> " + userId);
//	System.out.println("Password -> " + password);
//	System.out.println("RetypePassword -> " + retypepassword);
//	return "success";
//	}
try
{
//System.out.println("Connection to Driver");
Class.forName("org.hsqldb.jdbcDriver");
//System.out.println("Connection to Driver Successfull");
//System.out.println("Connection to DataBase");
connection = DriverManager.getConnection("jdbc:hsqldb:file:/home/aravindk/Desktop/HSQLDB/radiantNew", "SA", "");
//System.out.println("Connection to DataBase Successfull");
String query = "select USERNAME from USER where USERNAME = ?"; 
PreparedStatement statm = connection.prepareStatement(query);
statm.setString(1, userId);
ResultSet rs = statm.executeQuery();
if(rs.next())
{
errorMesg = "Username Already Exists!";
loginError.add(errorMesg);
vecError.add(errorMesg);
messageType = "error";
return ERROR;
}
String sql= "INSERT INTO USER (USERNAME, PASS, TYPE, NAME, EMAIL, ORGANIZATION) VALUES ('"+userId+"', '"+password+"', 'student', '"+name+"', '"+email+"', '"+organization+"')";
int action = connection.createStatement().executeUpdate(sql);
connection.setAutoCommit(true);
if(action >= 1)
{
//System.out.println("Data Saved in DataBase");
}
else
{
//System.out.println("Cannot Save Data in DB");
}
}
catch(ClassNotFoundException error)
{
System.out.println("Error " + error.getMessage());
errorMesg = "Database Error";
loginError.add(errorMesg);
vecError.add(errorMesg);
messageType = "error";
return ERROR;
}
catch(SQLException error)
{
//System.out.println("Error " + error.getMessage());
errorMesg = "Database Error";
loginError.add(errorMesg);
vecError.add(errorMesg);
messageType = "error";
return ERROR;
}
finally
{
if(connection != null)
try
{
connection.close();
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
