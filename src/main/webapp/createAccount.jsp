<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
     <%@ page import="java.sql.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Create New User</title>
</head>
<body>
<form action="createuser" method="POST">
             <table >
               
                    <tr>
                        <th colspan="2"><h2> Enter Your Information Here</h2></th>
                    </tr>
                    
<tr>
                        <td> <label>Name* :</label></td>
                        <td><input type="text" name="name" value="" /></td>
                    </tr>
                    
                    <tr>
                    <td><label> Email ID* :</label></td>
                        <td><input type="text" name="email" value="" /></td>
                    </tr>
                    
                    <tr>
                        <td> <label>Organization : </label></td>
                        <td><input type="text" name="organization" value="" /></td>
                    </tr>
                    
                    <tr>
                   <td> <label>Choose Username* : </label></td>
<td> <input type="text" name="userID" value="" /></td>
                    </tr>
                    
                    <tr>
                  <td>  <label>Password* : </label></td>
<td><input type="password" name="password" value="" /></td>
                    </tr>
                    
                     <tr>
                  <td>  <label>ReTypePassword* : </label></td>
<td><input type="password" name="retypepassword" value="" /></td>
                    </tr>
                    
                     <tr>
                        <td><input type="submit" value="Submit" /></td>
                        <td><input type="reset" value="Reset" /></td>
                    </tr>
                    
                    <tr>
                    <td> <p> * Marked are Required</p>  </td> </tr> 
            </table>
           
</form>
<%
java.sql.Connection conn=null;
String url="jdbc:hsqldb:hsql://localhost/";
String Driver="com.mysql.jdbc.Driver";
String username="SA";
String password="";
try
{
Class.forName(Driver);
conn= DriverManager.getConnection(url,username,password);
if(conn!=null){
System.out.println("Connected to DataBase");}
else{
System.out.println("Cannot Cannot to DataBase");
}
}
catch(Exception error)
{
System.out.println("Error " + error.getMessage());
}
%>
</body>
</html>