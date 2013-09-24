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

<div id="bg">
<div id="main">
<!-- header begins -->
<div id="header">
	<div id="logo">
        <div class="CNGH"><a href="#">WS Annotations Group</a>
        <h2><a href="http://www.metamorphozis.com/" id="metamorph">Design by University of Georgia</a></h2></div>
    </div>
    <div id="buttons">
      <div class="but"><a href="/Radiant/index.jsp"  title="">Home</a></div>
      <div class="but"><a href="/Radiant/sawsdl.jsp" title="">Annotation</a></div>
      <div class="but"><a href="/Radiant/discovery.jsp" title="">Discovery</a></div>
      <div class="but"><a href="#" title="">About us</a></div>
      <div class="but"><a href="#" title="">Contact us</a></div>
    </div>
</div>
<!-- header ends -->
    <!-- content begins -->
    	<div id="content">
        	<div id="right">
            	<div id="log">
                    <div>
                        <s:if test="#session.login == 'true'">
                        	<label for="text1">Welcome <s:property value="#session.accountType" /> <s:property value="#session.username" /> !</label>
                            <br/>
                            <font size="+1"><a href= '<s:url action="logout" >
								<s:param name="page">index</s:param>
								</s:url>'>Logout</a></font>
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
									<td width="120px"><img src="images/fish_inp.png" title="" alt="" style="padding-right: 5px; padding-bottom: 2px;" /><a href="/Radiant/createAccount.jsp">Create account</a></td>
									<td><img src="images/fish_inp.png" title="" alt="" style="padding-right: 5px; padding-bottom: 2px;" /><a href="/Radiant/forgotpass.jsp">Forgot password</a></td>
								</tr>
							</table>
						</s:if> 
                    </div>
              </div>
              <br />
            	<h1>Recent News</h1>
                <div class="tit_bot">
                  <div class="right_b"><span class="col_b">Related Publications  </span><br />
                    <a href="ChaitanyaThesis.pdf">RadiantWeb: A Tool Facilitating Semantic Annotation of Web Services,</a> <br />Masters Thesis (M.S. in CS Degree) May 2012. 
                  </div>
                  <hr />
                  <div class="right_b"><span class="col_b">available space </span><br />
                    news message ... </div>
                  <div class="more"><a href="#"><img src="images/b_more.gif" alt="" /></a></div>
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
       	  	  </div>
       	  </div>
                 <div style="clear: both"><img src="images/spaser.gif" alt="" width="1" height="1" /></div> 
            
          <div style="clear: both"><img src="images/spaser.gif" alt="" width="1" height="1" /></div>
     	</div>
        <!-- content ends -->
         <!-- footer begins -->
    <div id="footer">
  Copyright  2010. Designed by <a href="http://www.metamorphozis.com/" title="Flash Templates">Flash Templates</a><br />
		<a href="#">Privacy Policy</a> | <a href="#">Terms of Use</a> | <a href="http://validator.w3.org/check/referer" title="This page validates as XHTML 1.0 Transitional"><abbr title="eXtensible HyperText Markup Language">XHTML</abbr></a> | <a href="http://jigsaw.w3.org/css-validator/check/referer" title="This page validates as CSS"><abbr title="Cascading Style Sheets">CSS</abbr></a></div>
<!-- footer ends -->
</div> 
</div>
</body>
</html>
