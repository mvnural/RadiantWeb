<%@ page language="java" contentType="text/html; charset=BIG5"
    pageEncoding="BIG5"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=BIG5">
<title>Discovery result</title>
<script type="text/javascript" src="scripts/tinybox.js"></script>
<link href="tinybox.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" language="JavaScript"><!--

	function display(url){
		TINY.box.show({iframe:url,boxid:'frameless',width:750,height:550,fixed:false,maskid:'bluemask',maskopacity:40,closejs:function(){}});
	}
	
	function showResult(id){
		if (id == 'sawsdl'){
			document.getElementById('sawsdl').style.display = "block";
			document.getElementById('wsdl').style.display = "none";
		}else{
			document.getElementById('wsdl').style.display = "block";
			document.getElementById('sawsdl').style.display = "none";
		}
	}
	
//--></script>
</head>
<body>
<h2><a href="index.jsp" >home</a></h2>
<h3><a href="javascript:void(null);" onclick="showResult('sawsdl');" >Show SAWSDL result</a> &nbsp; &nbsp; &nbsp; &nbsp; <a href="javascript:void(null);" onclick="showResult('wsdl');" >Show WSDL result</a></h3>

<div id="sawsdl" style="display:block;">
	<h2>SAWSDL Result:</h2>
	<table>
		<s:if test="showSawsdlResult.size == 0">
			No match result
		</s:if>
		<% int i = 0; %>
		<s:iterator value="showSawsdlResult" id="OperationInfo">
		<% i++; %>
			<tr>
				<td>&nbsp; &nbsp; <b>Rank <%= i %>:</b>
					Service: 
					<a href='<s:url action="downloadWSDL" >
            				<s:param name="fileName"><s:property value="service" />.sawsdl</s:param>
            				</s:url>' ><s:property value="service" /></a>
					Operation: <font color="#FF0000"><s:property value="operation" /></font>
				</td>
				<td>&nbsp; &nbsp; <b>score:</b><s:property value="score" /></td>
			</tr>
		</s:iterator>
	</table>
</div>

<div id="wsdl" style="display:none;">
	<h2>WSDL Result:</h2>
	<table>
		<s:if test="showWsdlResult.size == 0">
			No match result
		</s:if>
		<% int j = 0; %>
		<s:iterator value="showWsdlResult" id="OperationOBJ">
		<% j++; %>
			<tr>
				<td>&nbsp; &nbsp; <b>Rank <%= j %>:</b>
					Service: 
					<a href='<s:url action="downloadWSDL" >
            				<s:param name="fileName"><s:property value="service" />.wsdl</s:param>
            				</s:url>' ><s:property value="service" /></a>
					Operation: <font color="#FF0000"><s:property value="operation" /></font>
				</td>
				<td>&nbsp; &nbsp; <b>score:</b><s:property value="score" /></td>
			</tr>
		</s:iterator>
	</table>
</div>
	
</body>
</html>