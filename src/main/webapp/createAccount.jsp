<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
pageEncoding="ISO-8859-1"%> <%-- @author Aravind Kalimurthy, University of Georgia --%>
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
               
<tr><th colspan="2"><h2> Enter Your Information Here</h2></th></tr>
<tr> <td> <label>            </br></label> </td> </tr>

                    
<tr><td><label> Email Address* :</label></td>
 <td><input type="text" name="email" value="" /></td></tr>
                    
<tr><td> <label>Username* : </label></td>
<td> <input type="text" name="userId" value="" /></td></tr>
                    
                      
<tr><td>  <label>Password* : </label></td>
<td><input type="password" name="password" value="" /></td></tr>
                    
<tr><td>  <label>RetypePassword* : </label></td>
<td><input type="password" name="retypepassword" value="" /></td></tr>
                    
<tr> <td> <label>            </br></label> </td> </tr>
                    
 <tr><td> <p> <h3>(Optional Fields) </h3></p>  </td> </tr> 
                    
<tr><td> <label>Name :</label></td>
<td><input type="text" name="name" value="" /></td></tr>
                    
<tr><td> <label>Organization : </label></td>
<td><input type="text" name="organization" value="" /></td></tr>
                    
<tr> <td> <label>            </br></label> </td> </tr><tr>

<td><input type="submit" value="Submit" /></td>
<td><input type="reset" value="Reset" /></td></tr>
                    
 <tr><td> <p> * Marked are Required</p>  </td> </tr> 
</table>
           
</form>
</body>
</html>