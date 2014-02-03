<%--
    The main page that displays the WSDL and ontology. 
    It requests all the functions of the tool and is updated through ajax for every request.
    Document   : index
    Created on : May 26, 2011, 4:32:06 PM
    Author     : Chaitanya Guttula, Yung Long Li
    License    : MIT style license file
    
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Radiant Web Service Annotation Tool</title>
<script type="text/javascript">
	
	//It defines the functions that would be called when wadl_form and owl_form are submitted.
    //It basically assigns the actions of the form to target of 2 hidden frames in the page, due to which the file
    //can be uploaded to the server without (apparent) page refresh; as there seems to be no straight forward way for an
    //ajaxed file upload; The targets for the forms that is uploadOWL.jsp and uploadWADL.jsp have the logic for uploading the respective files to the server.
    //Also you can see onload functions are called when the file iframe loads(i.e. file is uploaded)
    //Definitions for these functions can be found below
    function init()
    {
    	window.onbeforeunload = function()
		{
			var reslut = refreshAlert();
			if (reslut == "true") return "";
        };
		
		window.onclose = function(){
        	alert("hello");
        };
        
        
        $("#file").select(function(){
			alert("Selected");
        });
        
        $("#ontologyinfo").hide();
        $('#definition').hide();
        $('#owltabs').hide();
        
        $.draggingOWLNodeLabel = null;
        $.draggindOWLnodeId = null;
        $.draggingAnnotationValue = null;
        $.count = 0;
        $.tagID = "";

        $("#recommendform").dialog({
        	autoOpen: false,
            height: 300,
            width: 500,
            modal: true,
            buttons: {
            	"Annotate": function() {
                	
            		var iri = getCheckedValue(document.forms['recommend concept'].elements['option']);
            		var label = getCheckedId(document.forms['recommend concept'].elements['option']);
            		var id = document.getElementById('suggest-EleID').value;
            		var type = document.getElementById('suggest-EleType').value;
            		var name = document.getElementById('suggest-EleName').value;
            		
            		// execute the annotation to ws file
            		document.getElementById("update-id").value = id;
            		document.getElementById("update-name").value = name;
        			document.getElementById("update-type").value = type;
        			document.getElementById("update-action").value = "add";
        			document.getElementById("update-attr").value = "modelReference";
        			document.getElementById("update-value").value = iri;
        			ajaxHttpSend('updateWS', 'updateWS', addModelRederenceProcess);
            		$(this).dialog("close");
                },
                Cancel: function() {
                    $(this).dialog( "close" );
                }
            },
            close: function() {
            }
        });
        
        $("#ontrecdialogue").dialog({
            autoOpen: false,
            height: 260,
            width: 400,
            modal: true,
            buttons: {
                    "Load Ontology": function() {
                        var url = document.owlRecoForm.ontrecurl.value;
                        for (var index = 0; index < document.owlRecoForm.ontrecurl.length; index++) {
                            if (document.owlRecoForm.ontrecurl[index].checked) {
                                    url = document.owlRecoForm.ontrecurl[index].value;
                                     
                                    break;
                            }
                        }
                        $.ajax({
                            type: "POST",
                            url: "uploadOWL.jsp",
                            data: "ontoURL=" + url ,
                            async:false,
                            success: function(data,status,xmlhttp) {
                                $.isImport = 0;
                                loadowl();
                                
                            }
                        });
                        $('#pbar').append("<div id=\"progressbar\" style=\"width:172px;height:13px\"></div>");
                        $("#progressbar").progressbar({value: 100});       

                        
                        //$("#ontreccontent").replaceWith("<div id=\"ontreccontent\"></div>")
                        //document.owlRecoForm.submit();
                        
                        $(this).dialog("close");
                    },Cancel: function() {
                            $( this ).dialog( "close" );
                    }
            },
            close: function() {
                    //allFields.val( "" ).removeClass( "ui-state-error" );
            }
        });
        
        $("#SchemaMapping").dialog({
            autoOpen: false,
            height: 300,
            width: 350,
            modal: true,
            buttons: {
                    "Add SchemaMapper": function() {
                    	var attr = "";
                        if (document.getElementById('loweringSchemaMapping').checked){
                        	attr = "loweringSchemaMapping";
                        }else if (document.getElementById('liftingSchemaMapping').checked){
                        	attr = "liftingSchemaMapping";
                        }
                        var id = document.getElementById('mapper-eleId').value;
                        var value = document.getElementById('mapper-url').value;
                        if (attr != ""){
                        	document.getElementById("update-id").value = id;
                			document.getElementById("update-type").value = "simple";
                			document.getElementById("update-action").value = "add";
                			document.getElementById("update-attr").value = attr;
                			document.getElementById("update-value").value = value;
                			ajaxHttpSend('updateWS', 'updateWS', addSchemaMappingProcess);
                        } 
                        $(this).dialog("close");
                    },
                    Cancel: function() {
                            $( this ).dialog( "close" );
                    }
            },
            close: function() {
                    
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
            height: 550,
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
        
        $("#saveDoc").dialog({
            autoOpen: false,
            modal: true,
            buttons: {
                    "Save": function() {
                    	if (document.getElementById("savetoDB").checked){
                    		ajaxHttpSend('saveToDB', 'updateWS', savetoDBProcess);
                    	}
                    	if (document.getElementById("savetoFile").checked){
                    		var save = document.getElementById("getSawsdlFile");
                        	simulateClick(save, "click");
                    	}
                    	$( this ).dialog( "close" );
                    },
                    Cancel: function() {
                    	$( this ).dialog( "close" );
                    }
            },
            close: function() {
            	document.getElementById("refreshAlert").value = "true";
            }
        });
        
        $("#inputdescription").dialog({
        	autoOpen: false,
            height: 300,
            width: 350,
            modal: true,
            buttons: {
				"Search": function() {
            		var descriptionText = document.getElementById('descriptionText').value;
            		document.getElementById('suggest-EleDoc').value = descriptionText;
            		$(this).dialog("close");
            		recommendConcept();
                },
                Cancel: function() {
                    $(this).dialog( "close" );
                }
            },
            close: function() {
            }
        });
    
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
	
	function refreshAlert(){
		var result = document.getElementById("refreshAlert").value;
		if (result == "true") return result;
	}
	      
	function getTabIndex(){
		// check the active tab
		var ulelement = document.getElementById("owltabs").firstChild;
		var lielements = ulelement.childNodes;
		for (var index = 0; index < lielements.length; index++) {
			var className = lielements[index].getAttribute("class");
			if (className != ''){
				//alert("index = " + index);
				document.getElementById("input-ontology-tab").value = index;
			}
		}
	}
	
	function urlParam(name){
        	var results = new RegExp('[\\?&]' + name + '=([^&#]*)').exec(window.location.href);
        	if(results != null){
        		return results[1] || null;
			}else{
				return null;
			}
		
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
    <script type="text/javascript" language="javascript" src="./js/plugins/jquery.effects.core.js"></script>
    <script type="text/javascript" language="javascript" src="./js/plugins/jquery.effects.clip.js"></script>
    <script type="text/javascript" language="javascript" src="./js/plugins/jquery.ui.position.js"></script>
    <script type="text/javascript" language="javascript" src="./js/plugins/jquery.ui.resizable.js"></script>
    <script type="text/javascript" language="javascript" src="./js/plugins/jquery.ui.dialog.js"></script>
    <script type="text/javascript" language="javascript" src="./js/plugins/jquery.tinyscrollbar.js"></script>
    <script type="text/javascript" language="javascript" src="./js/plugins/jquery.scrollTo.js"></script>
	<script type="text/javascript" language="javascript" src="./js/plugins/jquery.ui.autocomplete.js"></script>
	
	<script type="text/javascript" language="javascript" src="./js/radiantweb/radiant.js"></script>
	<script type="text/javascript" language="javascript" src="./js/radiantweb/ontology.js"></script>
	<script type="text/javascript" language="javascript" src="./js/radiantweb/treeviewAction.js"></script>
	<script type="text/javascript" language="javascript" src="./js/radiantweb/wsdl.js"></script>
	
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
	//jQuery.struts2_jquery.require("js/struts2/jquery.ui.struts2-3.3.0.min.js");
});
</script>
       
<style>
	.ui-progressbar-value { background-image: url(./styles/themes/ui-lightness/images/3574351118_229c71823d_o.gif); }
</style>

<style type="text/css">
	input.hide1
	{
		position:relative;
		left:-165px;
		-moz-opacity:0;
		filter:alpha(opacity:0);
		opacity: 0;
		z-index: 2;
		width: 72px;
	}
	input.hide2
	{
		position:relative;
		left:-290px;
		-moz-opacity:0;
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
	.complex{padding:2px;background:#33FF66;color: black}
	.element{padding:2px;background:#CCCC99;color: black}
	.wsdltagname{color:#C28585;font-weight: bold;}
	.wsdlattrname{font-weight: bold}
	.wsdlattrvalue{color:royalblue}
	.wsdlannotation{color:#5D781D;font-weight: bold}
	
</style>

<script language="javascript" src="./scripts/jBox.js"></script>

</head>

<body>
<script type="text/javascript">


</script>
<input id="refreshAlert" type="hidden" value="true" />
<input id="OntologyTabIndex" type="hidden" value="0" />
<div id="main" style="width:98%; height:auto">
	<div style="float:left;"><a href="listUserServices.action" style="font-size: 150%;">Go Back to Home</a></div>
	<div style="width:100%; height:50px; text-align:center; vertical-align:middle; border-bottom: 1px solid #C5C5C5" >
        <h1><i>Radiant Web - Semantic Annotation Tool</i></h1>
    </div>
    
    <div id="wsdl" class='ui-widget-content ui-corner-all' style="width:60%; height:660px; float:left; margin-top: 8px; margin-right:0.3em; margin-left:0.3em; margin-bottom: 6px ">
        <s:if test="#session.login == 'true'">
        <div style="width:100%; height:30px; text-align:center; vertical-align:middle; border-bottom: 1px solid #C5C5C5" >
            <h3><i>WSDL/WADL Viewer</i></h3>
        </div>
        <div style="width:100%; height:auto; float:left; padding:4px;">
            <s:form id="wsdl_form" action="loadWS" theme="simple" method="POST" enctype="multipart/form-data">
				<div id="ws-location" style="width:100%; height:auto; float:left; padding:4px;">
					WSDL location <input type="text" id="wsloc" name="wsloc" size="50" >
				</div>
				<input type="button" style='z-index:1;width:80px;height:24px;font-size:12px;' value="Browse" id="owlfilebutton" onclick="hitFileBtn('WSFile');">
				<input type="submit" id="submit_ws_ajax" value="Load Web Service" style="width: 118px;height: 24px;"/>
				<input type="file" style="display: none;" id="WSFile" name="WSFile" size="1" onchange="selectfile('WSFile','wsloc')"/>
				<span><a id="sample_ws" href="#">Load sample Web Service</a></span>
				<!-- ajax action for upload file or pass url -->
				<script type='text/javascript'>
					jQuery(document).ready(function () { 
						var options_submit_ajax = {};
						options_submit_ajax.jqueryaction = "button";
						options_submit_ajax.id = "submit_ws_ajax";	// submit button id
						options_submit_ajax.onbef = "before_ws";	// the function before send ajax (usually set picture)
						options_submit_ajax.oncom = "complete_ws";	// the function after response 
						options_submit_ajax.targets = "myAjaxTarget1";	// used to store the response text 
						options_submit_ajax.href = "#";
						options_submit_ajax.formids = "wsdl_form";	// submit form id
						jQuery.struts2_jquery.bind(jQuery('#submit_ws_ajax'),options_submit_ajax);
						
						if(urlParam('name') !=null){
							$("#wsloc").val("db:" +decodeURIComponent(urlParam('name')));
							simulateClick(submit_ws_ajax, "click");
						}
						
						$("#sample_ws").click(function() {
							$("#wsloc").val("http://www.ebi.ac.uk/Tools/services/soap/wublast?wsdl");
							simulateClick(submit_ws_ajax, "click");
						});
					});  
				</script>
			</s:form>
			<div id="myAjaxTarget1" style="display:none;" >
			</div>
		</div>
        <center><div id="pbar1"></div></center>
        <div id="loadws" style="width:100%; overflow:auto; ">
        
        </div>
        
        <div id="ontrecdialogue" title="Ontology recommendation">
			<div id="ontreccontent">
            	<form id='owlRecoForm' name='owlRecoForm' action='upload.jsp' method="post" enctype="multipart/form-data">
                	<p>Choose an ontology to load</p>
                    <fieldset id="owlRecoFieldset"></fieldset>
            	</form>
            </div>
        </div>
        
        <div id="recommendform" title="Recommended Annotations"><p>Hello</p></div>
        
        <div id="inputdescription" title="Input document">
			<form>
				<label for="name">Concept description from the web service descriptor</label>
				<textarea name="text" id="descriptionText" cols="43"; rows="10" style="resize: none" ></textarea><br/>
				<label>Click <b>search</b> to get recommendations from the ontology</label>
			</form>
		</div>
        
        <div id="SchemaMapping" title="Schema Mapper">
        	<p>Schema Mapping Annotation</p>
            <form name="schemaMapping">
            	<fieldset>
                	Lowering Schema Mapping<input id="loweringSchemaMapping" name="option" value="loweringSchemaMapping" type="radio"/><br>
                    Lifting Schema Mapping<input id="liftingSchemaMapping" name="option" value="liftingSchemaMapping" type="radio"/><br>
                    Url : <input id="mapper-url" name="mapper-url" type="text" />
                    <input id="mapper-eleId" value="" type="hidden" />
                </fieldset>
           	</form>
		</div>
        
        <div id="saveDoc" title="Save Document">
			<p>Save the Document to</p>
			<form name="savedoc">
				<fieldset>
                	Repository(database) <input id="savetoDB" name="1" value="Repository" type="checkbox"/><br>
                    File <input id="savetoFile" name="1" value="File" type="checkbox" /><br>
               	</fieldset>
            </form>
            <div style="display:none;" >
            	<s:url id="exportSawsdlFile" namespace="/" action="exportSawsdlFile" ></s:url>
				<s:a id="getSawsdlFile" href="%{exportSawsdlFile}">myfile.sawsdl</s:a>
            </div>
        </div>
        </s:if>
    	<s:else>
    		<h2><font color="#FF0000" > Please login first!</font></h2>
    	</s:else>
    </div>
    
    <div id="owl" class='ui-widget-content ui-corner-all' style= "width:38%; height:660px; margin-left:0.3em; margin-right:0.3em; float:left; margin-top: 8px; margin-bottom: 6px">
		<s:if test="#session.login == 'true'">
		<div style="width:100%; height:30px; text-align:center; vertical-align:middle; border-bottom: 1px solid #C5C5C5" >
			<h3><i>Ontology Viewer</i></h3>
		</div>
		<div style="width:100%; padding:4px;">
			<s:form id="owl_form" action="loadOWL" theme="simple" method="POST" enctype="multipart/form-data">
				<div id="owl-location" style="width:100%; height:auto; float:left; padding:4px;">
					Ontology OWL file Location <input type="text" id="owlloc" name="owlloc" size="38" >
				</div>
				<input type="hidden" name="page" value="wsdl" />
				<input type="button" style='z-index:1;width:80px;height:24px;font-size:12px;' value="Browse" id="owlfilebutton" onclick="hitFileBtn('OWLFile');" >
				<input type="submit" id="submit_owl_ajax" value="Load Ontology" style="width:102px;height:24px;" onclick="getTabIndex();" />
<!-- 				<input id="recommend-ontology" type="button" style='z-index:1; width:120px; height:24px; font-size:12px; opacity: 0;' value="Recommend Ontology" > -->
				<input type="file" style="display: none;" id="OWLFile" name="OWLFile" size="1" onchange="selectfile('OWLFile','owlloc')"/>
				<input type="hidden" id="input-ontology-tab" name="tabIndex" value="0" />
				<span><a id="sample_owl" href="#">Load Sample Ontology</a></span>
				<!-- ajax action for upload file or pass url -->
				<script type='text/javascript'>
					jQuery(document).ready(function () { 
						var options_submit_ajax = {};
						options_submit_ajax.jqueryaction = "button";
						options_submit_ajax.id = "submit_owl_ajax";	// submit button id
						options_submit_ajax.onbef = "before_owl";	// the function before send ajax (usually set picture)
						options_submit_ajax.oncom = "complete_owl";	// the function after response 
						options_submit_ajax.targets = "myAjaxTarget2";	// used to store the response text 
						options_submit_ajax.href = "#";
						options_submit_ajax.formids = "owl_form";	// submit form id
						options_submit_ajax.timeout = "30000";	// cancel request after 30 seconds.
						
						jQuery.struts2_jquery.bind(jQuery('#submit_owl_ajax'),options_submit_ajax);
						
						$("#sample_owl").click(function() {
							$("#owlloc").val("http://purl.obolibrary.org/obo/obi/webService.owl");
							simulateClick(submit_owl_ajax, "click");
						});
						
					});  
				</script>
			</s:form>
			<div id="myAjaxTarget2" style="display:none;" >
			</div>
		</div>
		
		<form id="loadOwlHierarchy" method="post" >
			<input id="loadOwlisImport" type="hidden" name="importMsg" value="" >
			<input type="hidden" id="loadOwlloc" name="owlloc" value="" >
		</form>
        
        
        <center><div id="pbar2"></div></center>
        
        <div id="ontologyinfo" class="ui-widget-content ui-corner-all ui-state-default" style="width:96%; margin:3px;padding:3px"></div>
       	
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
    <div style="width:100%;margin-top: 6px; height:20px; text-align: center; float: left ;vertical-align:middle; border-top: 1px solid #C5C5C5"><br>Version 1.0 The project is developed at <a href="http://uga.edu/">The University of Georgia</a>. For feedback and suggestions <a href="http://mango.ctegd.uga.edu/jkissingLab/SWS/RadiantWeb/index.html">contact us</a></div>
	<div style="display:none;" >
		<input type="hidden" id="doc-type" name="type" value="" >
		<!-- for load wsdl/wadl xml -->
		<form name="loadWSXml" id="loadWSXml" method="post" >
			<input type="text" id="ws-type" name="type" value="" >
		</form>
		<!-- remove annotation -->
		<form name="updateWS" id="updateWS" method="post" >
			<input type="text" id="update-type" name="type" value="" >
			<input type="text" id="update-action" name="action" value="" >
			<input type="text" id="update-id" name="id" value="" >
			<input type="text" id="update-name" name="name" value="" >
			<input type="text" id="update-attr" name="attribute" value="" >
			<input type="text" id="update-value" name="value" value="" >
		</form>
		<!-- save to database -->
		<form name="saveToDB" id="saveToDB" method="post" >
			<input type="text" id="saveToDB-type" name="type" value="" >
		</form>
		<!-- recommend concept -->
		<form id="recommendConceptFrom" name="recommendConceptFrom" method="post">
			<input id="suggest-EleID" name="EleID" type="hidden" value="" />
       	  	<input id="suggest-EleName" name="EleName" type="hidden" value="" />
       	  	<input id="suggest-EleType" name="EleType" type="hidden" value="" />
       	  	<input id="suggest-EleDoc" name="EleDoc" type="hidden" value="" />
       	  	<input id="suggest-Ontology-Tab" name="tabIndex" type="hidden" value="0" />
		</form>
		<!-- get suggestion list of concept label -->
		<s:form id="suggest-form" action="loadSuggest" theme="simple" method="POST" >
			<input id="suggest-baseWord" name="baseWord" type="hidden" value="" />
		</s:form>
		<!-- search ontology -->
		<form id="searchOntologyTerm" name="searchOntologyTerm" method="post"> 
       	  	<input id="searchOntology-term" name="term" type="hidden" value="" />
       	</form>
       	<!-- set ontology operation and paramter super class -->
		<form id="SetOntologyOptions" name="SetOntologyOptions" method="post"> 
       	  	<input id="setOntology-Operation" name="operation" type="hidden" value="" />
       	  	<input id="setOntology-Param" name="param" type="hidden" value="" />
       	</form>
	</div>   
</div>
</body>
</html>



