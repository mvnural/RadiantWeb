<%@ page language="java" contentType="text/html; charset=BIG5"
	pageEncoding="BIG5"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sj" uri="/struts-jquery-tags"%>
<html>
<head>

<sj:head/>

<title>Tab Demo</title>
<script type="text/javascript" src="script/click.js"></script>


<script type="text/javascript" src="./scripts/tabber.js"></script>
<script type="text/javascript" language="javascript" src="./scripts_new/jquery-1.5.1.js"></script>
<script type="text/javascript" language="javascript" src="./scripts_new/jquery.hotkeys.js"></script>
<script type="text/javascript" language="javascript" src="./scripts_new/jquery.cookie.js"></script>
<script type="text/javascript" language="javascript" src="./scripts_new/jquery.jstree.js"></script>
<script type="text/javascript" language="javascript" src="./scripts_new/jquery.ui.core.js"></script>
<script type="text/javascript" language="javascript" src="./scripts_new/jquery.ui.widget.js"></script>
<script type="text/javascript" language="javascript" src="./scripts_new/jquery.ui.mouse.js"></script>
<script type="text/javascript" language="javascript" src="./scripts_new/jquery.ui.button.js"></script>       
<script type="text/javascript" language="javascript" src="./scripts_new/jquery.ui.draggable.js"></script>
<script type="text/javascript" language="javascript" src="./scripts_new/jquery.ui.droppable.js"></script>
<script type="text/javascript" language="javascript" src="./scripts_new/jquery.treeview.js"></script>
<script type="text/javascript" language="javascript" src="./scripts_new/jquery.ui.progressbar.js"></script>
<script type="text/javascript" language="javascript" src="./scripts_new/jquery.fixedMenu.js"></script>
<script type="text/javascript" language="javascript" src="./scripts_new/jquery.ui.selectable.js"></script>
<script type="text/javascript" language="javascript" src="./scripts_new/jquery.effects.clip.js"></script>
<script type="text/javascript" language="javascript" src="./scripts_new/jquery.effects.core.js"></script>
<script type="text/javascript" language="javascript" src="./scripts_new/jquery.ui.position.js"></script>
<script type="text/javascript" language="javascript" src="./scripts_new/jquery.ui.resizable.js"></script>
<script type="text/javascript" language="javascript" src="./scripts_new/jquery.ui.dialog.js"></script>
<script type="text/javascript" language="javascript" src="./scripts_new/jquery.tinyscrollbar.js"></script>
<script type="text/javascript" language="javascript" src="./scripts_new/jquery.scrollTo.js"></script>
<script type="text/javascript" language="javascript" src="./scripts/AjaxSearch.js"></script>
<script type="text/javascript" language="javascript" src="./scripts/kltooltips.evaluation.js"></script>
		
      
        

<script type="text/javascript">
$(function() {
	
	alert("1");
	
	jQuery.struts2_jquery.version="3.3.0";
  	jQuery.scriptPath = "/TKSS/struts/";
	jQuery.ajaxSettings.traditional = true;

	jQuery.ajaxSetup ({
		cache: false
	});
	
	jQuery.struts2_jquery.require("js/struts2/jquery.ui.struts2-3.3.0.min.js");
	
	alert("5");
	
	
});
</script>


<script type="text/javascript">

	
	function Tabs() {
	}

	Tabs.init = function(tabListId) {
		var $ = document.getElementById;
		Tabs.tabLinks = $(tabListId).getElementsByTagName("A");

		var link, tabId, tab;
		for ( var i = 0; i < Tabs.tabLinks.length; i++) {
			link = Tabs.tabLinks[i];
			tabId = link.getAttribute("tabId");
			if (!tabId)
				alert("Expand link does not have a tabId element: "
						+ link.innerHTML);
			tab = $(tabId);
			if (!tab)
				alert("tabId does not exist: " + tabId);

			if (i == 0) {
				tab.style.display = "block";
				link.className = "linkSelected ";//+ link.className.replace(Tabs.removeUnselectedRegex, '');
			} else {
				tab.style.display = "none";
				link.className = "linkUnselected ";//+ link.className.replace(Tabs.removeSelectedRegex, '');
			}

			link.onclick = function() {
				var tabId = this.getAttribute("tabId");
				for ( var i = 0; i < Tabs.tabLinks.length; i++) {
					var link = Tabs.tabLinks[i];
					var loopId = link.getAttribute("tabId");
					if (loopId == tabId) {
						$(loopId).style.display = "block";
						link.className = "linkSelected ";//+ link.className.replace(Tabs.removeUnselectedRegex, '');
					} else {
						$(loopId).style.display = "none";
						link.className = "linkUnselected ";//+ link.className.replace(Tabs.removeSelectedRegex, '');
					}
				}
				if (this.blur)
					this.blur();
				return false;
			}; // of link function
		}
	};
	
	function ajaxHttpSend(action, form, response)  
	{  
		// url
	    var url = action + ".action";
		alert("url ok");
		
		// param serialize
		var params = $('#' + form).serialize();
		alert("params ok");
		alert(params);
		
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
	
	function testresponse(responseText)  
    {
		var start = responseText.indexOf("{");
		var end = responseText.indexOf("}") + 1;
		var response = eval("(" + responseText.substring(start, end) + ")");
    	var errormsg = response.errormsg;
    	alert(errormsg);
	}
	
	function getSize(form, sizeid){
		var browser = getbrowser();
		var filesize;
		if (browser == 'msie'){
			
		}else{
			var file = document.getElementById(form).file.files[0];
			document.getElementById(sizeid).value = file.size;
		}
		if (filesize > 10000000){
			alert("file size is over 10 MByte limit");
		}
	}
	
	function ReplaceAll(strOrg, strFind, strReplace){
		var index = 0;
		while(strOrg.indexOf(strFind,index) != -1){
			strOrg = strOrg.replace(strFind,strReplace);
			index = strOrg.indexOf(strFind,index);
		}
		
		alert(strOrg);
		
		return strOrg;	
	}
	
	
	// complete file upload process
	jQuery(document).subscribe('complete', function(event,data) {
		
		
		alert("start");
		
		testresponse(event.originalEvent.request.responseText);
	});

	
	
</script>

<style type="text/css">
<!--
#tabList {
	padding: 3px 20px;
	margin: 0.1em 0 0 0;
}

#tabList li {
	font-family: Georgia, "Times New Roman", Times, serif;
	font-size:14px;
	list-style: none;
	display: inline;
	margin: 0;
}

#tabList li a {
	border: 1px solid #bbb;
	padding: 3px 0.5em;
	margin: 0px;
	-moz-border-radius-topleft: 7px;
	-moz-border-radius-topright: 7px;
}

#tabList li a.linkSelected {
	background: #fff;
	border-bottom: 2px solid white;
	padding-top: 5px;
	font-size: 110%;
}

#tabList li a.linkUnselected {
	background: #eee;
	border-bottom: 1px solid #eee;
}

#tabList li a:link,#tablist li a:visited {
	color: navy;
}

#tablist li a:hover {
	color: #fc6;
}

#tabContents {
	padding: 20px;
	border-top: 1px solid #BBCCDD;
}

.tabcontent {
	display: none;
}

#tabList li.tabInvisible {
	display: none;
}

.ifile {
	position:absolute;
	opacity:0;
	filter:alpha(opacity=0);
	height: 24px;
	width: 129px;
}
#apDiv1 {
	position:absolute;
	width:170px;
	height:25px;
	z-index:1;
	left: 15px;
	top: 141px;
}
-->
</style>
</head>

<body onLoad="init();">

<ul id="tabList">
	<li><a href="#" tabId="tab1">Event</a></li>
	<li><a href="#" tabId="tab2">Wiki</a></li>
	<li><a href="#" tabId="tab3">Concept Map</a></li>
</ul>

<div id="tabContents">

<div id="tab1">Tab 1 <input type="text" name="tabInput1"
	value="tab1" /></div>

<div id="tab2">Tab 2 <input type="text" name="tabInput2"
	value="tab2" /></div>

<div id="tab3">Tab 3 <input type="text" name="tabInput3"
	value="tab3" /></div>
</div>

<s:form theme="simple" action="loadOWL" method="POST" enctype="multipart/form-data">
	<table>
	  	<tr>
	  		<td valign="top" height="20" colspan="2"><input type="file" name="file" id="file" size="5" class="ifile"
     				onChange="
      				this.form.upfile.value=this.value.substr(this.value.lastIndexOf('\\')+1);
      			" >
                <img src="./images/changepic.gif" >
                <input type="file" name="file" id="123" size="10" onChange="alert(this.value);">
  			</td>
	  	</tr>
	  	<tr><td><s:submit type="image" src="./images/upload.gif" /></td><td><s:submit type="image" src="./images/delete.gif" /></td></tr>
  </table>
</s:form>

<select name="field" id="text11">
	<option value="physics">physics</option>
	<option value="chemistry">chemistry</option>
	<option value="biology">biology</option>
	<option value="math">math</option>
</select>


<s:form id="form" action="testUpload" theme="simple" method="POST" enctype="multipart/form-data">
	<input type="file" name="file1" onchange="getSize('form', 'filesize');"/>
	<input id="filesize" type="hidden" name="size" />
	<sj:submit value="Submit Form" targets="myAjaxTarget" onBeforeTopics="before" onCompleteTopics="complete" />
</s:form>
<div id="myAjaxTarget" onclick="test();" >
</div>

<br><br><br>


<s:form id="form" action="testUpload" theme="simple" method="POST" enctype="multipart/form-data">
	<input type="file" name="file1" onchange="getSize('form', 'filesize');"/>
	<input id="filesize" type="hidden" name="size" />
	<input type="submit" id="submit_352072428" value="Submit Form"/>
<script type='text/javascript'>
jQuery(document).ready(function () { 
	var options_submit_352072428 = {};
	options_submit_352072428.jqueryaction = "button";
	options_submit_352072428.id = "submit_352072428";
	options_submit_352072428.oncom = "complete";
	options_submit_352072428.targets = "myAjaxTarget1";
	options_submit_352072428.href = "#";
	options_submit_352072428.formids = "form";

jQuery.struts2_jquery.bind(jQuery('#submit_352072428'),options_submit_352072428);

 });  
</script>
</s:form>
<div id="myAjaxTarget1" >
</div>





</body>

</html>