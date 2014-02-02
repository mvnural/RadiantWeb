<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ taglib prefix="s" uri="/struts-tags"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>Metamorphosis Design Free Css Templates</title>
<meta name="keywords" content="" />
<meta name="description" content="" />
<link href="styles/main.css" rel="stylesheet" type="text/css" />
</head>
<body>
<script type="text/javascript">
$(document).ready(function(){
$("#guestAccount").click(function(){
$("#text1").attr("value","guest");
$("#text2").attr("value","guestPass");
});	
});
</script>
<div id="right">
<div id="log">
<div>
<s:if test="#session.login == 'true'">
<label for="text1">Welcome <s:property value="#session.accountType" /> <s:property value="#session.username" /> !</label>
<br/>
<font size="+1">
<a href= '<s:url action="logout" >
<s:param name="page">index</s:param>
</s:url>'>Logout</a>
</font>
<br/>
 	<img src="images/fish_inp.png" title="" alt="" style="padding-right: 5px; padding-bottom: 2px;"/><a href="/TKSS/changePass.jsp">Change Password</a>
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
<td><a id="guestAccount" href="#"><h2>Use guest account</h2></a></td>
</tr>
</table>
<table>
<tr>
<td width="120px"><img src="images/fish_inp.png" title="" alt="" style="padding-right: 5px; padding-bottom: 2px;" /><a href="createAccount.jsp">Create account</a></td>
<td><img src="images/fish_inp.png" title="" alt="" style="padding-right: 5px; padding-bottom: 2px;" /><a href="forgotpassword.jsp">Forgot password</a></td>
</tr>
</table>
</s:if> 
</div>
</div>
<br />
<h1>More Info</h1>
<div class="tit_bot">
<div class="right_b">
<a href="http://mango.ctegd.uga.edu/jkissingLab/SWS/RadiantWeb/RadiantWebUsersGuide.pdf">RadiantWeb User's Guide </a> <br /></div>
<div class="right_b"><a href="http://mango.ctegd.uga.edu/jkissingLab/SWS/RadiantWeb/index.html">RadiantWeb Website </a> <br>
</div>
<hr />
</div>   
</div>  
    <div id="left">
<h2>Radiant Web Annotation & Discovery Tool</h2>
<div class="col_l">
<span>  RadiantWeb is a Web application, to ease the process of annotation of Web service description documents with concepts from an ontology. It supports annotation of both WSDL and WADL documents. The tool follows the SAWSDL mechanism (A W3C recommendation) for annotation of WSDL documents. Since there is no specification put forth for annotating WADL documents, we use a mechanism similar to SAWSDL. RadiantWeb can operate at multiple levels of automation: manual, semi-automated and automated. </span><br />
<br />
<br />
<h2><a href="http://www.youtube.com/watch?v=Mxtc6krH-54&feature=player_embedded">Demo</a></h2>
<br />
<iframe width="480" height="360" src="http://www.youtube.com/embed/Mxtc6krH-54" frameborder="0" allowfullscreen></iframe>
<br />
<br />
</div>
</div>
</body>
</html>
