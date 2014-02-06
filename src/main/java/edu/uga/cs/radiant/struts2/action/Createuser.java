package edu.uga.cs.radiant.struts2.action; 
 
import java.util.Vector; 
import com.opensymphony.xwork2.ActionSupport; 
 
import edu.uga.radiant.util.DataBaseConnection; 
 
import java.security.MessageDigest;
import java.sql.Connection; 
import java.sql.PreparedStatement; 
import java.sql.ResultSet; 
import java.sql.SQLException; 
public class Createuser extends ActionSupport 
{ 
private static final long serialVersionUID = 1L; 
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
//    else 
//    { 
//    System.out.println("Name -> " + name);     
//    System.out.println("EMail -> " + email);     
//    System.out.println("Organization -> " + organization);     
//    System.out.println("UserId -> " + userId); 
//    System.out.println("Password -> " + password); 
//    System.out.println("RetypePassword -> " + retypepassword); 
//    return "success"; 
//    } 
try 
{ 
/* 
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
*/ 
String query = "select USERNAME from USER where USERNAME = ?";  
pstmt = conn.prepareStatement(query); 
pstmt.setString(1, userId);         
// get result 
rs = pstmt.executeQuery(); 
 
if(rs.next()) 
{ 
errorMesg = "Username Already Exists!"; 
loginError.add(errorMesg); 
vecError.add(errorMesg); 
messageType = "error"; 
return ERROR; 
} 
String output= jceSha(password);
//System.out.println("Converted String is :" +output);
String sql= "INSERT INTO USER (USERNAME, PASS, TYPE, NAME, EMAIL, ORGANIZATION) VALUES ('"+userId+"', '"+output+"', 'user', '"+name+"', '"+email+"', '"+organization+"')"; 
 
int action = conn.createStatement().executeUpdate(sql); 
conn.setAutoCommit(true); 
if(action >= 1) 
{ 
//System.out.println("Data Saved in DataBase"); 
} 
else 
{ 
//System.out.println("Cannot Save Data in DB"); 
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
//System.out.println("Error " + error.getMessage()); 
errorMesg = "Database Error"; 
loginError.add(errorMesg); 
vecError.add(errorMesg); 
messageType = "error"; 
return ERROR; 
} 
finally 
{ 
     
if(conn != null) 
{ 
try 
{ 
conn.close(); 
} 
catch(SQLException ignore) 
{ 
} 
} 
 
if(pstmt != null) 
{ 
try 
{ 
pstmt.close(); 
} 
catch(SQLException ignore) 
{ 
} 
} 
 
if(dbcn != null) 
{ 
dbcn.close(); 
} 
 
/* 
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
