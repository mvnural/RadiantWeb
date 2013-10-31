<%--
    Document   : index
    Modified From: original index.jsp (which is now index_old.jsp)
    Created on : May 26, 2011, 4:32:06 PM
    Author     : Yung Long Li
    Changes made: Takes into account functionality of the iframes called into the index_old.jsp i.e. rightPanel.jsp, leftPanelMsg.jsp, openWsdl.jsp
                   (Yes these three pages are also no longer required).
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Radiant Web Discovery Tool</title>
<script type="text/javascript">
	//It fefines the functions that would be called when wadl_form and owl_form are submitted.
    //It basically assigns the actions of the form to target of 2 hidden frames in the page, due to which the file
    //can be uploaded to the server without (apparent) page refresh; as there seems to be no straight forward way for an
    //ajaxed file upload; The targets for the forms that is uploadOWL.jsp and uploadWADL.jsp have the logic for uploading the respective files to the server.
    //Also you can see onload functions are called when the file iframe loads(i.e. file is uploaded)
    //Definitions for these functions can be found below
    function init()
    {
    	
    	// initialize value
    	document.getElementById("inputNum").value = 1;
    	document.getElementById("outputNum").value = 1;
    	
    	document.getElementById('requestDocHide').value = "";		
    	
    	document.getElementById("operationTextHide").value = "";
    	document.getElementById("operationNameHide").value = "";
    	document.getElementById("operationConceptHide").value = "";
    	document.getElementById("input1TextHide").value = "";
    	document.getElementById("input1TypeHide").value = "";
    	document.getElementById("input1NameHide").value = "";
    	document.getElementById("input1ConceptHide").value = "";
    	document.getElementById("output1TextHide").value = "";
    	document.getElementById("output1TypeHide").value = "";
    	document.getElementById("output1NameHide").value = "";
    	document.getElementById("output1ConceptHide").value = "";
    	
        $("#file").select(function(){
			//alert("Selected");
        });
        
        $("#ontologyinfo").hide();
        $('#definition').hide();
        $('#owltabs').hide();
        
        
        $.draggingOWLNodeLabel = null;
        $.draggindOWLnodeId = null;
        $.draggingAnnotationValue = null;
        $.count = 0;
        $.tagID = "";

        // set the recommend form dialog, it will hide first until it is called
        $("#recommendform").dialog({
        	autoOpen: false,
            height: 300,
            width: 500,
            modal: true,
            buttons: {
            	"Annotate": function() {
                	
            		var iri = getCheckedValue(document.forms['recommend concept'].elements['option']);
            		var label = getCheckedId(document.forms['recommend concept'].elements['option']);
            		document.getElementById(tagid + 'ConceptHide').value = iri;
            		
            		var owlid = ReplaceAll(iri , "#", "_");
            		owlid = ReplaceAll(owlid , ":", "_");
            		owlid = ReplaceAll(owlid , "/", "_");
            		owlid = ReplaceAll(owlid , ".", "_");
            		
            		var divbar = document.getElementById(tagid + 'bar');
            		divbar.innerHTML = '&nbsp; &nbsp; <span onclick="openOWLNode(\'' + owlid + '\')"><b>concept:< ' + label + ' ></b></span>' + 
            			'<span class=\"ui-button ui-corner-all" style="background:#616D7E; color:white; text-align: center; width: 20px; margin: 2px; padding:1px" onclick="removeTag(this, \'' + wsdlType + '\', \'concept\');"><b> X </b></span>' ;
            		        
                    $(this).dialog("close");
                },
                Cancel: function() {
                    $(this).dialog( "close" );
                }
            },
            close: function() {
            }
        });
        
     
        // set the input description dialog, it will hide first until it is called
        $("#inputdescription").dialog({
        	autoOpen: false,
            height: 300,
            width: 350,
            modal: true,
            buttons: {
				"OK": function() {
            		
                	var descriptionText = document.getElementById('descriptionText').value;
                	document.getElementById(tagid + 'TextHide').value = descriptionText;
                	$(this).dialog("close");
                },
            	"Search": function() {
            		
                	var descriptionText = document.getElementById('descriptionText').value;
                	document.getElementById(tagid + 'TextHide').value = descriptionText;
                	var message = '';
                	var type = elementType;
                	var name = document.getElementById(nameTagId).value;
                	$(this).dialog("close");
                    textRecommend(name, type, descriptionText, message);
                },
                Cancel: function() {
                    $(this).dialog( "close" );
                }
            },
            close: function() {
            }
        });
        
     
        // set the enter name dialog, it will hide first until it is called
        $("#entername").dialog({
        	autoOpen: false,
            height: 150,
            width: 300,
            modal: true,
            buttons: {
            	"OK": function() {
            		var id = tagid;
            		var nameVal = document.getElementById('nameVal').value.trim();
            		if (id != 'operation'){
            			var typeVal = document.getElementById('typeVal').value;
                	}
            		
            		var spantag = document.getElementById(id + "Name");
            		if (nameVal != ''){
            			spantag.innerHTML = nameVal;
            			document.getElementById(id + 'NameHide').value = nameVal;
            		}else{
            			spantag.innerHTML = "name: undefined";
            			document.getElementById(id + 'NameHide').value = "";
            		}
            		if (id != 'operation'){
            			if (typeVal != ''){
                			if (nameVal == ''){
                				spantag.innerHTML = "name: undefined";
                				document.getElementById(id + 'TypeHide').value = "";
                			}else{
                				spantag.innerHTML = nameVal + ":[" + typeVal + "]";;
                				document.getElementById(id + 'TypeHide').value = typeVal;
                			}
                		}else{
                			if (nameVal == ''){
                				spantag.innerHTML = "name: undefined";
                			}else{
                				spantag.innerHTML = nameVal;
                			}
                			document.getElementById(id + 'TypeHide').value = "";
                		}
            		}
            		document.getElementById('nameVal').value = "";
            		if (id != 'operation'){
            			document.getElementById('typeVal').value = "string";
            		}
            		$(this).dialog("close");
                },
                Cancel: function() {
                    $(this).dialog( "close" );
                }
            },
            close: function() {
                //allFields.val( "" ).removeClass( "ui-state-error" );
            }
        });

        $("#enterrequest").dialog({
        	autoOpen: false,
            height: 150,
            width: 350,
            modal: true,
            buttons: {
            	"OK": function() {
            		var requestDocVal = document.getElementById('requestDocVal').value.trim();
            		document.getElementById('requestDocHide').value = requestDocVal;
            		if (requestDocVal != ''){
            			document.getElementById('requestDoc').innerHTML = "keyword: " + requestDocVal;
                	}else{
                		document.getElementById('requestDoc').innerHTML = "request name: undefined";
                	}
            		$(this).dialog("close");
                },
                Cancel: function() {
                    $(this).dialog( "close" );
                }
            },
            close: function() {
                //allFields.val( "" ).removeClass( "ui-state-error" );
            }
        });
        
        $("#setOptionsDialog").dialog({
            autoOpen: false,
            height: 190,
            width: 460,
            modal: true,
            buttons: {
                "Set Options": function() {
                    setOntologyOptions();
                    $(this).dialog("close");
                },
                Cancel: function() {
                    $( this ).dialog( "close" );
                }
            },
            close: function() {
                //allFields.val( "" ).removeClass( "ui-state-error" );
            }
        });
                
        $("#importErrorDialog").dialog({
            autoOpen: false,
            height: 195,
            width: 450,
            modal: true,
            buttons: {
                "Load Onlogy With Import": function() {
                    document.getElementById("submitimportform").click();            
                    $(this).dialog("close");
                },
                Cancel: function() {
                    $( this ).dialog( "close" );
                }
            },
            close: function() {
                //allFields.val( "" ).removeClass( "ui-state-error" );
            }
        });
                
        $("#searchOntologyDialog").dialog({
            autoOpen: false,
            height: 250,
            width: 850,
            modal: true,
            buttons: {
                Cancel: function() {
                    $( this ).dialog( "close" );
                }
            },
            close: function() {
                //allFields.val( "" ).removeClass( "ui-state-error" );
            }
        });
                
        $("#discoverytree").treeview({
            persist: "location",
            animated: "slow",
            zIndex:1000,
            handle:'span',
            containment:'document',
            collapsed: false,
            unique: true
        });
        
        // set the specific tag droppable for convenient to direct to drop semantic concept
        setDroppable('operationdrop');
        setDroppable('input1drop');
        setDroppable('output1drop');
        
    }
    window.onload=init;

</script>
        
<script type="text/javascript">
    
	var isopen = 0;   
	var count = 0;
	var baseWord = "";
	
	var keyStr = "ABCDEFGHIJKLMNOP" +
    	"QRSTUVWXYZabcdef" +
    	"ghijklmnopqrstuv" +
    	"wxyz0123456789+/" +
    	"=";
	var tagid;
	var elementType;
	var nameTagId;
	var wsdlType;
	
	function setDroppable(id){
		$("#" + id).droppable({
			drop: function(event, ui) {
				var temp = this.id;
				tagid = temp.substring(0, (temp.length - 4));
        		if (tagid.indexOf("operation") != -1){
        			wsdlType = "operation";
        		}else if (tagid.indexOf("input") != -1){
        			wsdlType = "input";
        		}else if (tagid.indexOf("output") != -1){
        			wsdlType = "output";
        		}
				var iri = ui.draggable.attr('title');
				var label = ui.draggable.text();
        		document.getElementById(tagid + 'ConceptHide').value = iri;
        		var owlid = ReplaceAll(iri , "#", "_");
        		owlid = ReplaceAll(owlid , ":", "_");
        		owlid = ReplaceAll(owlid , "/", "_");
        		owlid = ReplaceAll(owlid , ".", "_");
        		var divbar = document.getElementById(tagid + 'bar');
        		divbar.innerHTML = '&nbsp; &nbsp; <span onclick="openOWLNode(\'' + owlid + '\')"><b>concept:< ' + label + ' ></b></span>' + 
        			'<span class=\"ui-button ui-corner-all" style="background:#616D7E; color:white; text-align: center; width: 20px; margin: 2px; padding:1px" onclick="removeTag(this, \'' + wsdlType + '\', \'concept\');"><b> X </b></span>' ;
			}
        });
	}
	
	function entername(ele, type){
		var node = findParentNode(type, ele);
		tagid = node.id;
		document.getElementById('nameVal').value = document.getElementById(node.id + 'NameHide').value;
		if (type == 'operation'){
			document.getElementById('typeVal').value = "";
			document.getElementById('typeLabel').style.display = "none";
		}else{
			document.getElementById('typeLabel').style.display = "block";
			document.getElementById('typeVal').value = document.getElementById(node.id + 'TypeHide').value;
		}
		$('#entername').dialog("open");
	}
	
	function enterRequest(){
		$('#enterrequest').dialog("open");
	}
	
	function recommendConcept(ele, conceptType, type){
		var node = findParentNode(type, ele);
		document.getElementById('descriptionText').value = document.getElementById(node.id + "TextHide").value;
		tagid = node.id;
		elementType = conceptType;
		wsdlType = type;
		nameTagId = node.id + 'NameHide';
		$('#inputdescription').dialog("open");
	}
	
	/*
	 * remove the unused input or output div
	 */
	 function removeTag(ele, type, concept){
	     var expression = '';
	     if (concept != null){
	    	 expression = "Do you want to remove this " + type + " " + concept;
	     }else{
	    	 expression = "Do you want to remove this " + type ;
	     }
	     if(confirm(expression)){
	  	   
	    	 var node = findParentNode(type, ele);
	    	 if (concept == 'concept'){
	    		 var id = node.id;
	    		 if (id.indexOf("bar") != -1){
	    			 var end = id.indexOf("bar");
	    			 id = id.substring(0, end);
	    		 }
	    		 //document.getElementById(id + "TextHide").value = "";
	    		 document.getElementById(id + "ConceptHide").value = "";
	    		 document.getElementById(id + "bar").innerHTML = "";
	    	 }else{
	    		 var idx = (node.id.substring(type.length, node.id.length) - 1) + 1;
		    	 var removeTag = document.getElementById(node.id);
		    	 var parent = removeTag.parentNode;
		    	 parent.removeChild(removeTag);
		    	 var num = (document.getElementById(type + "Num").value - 1) + 1;
		    	 var nowNum = num - 1;
		    	 for(var i = idx + 1; i < (num + 1); i++) {
	    			 // change li tag
		    		 var tag = document.getElementById(type + i);
		    		 tag.id = type + (i - 1);
		    		 // change div tag
		    		 tag = document.getElementById(type + i + 'drop');
		    		 tag.id = type + (i - 1) + 'drop';
		    		 //change name tag
		    		 tag = document.getElementById(type + i + 'Name');
		    		 tag.id = type + (i - 1) + 'Name';
		    		 // change hidden input tag
		    		 tag = document.getElementById(type + i + 'NameHide');
		    		 tag.id = type + (i - 1) + 'NameHide';
		    		 // change hidden input concept
		    		 tag = document.getElementById(type + i + 'ConceptHide');
		    		 tag.id = type + (i - 1) + 'ConceptHide';
		    		 // change hidden input text
		    		 tag = document.getElementById(type + i + 'TextHide');
		    		 tag.id = type + (i - 1) + 'TextHide';
		    		 // change hidden input type
		    		 tag = document.getElementById(type + i + 'TypeHide');
		    		 tag.id = type + (i - 1) + 'TypeHide';
		    		 // change div bar
		    		 tag = document.getElementById(type + i + 'bar');
		    		 tag.id = type + (i - 1) + 'bar';
		    	 }
	    		 document.getElementById(type + "Num").value = nowNum;
	    	 }	    	 
	     }
	 }
	      
   /* 
    * The function makes Ajax call o recommendAnnotation.jsp which performs the recommendation 
    * for the wadl element and then opens a dailog box showing the recommended annotations
    * in the form of radio buttons. The user can select the concept and click Annotate
    * to annotate the element with the slected concept.
    *
    **/
    function textRecommend(name, elementType, inputText, message){
    	document.getElementById('suggest-EleName').value = name;
		document.getElementById('suggest-EleType').value = elementType;
		document.getElementById('suggest-EleDoc').value = inputText;
		ajaxHttpSend('recommendConcept', 'TextRecommend', recommendSuggestion);
    }
   
    function recommendSuggestion(responseText)  
    {
    	try{
    		var response = eval("(" + responseText + ")");
        	var errormsg = response.errormsg;
        	//alert(errormsg);
        	if (errormsg == '' ){
        		document.getElementById("recommendform").innerHTML = response.innerHtml;
        		$('#recommendform').dialog("open");
        	}else{
        		alert(errormsg);
        	}
    	}catch(err){
    		alert("Internet connection problem");	
    	}
    }
    
    function setOwlLoc(){
    	document.getElementById('discovery-owlloc').value = document.getElementById('owlloc').value;
    }
   
	
   
</script>
<script>

function addInputElement() {
	var num = (document.getElementById('inputNum').value - 1) + 2; 
	var branches1 = $('<li id="input' + num + '"><div id="input' + num + 'drop" class="element drop ui-widget-content ui-corner-all ui-droppable" title="" >' +
		   				' <span id="input' + num + 'Name" style="width:40%; float:left;">name: undefined</span>' +
						' <input id="input' + num + 'NameHide" name="inputName" type="hidden" value="" >' + 
							' <span class="ui-button ui-corner-all" style="background:#616D7E; color:white; text-align: center; width: 80px; margin: 2px; padding:1px" onclick="entername(this, \'input\');" >enter name</span>' +
						' <span class="ui-button ui-corner-all" style="background:#616D7E; color:white; text-align: center; width: 80px; margin: 2px; padding:1px" onclick="recommendConcept(this, \'param\', \'input\');">enter doc</span>' + 
						' <span class="ui-button ui-corner-all" style="background:#616D7E; color:white; text-align: center; width: 20px; margin: 2px; padding:1px" onclick="removeTag(this, \'input\');"><b> X </b></span>' + 
						' <input type="hidden" id="input' + num + 'ConceptHide" name="inputConcept" value="" >' + 
						' <input type="hidden" id="input' + num + 'TextHide" name="inputText" value="" >' + 
						' <input type="hidden" id="input' + num + 'TypeHide" name="inputType" value="" >' + 
					' </div>' + 
					' <div id="input' + num + 'bar" >' + 
					' </div>' +
					' </li>'
	).appendTo("#input");
	document.getElementById('inputNum').value = num;
	setDroppable("input" + num + "drop");
}

function addOutputElement() {
	var num = (document.getElementById('outputNum').value - 1) + 2; 
	var branches2 = $('<li id="output' + num + '">' +
						' <div id="output' + num + 'drop" class="element drop ui-widget-content ui-corner-all ui-droppable" title="" >' +
						' <span id="output' + num + 'Name" style="width:40%; float:left;">name: undefined</span>' +
						' <input id="output' + num + 'NameHide" name="outputName" type="hidden" value="" >' +
							' <span class="ui-button ui-corner-all" style="background:#616D7E; color:white; text-align: center; width: 80px; margin: 2px; padding:1px" onclick="entername(this, \'output\');" >enter name</span>' +
					 	' <span class="ui-button ui-corner-all" style="background:#616D7E; color:white; text-align: center; width: 80px; margin: 2px; padding:1px" onclick="recommendConcept(this, \'param\', \'output\');">enter doc</span>' +
						' <span class="ui-button ui-corner-all" style="background:#616D7E; color:white; text-align: center; width: 20px; margin: 2px; padding:1px" onclick="removeTag(this, \'output\');"><b> X </b></span>' +
						' <input type="hidden" id="output' + num + 'ConceptHide" name="outputConcept" value="" >' + 
						' <input type="hidden" id="output' + num + 'TextHide" name="outputText" value="" >' +
						' <input type="hidden" id="output' + num + 'TypeHide" name="outputType" value="" >' +
						' </div>' +
						' <div id="output' + num + 'bar" >' + 
						' </div>' +
						' </li>'
	).appendTo("#output");
	document.getElementById('outputNum').value = num;
	setDroppable("output" + num + "drop");
}

</script>


	<script type="text/javascript" src="./js/base/jquery-1.7.1.min.js"></script>
	<script type="text/javascript" src="./js/base/jquery.ui.core.min.js?s2j=3.3.0"></script>
	<script type="text/javascript" src="./js/plugins/jquery.subscribe.min.js"></script>
	<script type="text/javascript" src="./js/struts2/jquery.struts2-3.3.0.min.js"></script>

    <link rel="stylesheet" href="./styles/jquery.treeview.css"/>
    <link rel="stylesheet" href="./styles/screen.css"/>
    <link rel="stylesheet" media="screen" type="text/css" href="./styles/kltooltips.css" />
    <link rel="stylesheet" media="screen" type="text/css" href="./styles/example.css" />
    
    <script type="text/javascript" src="./js/plugins/tabber.js"></script>
    <script type="text/javascript" language="javascript" src="./js/plugins/jquery.hotkeys.js"></script>
    <script type="text/javascript" language="javascript" src="./js/plugins/jquery.cookie.js"></script>
    <script type="text/javascript" language="javascript" src="./js/plugins/jquery.jstree.js"></script>
    <script type="text/javascript" language="javascript" src="./js/plugins/jquery.ui.core.js"></script>
    <script type="text/javascript" language="javascript" src="./js/plugins/jquery.ui.widget.js"></script>
    <script type="text/javascript" language="javascript" src="./js/plugins/jquery.ui.mouse.js"></script>
    <script type="text/javascript" language="javascript" src="./js/plugins/jquery.ui.button.js"></script>       
    <script type="text/javascript" language="javascript" src="./js/plugins/jquery.ui.draggable.js"></script>
    <script type="text/javascript" language="javascript" src="./js/plugins/jquery.ui.droppable.js"></script>
    <script type="text/javascript" language="javascript" src="./js/plugins/jquery.treeview.js"></script>
    <script type="text/javascript" language="javascript" src="./js/plugins/jquery.ui.progressbar.js"></script>
    <script type="text/javascript" language="javascript" src="./js/plugins/jquery.fixedMenu.js"></script>
    <script type="text/javascript" language="javascript" src="./js/plugins/jquery.ui.selectable.js"></script>
    <script type="text/javascript" language="javascript" src="./js/plugins/jquery.effects.clip.js"></script>
    <script type="text/javascript" language="javascript" src="./js/plugins/jquery.effects.core.js"></script>
    <script type="text/javascript" language="javascript" src="./js/plugins/jquery.ui.position.js"></script>
    <script type="text/javascript" language="javascript" src="./js/plugins/jquery.ui.resizable.js"></script>
    <script type="text/javascript" language="javascript" src="./js/plugins/jquery.ui.dialog.js"></script>
    <script type="text/javascript" language="javascript" src="./js/plugins/jquery.tinyscrollbar.js"></script>
    <script type="text/javascript" language="javascript" src="./js/plugins/jquery.scrollTo.js"></script>
    <script type="text/javascript" language="javascript" src="./js/plugins/jquery.ui.autocomplete.js"></script>
	
	<script type="text/javascript" language="javascript" src="./js/radiantweb/radiant.js"></script>
	<script type="text/javascript" language="javascript" src="./js/radiantweb/ontology.js"></script>
	<script type="text/javascript" language="javascript" src="./js/radiantweb/treeviewAction.js"></script>
	
	<link rel="stylesheet" href="./styles/jquery-ui/base/jquery.ui.all.css">
    <link rel="stylesheet" href="./styles/default.css">
    <link rel="stylesheet" href="./styles/jBox.css">
    <link rel="stylesheet" href="./styles/themes/apple/style.css">
    <link rel="stylesheet" href="./styles/fixedMenu_style1.css">
    <link rel="stylesheet" href="./styles/css/website.css" type="text/css" media="screen"/>
       
<script type="text/javascript">
$(function() {
	jQuery.struts2_jquery.version="3.3.0";
	jQuery.scriptPath = "./struts/";
	jQuery.ajaxSettings.traditional = true;
	jQuery.ajaxSetup ({
		cache: false
	});
	jQuery.struts2_jquery.require("js/struts2/jquery.ui.struts2-3.3.0.min.js");
});
</script>
       
<style>
	.ui-progressbar-value { background-image: url(./styles/themes/ui-lightness/images/3574351118_229c71823d_o.gif); }
</style>

<style type="text/css">
	input.hide1
	{
		position:absolute;
		left:510px;
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
	.operation{padding:2px;background:#FFCC99;color: black}
	.message{padding:2px;background:#C0C0C0;color: white}
	.element{padding:2px;background:#CCCC99;color: black}
	.wsdltagname{color:#C28585;font-weight: bold;}
	.wsdlattrname{font-weight: bold}
	.wsdlattrvalue{color:royalblue}
	.wsdlannotation{color:#5D781D;font-weight: bold}
	
</style>

<script language="javascript" src="./scripts/jBox.js"></script>

</head>

<body>
<div id="main" style="width:98%; height:auto">
	<div style="float:right;"><a href="index.jsp" style="font-size: 150%;">Home</a></div>
    <div style="width:100%; height:50px; text-align:center; vertical-align:middle; border-bottom: 1px solid #C5C5C5" >
        <h1><i>Radiant Web - Semantic Discovery Tool</i></h1>
    </div>
    <div id="discovery" class='ui-widget-content ui-corner-all' style="width:48%; height:660px; float:left; margin-top: 8px; margin-right:0.1em; margin-left:0.1em; margin-bottom: 6px ">
        <s:if test="#session.login == 'true'">
        <div style="width:100%; height:30px; text-align:center; vertical-align:middle; border-bottom: 1px solid #C5C5C5" >
            <h3><i>WSDL & SAWSDL Seaching</i></h3>
        </div>
        <!--<div style="width:23%; height:auto; text-align: right; float:left; padding:8px; ">
           <form id="save_wsdl" method="post" action="saveSawsdl.jsp">
                <input type="submit" name="action" value="Save WSDL" />
           </form>
        </div>-->
        <s:form action="Discovery" theme="simple" method="POST">
        <div id="discoveryform" style="width:100%; overflow:auto;">
        	<input id="discovery-owlloc" name="owlloc" type="hidden" value="" >
        	<input id="requestDocHide" name="serviceDoc" type="hidden" value="" >
        	<input id="inputNum" name="inputNum" type="hidden" value="" >
        	<input id="outputNum" name="outputNum" type="hidden" value="" >
        	<!-- For insert discovery form -->
        	<div class="ui-widget-content ui-corner-all ui-state-default" style="width:93%; margin:6px;padding:6px">
				<span id="requestDoc" style="width:67%;float:left;padding:2px">service keyword: undefined</span>
				<span id="" class="ui-button ui-corner-all" style="color:white;background:#616D7E;padding:3px;" onclick="enterRequest();" >enter word</span>	
			</div>
        	<div id="discoverytree" class='ui-widget-content ui-corner-all' style='overflow:auto;margin:6px; height:545px; padding:10px'>
        		<ul>
        			<li>
        				<div id="" class="drop ui-widget-content ui-corner-all" >
                    		<span style='width:80%;'>Operation:</span>
                    	</div>
                    </li>
        			<li id='operation'>
						<div id="operationdrop" class="operation drop ui-widget-content ui-corner-all ui-droppable" title="" >
                       		<span id="operationName" style='width:50%; float:left;'>name: undefined</span>
                       		<input id="operationNameHide" name="operationName" type="hidden" value="" >
                       		<span class="ui-button ui-corner-all" style="background:#616D7E; color:white; text-align: center; width: 90px; margin: 2px; padding:1px" onclick="entername(this, 'operation');" >enter name</span>
                       		<span class="ui-button ui-corner-all" style="background:#616D7E; color:white; text-align: center; width: 90px; margin: 2px; padding:1px" onclick="recommendConcept(this, 'operation', 'operation');">enter doc</span>
                       		<input type="hidden" id="operationConceptHide" name="operationConcept" value="" >
                       		<input type="hidden" id="operationTextHide" name="operationText" value="" >
                       	</div>
                       	<div id="operationbar" >
                       		
                       	</div>
                       	<ul>
                       		<li>
        						<div id="" class="drop ui-widget-content ui-corner-all" >
                    				<span style='width:80%;'>Input:</span>
                    				<span class="ui-button ui-corner-all" style="background:#616D7E; color:white; text-align: center; width: 50px; margin: 2px; padding:1px" onclick="addInputElement();" >Add</span>
                    			</div>
                    			<ul id="input">
                    				<li id="input1">
                            			<div id="input1drop" class="element drop ui-widget-content ui-corner-all ui-droppable" title="" >
                            				<span id="input1Name" style='width:40%; float:left;'>name: undefined</span>
                            				<input id="input1NameHide" name="inputName" type="hidden" value="" >
                       						<span class="ui-button ui-corner-all" style="background:#616D7E; color:white; text-align: center; width: 80px; margin: 2px; padding:1px" onclick="entername(this, 'input');" >enter name</span>
                            				<span class="ui-button ui-corner-all" style="background:#616D7E; color:white; text-align: center; width: 80px; margin: 2px; padding:1px" onclick="recommendConcept(this, 'param', 'input');">enter doc</span>
                            				<span class="ui-button ui-corner-all" style="background:#616D7E; color:white; text-align: center; width: 20px; margin: 2px; padding:1px" onclick="removeTag(this, 'input');"><b> X </b></span>
                            				<input type="hidden" id="input1ConceptHide" name="inputConcept" value="" >
                       						<input type="hidden" id="input1TextHide" name="inputText" value="" >
                       						<input type="hidden" id="input1TypeHide" name="inputType" value="" >
                            			</div>
                            			<div id="input1bar" >
                    					
                    					</div>
                    				</li>
                    			</ul>
                    		</li>
                    	</ul>
                       	
                       	<ul>
                       		<li>
        						<div id="" class="drop ui-widget-content ui-corner-all" >
                    				<span style='width:80%;'>Output:</span>
                    				<span class="ui-button ui-corner-all" style="background:#616D7E; color:white; text-align: center; width: 50px; margin: 2px; padding:1px" onclick="addOutputElement();" >Add</span>
                    			</div>
                    			<ul id="output">
                    				<li id="output1">
                            			<div id="output1drop" class="element drop ui-widget-content ui-corner-all ui-droppable" title="" >
                            				<span id="output1Name" style='width:40%; float:left;'>name: undefined</span>
                            				<input id="output1NameHide" name="outputName" type="hidden" value="" >
                       						<span class="ui-button ui-corner-all" style="background:#616D7E; color:white; text-align: center; width: 80px; margin: 2px; padding:1px" onclick="entername(this, 'output');" >enter name</span>
                            				<span class="ui-button ui-corner-all" style="background:#616D7E; color:white; text-align: center; width: 80px; margin: 2px; padding:1px" onclick="recommendConcept(this, 'param', 'output');">enter doc</span>
                            				<span class="ui-button ui-corner-all" style="background:#616D7E; color:white; text-align: center; width: 20px; margin: 2px; padding:1px" onclick="removeTag(this, 'output');"><b> X </b></span>
                            				<input type="hidden" id="output1ConceptHide" name="outputConcept" value="" >
                       						<input type="hidden" id="output1TextHide" name="outputText" value="" >
                       						<input type="hidden" id="output1TypeHide" name="outputType" value="" >
                            			</div>
                            			<div id="output1bar" >
                    					
                    					</div>
                    				</li>
                    			</ul>
                    		</li>
                    	</ul>
                    </li>
        		</ul>
        		<br><br><br>
        		<s:submit type="button" value="Discover Web Service"/>	
        	</div>
        	</div>
        </s:form>
        <div id="recommendform" title="Recommended Annotations">
        	<p>Hello</p>
        </div>
        
        <div id="inputdescription" title="Input document">
			<form>
				<label for="name">type expression</label>
				<textarea name="text" id="descriptionText" cols="40"; rows="10" style="resize: none" ></textarea><br/>
				<label>click <b>search</b> to get recommend ontology concept</label>
			</form>
		</div>
		
		<div id="entername" title="Enter name">
			<form>
				<label for="name">name</label>
				<input type="text" id="nameVal" value="" />
				<div id="typeLabel" style="display:block;" >
					<label for="name" >type</label>
					<select id="typeVal" >
						<option></option>
						<option>complex</option>
  						<option>string</option>
  						<option>int</option>
  						<option>short</option>
  						<option>integer</option>
  						<option>nonNegativeInteger</option>
  						<option>positiveInteger</option>
  						<option>float</option>
  						<option>double</option>
  						<option>dateTime</option>
  						<option>boolean</option>
					</select>
				</div>
			</form>
		</div>
		
		<div id="enterrequest" title="Enter request">
			<form>
				<label for="name">keyword</label>
				<input type="text" id="requestDocVal" value="" /><br>
			</form>
		</div>
        </s:if>
        <s:else>
    		<h2><font color="#FF0000" > Please login first!</font></h2>
    	</s:else>
    </div>

	<div id="owl" class='ui-widget-content ui-corner-all' style= "width:50%; height:660px; margin-left:0.1em; margin-right:0.1em; float:left; margin-top: 8px; margin-bottom: 6px">
		<s:if test="#session.login == 'true'">
		<div style="width:100%; height:30px; text-align:center; vertical-align:middle; border-bottom: 1px solid #C5C5C5" >
			<h3><i>Ontology Viewer (for SAWSDL)</i></h3>
		</div>
		
		<s:form id="owl_form" action="loadOWL" theme="simple" method="POST" enctype="multipart/form-data">
			<div style="width:100%; height:auto; float:left; padding:4px;">
				Owl Location <input type="text" id="owlloc" name="owlloc" size="50" >
			</div>
			&nbsp; &nbsp;
			<input type="button" style='z-index:1;width:80px;height:24px;font-size:12px;' value="Browse" id="owlfilebutton" onclick="hitFileBtn('OWLFile');" >
			<input type="submit" id="submit_owl_ajax" value="Load OWL" style="width:80px;" onclick="setOwlLoc();"/>
			<input type="file" style="display: none;" id="OWLFile" name="OWLFile" onchange="selectfile('OWLFile','owlloc')"/>
			<input type="hidden" id="input-ontology-tab" name="tabIndex" value="0" />
			<!-- ajax action for upload file or pass url -->
			<script type='text/javascript'>
				jQuery(document).ready(function () { 
					var options_submit_ajax = {};
					options_submit_ajax.jqueryaction = "button";
					options_submit_ajax.id = "submit_owl_ajax";	// submit button id
					options_submit_ajax.onbef = "before_owl";	// the function before send ajax (usually set animation picture)
					options_submit_ajax.oncom = "complete_owl";	// the function after response 
					options_submit_ajax.targets = "myAjaxTarget2";	// used to store the response text 
					options_submit_ajax.href = "#";
					options_submit_ajax.formids = "owl_form";	// submit form id
					jQuery.struts2_jquery.bind(jQuery('#submit_owl_ajax'),options_submit_ajax);					
				 });  
			</script>
		</s:form>
		<div id="myAjaxTarget2" style="display:none;" >
		</div>
		
		<form id="loadOwlHierarchy" method="post" >
			<input id="loadOwlisImport" type="hidden" name="importMsg" value="" >
			<input type="hidden" id="loadOwlloc" name="owlloc" value="" >
		</form>
        <hr>
        
        <center><div id="pbar2"></div></center>
        
        <div id="ontologyinfo" class="ui-widget-content ui-corner-all ui-state-default" style="width:97%; margin:3px;padding:3px"></div>
       	
       	<div style="background-image:url('./styles/images/step1.gif');height: 70px; margin: 4px; " class="ui-widget-content ui-corner-all" id="definition">
        	<div style="padding:4px;border-bottom:1px solid #c5c5c5" id="def">Definition</div>
        	<div id="defvalue" style="padding:4px;overflow: auto"></div>
        </div>
        
        <div id="owltabs" class="tabber" style="padding:2px;">
            <div id="owl1" class='tabbertab' style="height:395px; overflow: auto"><h2>Ontology 1</h2></div>
        	<div id="owl2" class='tabbertab' style="height:395px; overflow: auto"><h2>Ontology 2</h2></div>
        	<div id="owl3" class='tabbertab' style="height:395px; overflow: auto"><h2>Ontology 3</h2></div>
        </div>
            
        <div id="importErrorDialog" title="Import Error">
        	<p>Unable to import <span id="owlImportURL" class="import"></span></p>
            <form name="owlImportForm" id="owlImportForm" method="post" enctype="multipart/form-data" action="uploadOWL.jsp">
            	Enter path for local copy <input id="importFile" type="file" name='importFile'/><br>
                <input type="hidden" name="todo" value="upload">
                <input type="hidden" name="useImport" value="1">
                <input type='hidden' name="importURLEncoded" id='importURLEncoded'>
                <input type="submit" class="hide1" id="submitimportform" name="sumit" value="Submit">
				<iframe id="upload_target3" name="upload_target3" src="" style="width:0;height:0;border:0px solid #fff;"></iframe>
			</form>
		</div>
            
		<div id="setOptionsDialog" title="Set options for the ontology">
        	<p>Please enter the following options to help in recommending terms</p>
            <form name="setOptionsForm" id="owlImportForm" method="post" enctype="multipart/form-data" action="uploadOWL.jsp">
            	<fieldset>
                	IRI/Label of Operation term in Ontology <input id="operationOption" type="text" name='operationOption'/><br>
                    IRI/Label of parameter term in Ontology <input type="text" name="parameterOption" id="parameterOption">
                </fieldset>
			</form>
		</div>
        
        <div id="searchOntologyDialog" title="Search Ontlogy">
			<p>Search results for <span class="term"></span></p>
            <div id="searchResults"></div>
		</div>
        </s:if>
        <s:else>
        	<h2><font color="#FF0000" > Please login first!</font></h2>
        </s:else>
	</div>
    <div id='legend'></div>    
    </div>
    <div style="display:none;" >
    	<form id="TextRecommend" name="textRecommend" method="post"> 
       	  	<input id="suggest-EleName" name="EleName" type="hidden" value="" />
       	  	<input id="suggest-EleType" name="EleType" type="hidden" value="" />
       	  	<input id="suggest-EleDoc" name="EleDoc" type="hidden" value="" />
			<input id="suggest-Ontology-Tab" name="tabIndex" type="hidden" value="0" />
		</form>
		<form id="SetOntologyOptions" name="textRecommend" method="post"> 
       	  	<input id="setOntology-Operation" name="operation" type="hidden" value="" />
       	  	<input id="setOntology-Param" name="param" type="hidden" value="" />
       	</form>
       	<form id="searchOntologyTerm" name="searchOntologyTerm" method="post"> 
       	  	<input id="searchOntology-term" name="term" type="hidden" value="" />
       	</form>
       	<s:form id="suggest-form" action="loadSuggest" theme="simple" method="POST" >
			<input id="suggest-baseWord" name="baseWord" type="hidden" value="" />
		</s:form>
	</div>
</body>
</html>



