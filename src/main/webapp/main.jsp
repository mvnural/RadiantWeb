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
<% String login = (String) session.getAttribute("login"); %>
		

<body>
<div id="bg">
<div id="main">
<!-- header begins -->
<div id="header">
	<div id="logo">
        <div class="CNGH"><a href="#">WS Annotations Group</a>
        <h2><a href="#" id="metamorph">Design by University of Georgia</a></h2></div>
    </div>
    <div id="buttons">
      <div class="but"><a href="<%=request.getContextPath()%>/index.jsp"  title="">Home</a></div>
      <div class="but"><a href="<%=request.getContextPath()%>/sawsdl.jsp" title="">Annotation</a></div>
      <div class="but"><a href="<%=request.getContextPath()%>/discovery.jsp" title="">Discovery</a></div>
      <% if (login != null){ %>
      	<div class="but"><a href="/<%=request.getContextPath()%>/myPage.jsp" title="">MyPage</a></div>
      <% } %>
      <div class="but"><a href="#" title="">Contact us</a></div>
    </div>
</div>
<!-- header ends -->
    <!-- content begins -->
	<div id="content">
		<sitemesh:write property='body'/> 
        <div style="clear: both"><img src="images/spaser.gif" alt="" width="1" height="1" /></div>             
        <div style="clear: both"><img src="images/spaser.gif" alt="" width="1" height="1" /></div>
    </div>
    <!-- content ends -->
    <!-- footer begins -->
    <div id="footer">
  		Copyright  2010. Designed by <a href="http://www.metamorphozis.com/" title="Flash Templates">Flash Templates</a><br />
		<a href="#">Privacy Policy</a> | <a href="#">Terms of Use</a> | <a href="http://validator.w3.org/check/referer" title="This page validates as XHTML 1.0 Transitional"><abbr title="eXtensible HyperText Markup Language">XHTML</abbr></a> | <a href="http://jigsaw.w3.org/css-validator/check/referer" title="This page validates as CSS"><abbr title="Cascading Style Sheets">CSS</abbr></a>
	</div>
	<!-- footer ends -->
</div> 
</div>
</body>
</html>
