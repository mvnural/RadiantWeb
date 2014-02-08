package edu.uga.cs.radiant.struts2.action;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.lang.Math.random;
import static java.lang.Math.round;
import static org.apache.commons.lang.StringUtils.leftPad;
import java.util.Properties;
import java.util.Vector;
import com.opensymphony.xwork2.ActionSupport;
import edu.uga.radiant.util.DataBaseConnection;
import java.security.MessageDigest;
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
if(!(email.contains("@") && ( email.contains(".com") || email.contains(".edu") || email.contains(".org"))))
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
    //rs.next();
    
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
//pass = rs.getString("PASS");
pass= gen(8);
String newpass= jceSha(pass);
String sql= "UPDATE USER SET PASS = '"+newpass+"' WHERE EMAIL = '"+email+"'";
int action = conn.createStatement().executeUpdate(sql); 
conn.setAutoCommit(true); 
if(action >= 1) 
{ 
System.out.println("Pass Saved in DataBase"); 
} 
else 
{ 
System.out.println("Cannot Save Pass in DB"); 
} 
}


Properties props = new Properties();
//props.put("mail.smtp.auth", "true");
props.put("mail.smtp.user", "radiantweb@gmail.com");
props.put("mail.smtp.host", "mailgateway.uga.edu");
//props.put("mail.smtp.port", "587");
Session session = Session.getDefaultInstance(props);
try
{
Message message = new MimeMessage(session);
message.setFrom(new InternetAddress("radiantweb@gmail.com"));
message.setRecipients(Message.RecipientType.TO,
InternetAddress.parse(email));
message.setSubject("Radiantweb Password");
message.setText("New password is " +pass);
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
