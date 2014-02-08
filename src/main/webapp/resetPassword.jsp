<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Reset Password</title>
</head>
<body>


<form action="resetpass" theme="simple" method="POST">

<table>

<tr> <td> <label> </label> </br> </td> </tr>
         
<tr><td> Enter old password :</td>
<td><input type="password" name="password" value="" /></td></tr>

<tr><td> Enter new password :</td>
<td><input type="password" name="newpass" value="" /></td></tr>

<tr><td> Retype new password :</td>
<td><input type="password" name="rnewpass" value="" /></td></tr>

<tr> <td> <label>            </br></label> </td> </tr><tr>

<td><input type="submit" id="submit" style="font-face: 'Comic Sans MS'; font-size: small; color: teal; background-color: #FFFFF; border: 2pt ridge lightgrey" value="Submit" /></td>
<td><input type="reset" id="reset" style="font-face: 'Comic Sans MS'; font-size: small; color: teal; background-color: #FFFFF; border: 2pt ridge lightgrey" value=" Reset " /></td></tr>
 
                    
</table>
                 
</form>

</body>
</html>



</body>
</html>