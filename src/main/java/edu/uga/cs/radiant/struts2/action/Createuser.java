package edu.uga.cs.radiant.struts2.action;
import java.util.Map;
import java.util.Vector;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.lang.ClassNotFoundException;
public class Createuser extends ActionSupport
{
private String name;
private String email;
private String organization;
private String userId;
private String password;
private String retypepassword;
public Createuser()
{
}
//	public Createuser( String name, String email, String organization, String userId, String password, String retypepassword)
//	{
//	
//	this.userId = userId;
//	this.name = name;
//	this.email = email;
//	this.password = password;
//	this.retypepassword = retypepassword;
//	}
//	
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
try
{
System.out.println("Connection to Driver");
Class.forName("org.hsqldb.jdbcDriver");
System.out.println("Connection to Driver Successfull");
System.out.println("Connection to DataBase");
connection = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/", "SA", "");
System.out.println("Connection to DataBase Successfull");
}
catch(ClassNotFoundException error)
{
System.out.println("Error " + error.getMessage());
}
catch(SQLException error)
{
System.out.println("Error " + error.getMessage());
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
System.out.print("Password -> " + password);
return "success";
}
}
