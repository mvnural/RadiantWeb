<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ taglib prefix="s" uri="/struts-tags"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>Radiant Web</title>
<meta name="keywords" content="" />
<meta name="description" content="" />
<link href="styles/main.css" rel="stylesheet" type="text/css" />
</head>
<body>
	
	<div id="right">
       	<div id="log">
			<div>
				<s:if test="#session.login == 'true'">
                	<label for="text1">Welcome <s:property value="#session.accountType" /> <s:property value="#session.username" /> !</label>
                	<br/>
					<font size="+1"><a href= '<s:url action="logout" >
						<s:param name="page">index</s:param>
						</s:url>'>Logout</a>
					</font>
                    <br/>
                    <img src="images/fish_inp.png" title="" alt="" style="padding-right: 5px; padding-bottom: 2px;"/><a href="resetPassword.jsp">Change Password</a>
                    <br/>
                    <s:if test="#session.accountType == 'admin'">
						<a href= '<s:url action="showBug" >
						</s:url>'>System Bug</a>
					</s:if>
                    <br/>
                    &nbsp;
				</s:if>
				<s:if test="#session.login != 'true'">  
					<s:form action="login" theme="simple" method="POST">
					
						<h3>User Login</h3>
						<label for="text1">Username </label>
						<input id="text1" type="text" name="userID" value="" /><br />
						<label for="text2">Password </label>
						<input id="text2" type="password" name="password" value="" /><br />
						<input type="submit" id="login-submit" value="" />
					
					</s:form>
					<s:iterator value="loginError" id="ErrorMesg">
						<font color="#FF0000"><s:property value="ErrorMesg" /></font>
						<br/>
					</s:iterator>
					<table>
						<tr>
							<td width="120px"><img src="images/fish_inp.png" title="" alt="" style="padding-right: 5px; padding-bottom: 2px;" /><a href="createAccount.jsp">Create account</a></td>
							<td><img src="images/fish_inp.png" title="" alt="" style="padding-right: 5px; padding-bottom: 2px;" /><a href="forgotPassword.jsp">Forgot password</a></td>
						</tr>
					</table>
				</s:if> 
			</div>
		</div> 
	</div>  
	<div id="left">
    	<h3><a href="sawsdl.jsp">Start a new annnotation</a></h3>
    	<h2>My Services</h2>
    	<table>
    	<s:iterator value="userServices">
       		<tr>
       			<td><strong>Name:</strong><p><s:property value="name"/></p></td><td><s:property value="description"/></td><td><a href="sawsdl.jsp?name=<s:property value="name"/>">Load</a></td>	
       		</tr>
       	</s:iterator>
       	</table> 		
	</div>
    <div id="center">
    	
       	
    </div>
</body>
</html>
