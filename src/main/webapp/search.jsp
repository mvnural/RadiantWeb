<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>

<script type="text/javascript" src="/TKSS/struts/js/base/jquery-1.7.1.min.js"></script>
<script type="text/javascript" src="/TKSS/struts/js/base/jquery.ui.core.min.js?s2j=3.3.0"></script>
<script type="text/javascript" src="/TKSS/struts/js/plugins/jquery.subscribe.min.js"></script>
<script type="text/javascript" src="/TKSS/struts/js/struts2/jquery.struts2-3.3.0.min.js"></script>

<meta http-equiv="content-type" content="text/html; charset=utf-8"></meta>
<title>Radiant Web Service Collection Tool</title>
<meta name="keywords" content=""></meta>
<meta name="description" content=""></meta>
<link rel="stylesheet" href="./styles/jquery.treeview.css"/>
<link rel="stylesheet" href="./styles/screen.css"/>
<link rel="stylesheet" media="screen" type="text/css" href="./styles/kltooltips.css" />
<link rel="stylesheet" media="screen" type="text/css" href="./styles/example.css" />
<link rel="stylesheet" href="./styles/jquery-ui/base/jquery.ui.all.css">
<link rel="stylesheet" href="./styles/default.css">
<link rel="stylesheet" href="./styles/jBox.css">
<link rel="stylesheet" href="./styles/themes/apple/style.css">
<link rel="stylesheet" href="./styles/fixedMenu_style1.css">
<link rel="stylesheet" href="./styles/css/website.css" type="text/css" media="screen"/>
<link rel="stylesheet" href="./styles/css/tab.css" type="text/css" media="screen"/>
<script type="text/javascript" language="javascript" src="./scripts_new/jquery-1.5.1.js"></script>
<script type="text/javascript" language="javascript" src="./scripts_new/jquery.treeview.js"></script>
<script type="text/javascript" language="javascript" src="./scripts_new/jquery.ui.progressbar.js"></script>
<script type="text/javascript" language="javascript" src="./scripts_new/jquery.ui.core.js"></script>
<script type="text/javascript" language="javascript" src="./scripts_new/click.js"></script>
<style>
	.ui-progressbar-value { background-image: url(./styles/themes/ui-lightness/images/3574351118_229c71823d_o.gif); }
</style>

<style type="text/css">
	input.hide1
	{
		position:absolute;
		-moz-opacity:0 ;
		filter:alpha(opacity:0);
		opacity: 0;
		z-index: 2;
		width: 72px;
	}
	input.align
	{
		position:relative;
	}

	.annotated{background:#99CC99;padding:2px;margin:10px;color: white}
	.suggested{background:#C28585;padding: 2px; margin:10px;color: white}
	.modelReference{color:white;background:#347C2C}
	.hove{cursor:pointer}
	.preExisting{color:white;background:#5E7D7E;padding:2px;margin:10px}
	.selectedowl{background: #CCFFFF;}
	.operation{padding:2px;background:#FFCC99;color: white}
	.message{padding:2px;background:#C0C0C0;color: white}
	.element{padding:2px;background:#CCCC99;color: white}
	.wsdltagname{color:#C28585;font-weight: bold;}
	.wsdlattrname{font-weight: bold}
	.wsdlattrvalue{color:royalblue}
	.wsdlannotation{color:#5D781D;font-weight: bold}
	
</style>
<script language="JavaScript">  
    
    function init(){

    	$("#sawsdltree").treeview({
			persist: "location",
            animated: "slow",
            zIndex:1000,
            handle:'span',
            collapsed: true,
            unique: true
		});
    	
    	$("#wsdltree").treeview({
			persist: "location",
            animated: "slow",
            zIndex:1000,
            handle:'span',
            collapsed: true,
            unique: true
		});
    	
    	if (document.getElementById('totalPages').value != ''){
    		var totalPages = (document.getElementById('totalPages').value - 1) + 1; 
    		var startPage = (document.getElementById('startPage').value - 1) + 1; 
    		var splitPageDiv = document.getElementById('splitPage');
    		var innerhtml = "<br>";
    		for (var i = 1; i <= totalPages; i++){
    			if (i == startPage) continue;
    			var innerhtml = innerhtml + "<a href=\"javascript:void(null);\" onclick=\"searchSubmit('" + i + "');\">page " + i + "</a>" + " &nbsp; &nbsp; ";
    			splitPageDiv.innerHTML = innerhtml;    			
    		}	
    	}
    }
    /*
    function ajaxHttpSend(action, form, response)  
    {
    	show_progressbar('processImage1');
    	show_progressbar('processImage2');
    	
    	// url
        var url = action + ".action";
    	alert("url ok");
    	
    	// param serialize
    	var params = $('#' + form).serialize();
    	alert("params ok");
    	
    	var xmlhttp;
        if (window.XMLHttpRequest)
        {// code for IE7+, Firefox, Chrome, Opera, Safari
    		   xmlhttp=new XMLHttpRequest();
        }else{// code for IE 6, IE5
            xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
        }
        
        alert(url + "?" + params);
        
        xmlhttp.open("POST", url + "?" + params, true);
        xmlhttp.send(); 
        xmlhttp.onreadystatechange=function()
        {
        	if (xmlhttp.readyState==4 && xmlhttp.status==200)
            {
        		response(xmlhttp.responseText);
        	}
        };
    }
    
    function processResponse(responseText)  
    {
    	//alert("receive");
    	try{
    		var response = eval("(" + request.responseText + ")");
        	
        	errormsg = response.errormsg;
        	if (errormsg != '' ){
        		clear_progressbar('processImage1');
        		clear_progressbar('processImage2');
        		alert(errormsg);
        		reutrn;
        	}
        	
            $("wsdlNum").innerHTML = "<b><FONT SIZE=3>Total WSDL file in system : " + response.wsdlNum + "</FONT></b>&nbsp; &nbsp; &nbsp;" ;
        	$("sawsdlNum").innerHTML = "<b><FONT SIZE=3>Total SAWSDL file in system : " + response.sawsdlNum + "</FONT></b><br/><br/><br/>" ;
        	
        	var wsdls = response.wsdls;
        	var sawsdls = response.sawsdls;

        	var wsdlResult = '<table class="tablecss"><tr><td colspan="4" class="tdcss"><b>WSDL results:</b></td></tr>' +
    							'<tr><td width="100" class="tdcss">Name</td><td width="300" class="tdcss">WSDL Location</td><td width="300" class="tdcss">Backup Location</td><td width="200" class="tdcss">Error Message</td></tr>' ;	
    		
    		for(var i = 0; i < wsdls.length; i++){	
    			wsdlResult = wsdlResult + '<tr valign="top"><td class="tdcss">' + wsdls[i].name + '</td>' + 
    										'<td class="tdcss">' + wsdls[i].wsLocation + '</td>' +
    										'<td class="tdcss">' + wsdls[i].backupWSDLURL + '</td>' + 
    										'<td class="tdcss">' + wsdls[i].WSDLmessage + '</td></tr>' 
    			;
        	}
    		wsdlResult = wsdlResult + '</table>';
    		$("wsdlResult").innerHTML = wsdlResult;
    		clear_progressbar('processImage1');
    		
    		var sawsdlResult = '<table class="tablecss"><tr><td colspan="4" class="tdcss"><b>SAWSDL results:</b></td></tr>' +
    							'<tr><td width="100" class="tdcss">Name</td><td width="500" class="tdcss">SAWSDL Location</td><td width="200" class="tdcss">Error Message</td></tr>' ;
    		
    		for(var i = 0; i < sawsdls.length; i++){
    			sawsdlResult = sawsdlResult + '<tr valign="top"><td class="tdcss">' + sawsdls[i].name + '</td>' + 
    											'<td class="tdcss">' + sawsdls[i].SAWSDLURL + '</td>' +
    											'<td class="tdcss">' + sawsdls[i].SAWSDLmessage + '</td></tr>'
    			;
        	}
    		sawsdlResult = sawsdlResult + '</table>';
    		$("sawsdlResult").innerHTML = sawsdlResult;
    		
    		clear_progressbar('processImage1');
    		clear_progressbar('processImage2');
    	
    	}catch(err){
    		
    		alert("connect error");
    		clear_progressbar('processImage1');
    		clear_progressbar('processImage2');
    	
    	}
    }
    */
    function show_progressbar(id) {
    	document.getElementById(id).innerHTML = "<b>Loading..., please wait.</b>";
    }
    
    function clear_progressbar(id) {
    	document.getElementById(id).innerHTML = '';
	}
    
    function tabchange(tab) {
    	if (tab == 'sawsdltreetab'){
    		document.getElementById('sawsdltree').style.display = "block";
    		document.getElementById("sawsdltreetab").setAttribute("class", "tabberactive"); 
    		document.getElementById('wsdltree').style.display = "none";
    		document.getElementById("wsdltreetab").setAttribute("class", ""); 
    	}else if (tab == 'wsdltreetab'){
    		document.getElementById('sawsdltree').style.display = "none"; 
    		document.getElementById("sawsdltreetab").setAttribute("class", "");
    		document.getElementById('wsdltree').style.display = "block";
    		document.getElementById("wsdltreetab").setAttribute("class", "tabberactive"); 
    	}
    }
    
    function viewWs(filename){
    	alert(filename);
    	document.getElementById('test').click();
    }
    
    function searchSubmit(page){
    	document.getElementById('pageid').value = page;
    	document.getElementById('keyword').value = document.getElementById('submitKeyword').value;
    	var submitButton = document.getElementById('submitButton');
    	simulateClick(submitButton, "click");
    }
        
</script> 

</head>

<body onload="init();">
<!-- start header -->
<div id="main" style="width:98%; height:auto">
	<div style="width:100%; height:50px; text-align:center; vertical-align:middle; border-bottom: 1px solid #C5C5C5" >
        <h1><i>Radiant Web - Collecting WebService Tool</i></h1>
	</div>
	<!-- start content -->
	<div id="collectService" class='ui-widget-content ui-corner-all' style="width:73%; height:660px; float:left; margin-top: 1em; margin-left:0.5em; margin-right:0.5em; margin-bottom: 1em ">
		<div style="width:100%; height:30px; text-align:center; vertical-align:middle; border-bottom: 1px solid #C5C5C5" >
            <h2><i>Web Service Collection Tool</i></h2>
        </div>
		<div class="post" style="margin-left:0.5em; margin-right:0.5em; ">
			<h2>BioCatalogue Website search tool</h2>
			<s:form action="SearchWSDL" theme="simple" id="search" name="search" method="post"> 
			<table>
				<tr><td colspan="3">
					<b>Input keywords to search and download Web Service.</b>
				</td></tr>
				<tr><td colspan="3"><br/></td></tr>
				<tr>
					<td width="50px" >
						keyword
					</td>
					<td width="50px">
						<input TYPE="text" name="keyword" id="keyword"/>
						<input id="pageid" TYPE="hidden" name="pageid" value="" />
					</td>
					<td><input id="submitButton" type="submit" value="search" onClick="show_progressbar('processImage');"/></td>
				</tr>
			</table>
			</s:form>
			<div style="width:100%; height:60px; overflow-x:hidden; overflow-y:auto;">
				<b><s:property value="errormsg" /></b>
				<br/>
				<input id="totalPages" type="hidden" value="<s:property value='totalPages' />" />
				<input id="startPage" type="hidden" value="<s:property value='startPage' />" />
				<input id="submitKeyword" type="hidden" value="<s:property value='keyword' />" />
				<div id="splitPage" >
				
				</div>
				<div id="processImage">
				
				</div>
			</div>
			<br/>
			<h2>BioCatalogue Result</h2>
			<div style="height:365px; overflow:auto;">
				<div id="wsdlResult">
					<table class="tablecss">
						<tr>
							<td colspan='4' class="tdcss"><b>WSDL results:</b></td>
						</tr>
						<tr>
							<td width='100' class="tdcss">Name</td>
							<td width='300' class="tdcss">WSDL Location</td>
							<td width='300' class="tdcss">Backup Location</td>
							<td width='200' class="tdcss">Error Message</td>
						</tr>
						<s:iterator value="wsdls" id="URLlink">
							<tr>
								<td width='100' class="tdcss"><s:property value="name" /></td>
								<td width='300' class="tdcss"><s:property value="wsLocation" /></td>
								<td width='300' class="tdcss"><s:property value="backupWSDLURL" /></td>
								<td width='200' class="tdcss"><s:property value="WSDLmessage" /></td>
							</tr>
						</s:iterator>
					</table>
				</div>
				<br/>
				<div id="sawsdlResult">
					<table class="tablecss">
						<tr>
							<td colspan='3' class="tdcss"><b>SAWSDL results:</b></td>
						</tr>
						<tr>
							<td width='100' class="tdcss">Name</td>	
							<td width='500' class="tdcss">SAWSDL Location</td>
							<td width='200' class="tdcss">Error Message</td>
						</tr>
						<s:iterator value="sawsdls" id="URLlink">
							<tr>
								<td width='100' class="tdcss"><s:property value="name" /></td>
								<td width='500' class="tdcss"><s:property value="SAWSDLURL" /></td>
								<td width='200' class="tdcss"><s:property value="SAWSDLmessage" /></td>
							</tr>
						</s:iterator>
					</table>
				</div>
				<br/>
				<br/>
			</div>
		</div>
	</div>
	<div id="serviceList" class='ui-widget-content ui-corner-all' style= "width:24%; height:660px; margin-left:0.5em; float:left; margin-top: 1em; margin-bottom: 1em">
		<div style="width:100%; height:30px; text-align:center; vertical-align:middle; border-bottom: 1px solid #C5C5C5" >
            <h2><i>Web Service List</i></h2>
        </div>
        <div id="webservicetree" style="padding:2px;">
            <ul class="tabbernav">
				<li id="sawsdltreetab" class="tabberactive">
					<a href="javascript:void(null);" title="SAWSDL" onclick="tabchange('sawsdltreetab');" >SAWSDL</a>
				</li>
				<li id="wsdltreetab" class="">
					<a href="javascript:void(null);" title="WSDL" onclick="tabchange('wsdltreetab');" >WSDL</a>
				</li>
			</ul>
			<s:action name="getWsTree" executeResult="true" />
        </div>
        
	</div>
	<a id="WsLink" href="" ></a>
	<!-- end content -->
	<div style="clear: both;"></div>
</div>
<!-- end page -->
</body></html>
