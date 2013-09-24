<%@ page language="java" contentType="text/html; charset=BIG5"
    pageEncoding="BIG5"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
</head>
<body>
	<div id="sawsdltree" style="height:580px; overflow: auto; display:block;" title="">
		<br>
		SAWSDL number:<s:property value="sawsdls.size()" />
		<ul>
			<s:iterator value="sawsdls" id="URLlink">
				<li id='<s:property value="name" />'>
            		<div>
						<a href='<s:url action="downloadWSDL" >
            				<s:param name="fileName"><s:property value="name" /></s:param>
            				</s:url>' ><s:property value="name" /></a>
            		</div>
        		</li>
			</s:iterator>
		</ul>
	</div>
	<div id="wsdltree" style="height:580px; overflow: auto; display:none;" title="">
		<br>
		WSDL number:<s:property value="wsdls.size()" />
		<ul>
			<s:iterator value="wsdls" id="URLlink">
				<li id='<s:property value="name" />'>
            		<div>
						<a href='<s:url action="downloadWSDL" >
            				<s:param name="fileName"><s:property value="name" /></s:param>
            				</s:url>' ><s:property value="name" /></a>
            		</div>
        		</li>
			</s:iterator>
		</ul>
	</div>
</body>
</html>