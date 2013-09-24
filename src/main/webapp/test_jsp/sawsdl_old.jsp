<%--
    The main page that displays the WSDL and ontology. 
    It requests all the funtions of the tool and is updated throough ajax for every request.
    Document   : index
    Created on : May 26, 2011, 4:32:06 PM
    Author     : Chaitanya Guttula
    License    : MIT style license file
    
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="s" uri="/struts-tags"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Radiant Web Annotation Tool</title>
        
<script type="text/javascript">
	//It fefines the functions that would be called when wsdl_form and owl_form are submitted.
    //It basically assigns the actions of the form to target of 2 hidden frames in the page, due to which the file
    //can be uploaded to the server without (apparent) page refresh; as there seems to be no straight forward way for an
    //ajaxed file upload; The targets for the forms that is uploadOWL.jsp and uploadWSDL.jsp have the logic for uploading the respective files to the server.
    //Also you can see onload functions are called when the file iframe loads(i.e. file is uploaded)
    //Definitions for these functions can be found below
	function init()
	{
		var prev_id;
		window.onbeforeunload = function()
		{
			return "The data will be lost. Do you really want to refresh";
        };
		
        /*
        //window.onunload = unloadPage;
		window.onunload =  sessioninvalidate;
		function unloadPage(){
        	alert("unload");
        }
		function sessioninvalidate(){
        	var nameEQ = "previd=";
        	var prev = "";
        	var ca = document.cookie.split(';');
        	for(var i=0;i < ca.length;i++) {
        		var c = ca[i];
        		while (c.charAt(0)==' ') c = c.substring(1,c.length);
        		if (c.indexOf(nameEQ) == 0) 
        			prev = c.substring(nameEQ.length,c.length);
        	}
        	$.ajax({
            	type: "POST",
            	url: "invalidateSession.jsp",
            	data: "&id="+pev,
            	success: function(data,status,xmlhttp) {
            		var res = xmlhttp.responseText.toString().trim();
            		alert(res);
            	}
            });
        }
		$("#progressdialogue").progressbar({value:100});
        //document.getElementById('owlImportForm').onsubmit=function(){
        document.getElementById("submitimportform").onclick=function()
        {
        	document.getElementById('owlImportForm').target = 'upload_target3'; //'upload_target' is the name of the iframe
        	$('#pbar').append("<div id=\"progressbar\" style=\"width:172px;height:13px\"></div>").fadeIn();
        	$("#progressbar").progressbar({value: 100});                 
        	$.isImport=1;
        	document.forms["owlImportForm"].submit();
        	document.getElementById("upload_target3").onload = loadowl;
        };
        document.getElementById('owl_form').onsubmit=function()
        {
            document.getElementById('owl_form').target = 'upload_target2'; //'upload_target' is the name of the iframe
            $('#pbar').append("<div id=\"progressbar\" style=\"width:172px;height:13px\"></div>");
            $("#ontologyinfo").hide();
            $('#definition').hide();
            $('#owltabs').hide();
            $("#progressbar").progressbar({value: 100});  
            $.importURlEncoded='';
            $.isImport=0;
            document.getElementById("upload_target2").onload = loadowl; 
        };
        $("#progressbardialogue").dialog({
            autoOpen: false,
            height: 70, 
            width: 200,
            modal: true,
            buttons: {
                    
                    
                }
        });
        */
        
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
            		
            		document.getElementById(tagid + 'ConceptHide').value = iri;
            		
            		var owlid = ReplaceAll(iri , "#", "_");
            		owlid = ReplaceAll(owlid , ":", "_");
            		owlid = ReplaceAll(owlid , "/", "_");
            		owlid = ReplaceAll(owlid , ".", "_");
            		
            		var divbar = document.getElementById(tagid + 'bar');
            		divbar.innerHTML = '&nbsp; &nbsp; <span onclick="openOWLNode(\'' + owlid + '\')"><b>concept:< ' + label + ' ></b></span>' + 
            			'<span class=\"ui-button ui-corner-all" style="background:#616D7E; color:white; text-align: center; width: 20px; margin: 2px; padding:1px" onclick="remove(this, \'' + wsdlType + '\', \'concept\');"><b> X </b></span>' ;
            		        
                    $(this).dialog("close");
                },
                Cancel: function() {
                    $(this).dialog( "close" );
                }
            },
            close: function() {
            }
        });
        
        document.getElementById('wsdl_form').onsubmit=function()
        {
        	$('#pbar1').append("<div id=\"progressbar\" style=\"width:172px;height:13px\"></div>").fadeIn();
        	$("#progressbar").progressbar({value: 100});
        	document.getElementById('wsdl_form').target = 'upload_target'; //'upload_target' is the name of the iframe
        	document.getElementById("upload_target").onload = loadwsdl;
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
        
        $("#ontrecdialogue").dialog({
            autoOpen: false,
            height: 260,
            width: 400,
            modal: true,
            buttons: {
                    "Load Ontology": function() {
                        var url = document.owlRecoForm.ontrecurl.value;
                        for (index=0; index < document.owlRecoForm.ontrecurl.length; index++) {
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
                          var value;
                          var annotType;
                          var val;
                          for (index=0; index < document.schemaMapping.schemaMapper.length; index++) {
                            if (document.schemaMapping.schemaMapper[index].checked) {
                                    annotType = document.schemaMapping.schemaMapper[index].value;
                            }
                          }
                          value = document.schemaMapping.mapperurl.value;
                          $.count = $.count+1;
                          val = encode64(value);
                          updateWSDL(wsdleleName, wsdleleType, wsdlelemsg, val, annotType, "annotation"+$.count, 'addSchemaMapper');
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
        
        $("#saveDoc").dialog({
            autoOpen: false,
            modal: true,
            buttons: {
                    "Save": function() {
                          var filepath='Not';
                          var saveTo;     //Flag reoresenting where to save the WSDL -
                                         // 1 - save in existdb only 
                                         // 2 - Save in file only
                                         // 3 - save in both existdb and file
                          var savetoFile = 0;
                          var savetoExist =0 ;
                          if(document.savedoc.savetoDB.checked)
                              savetoExist = 1;
                          if(document.savedoc.savetoFile.checked){
                              savetoFile = 1;
                              filepath = document.savedoc.saveWSDLFilePath.value;
                              alert("The file path is : "+filepath);
                          }
                          if(savetoExist == 1 && savetoFile == 0)
                              saveTo = 1;
                          else
                          if(savetoExist==0 && savetoFile ==1)
                              saveTo = 2;
                          else
                          if(savetoExist == 1 && savetoFile == 1)
                              saveTo = 3;
                          
                          alert("save to : "+saveTo);
                          saveWSDL(saveTo,filepath);
                          
                          $(this).dialog("close");

                    },
                    Cancel: function() {
                            $( this ).dialog( "close" );
                    }
            },
            close: function() {
            
            }
        });

    }
    window.onload=init;
</script>
        
<script type="text/javascript">

	function unloadPage(){
    	alert("unload event detected!");
    }
    
	function selectfile(fileId,textId){
    	var val = $('#'+fileId).val();
    	var x = val.toString().split("\\");
    	$('#'+textId).val(x[x.length-1]);
    }  
          
    var isopen=0;   
    var count = 0;
    var wsdleleName;
    var wsdleleType;
    var wsdlelemsg;
        
    var keyStr = "ABCDEFGHIJKLMNOP" +
    			"QRSTUVWXYZabcdef" +
                "ghijklmnopqrstuv" +
                "wxyz0123456789+/" +
                "=";
	
    function encode64(input) {
    	var output = "";
		var chr1, chr2, chr3 = "";
		var enc1, enc2, enc3, enc4 = "";
		var i = 0;
		do {
			chr1 = input.charCodeAt(i++);
			chr2 = input.charCodeAt(i++);
			chr3 = input.charCodeAt(i++);
			enc1 = chr1 >> 2;
			enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
			enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
			enc4 = chr3 & 63;
			if (isNaN(chr2)) {
				enc3 = enc4 = 64;
			} else if (isNaN(chr3)) {
				enc4 = 64;
			}
         
			output = output +
					keyStr.charAt(enc1) +
					keyStr.charAt(enc2) +
					keyStr.charAt(enc3) +
					keyStr.charAt(enc4);
			chr1 = chr2 = chr3 = "";
			enc1 = enc2 = enc3 = enc4 = "";

		} while (i < input.length);
		return output;
    }
   
   
 	/*
	 * Function for saving WSDL to ExistDB
     *
     **/
    function saveWSDL(){
		 var xmlhttp;
         if (window.XMLHttpRequest){	// code for IE7+, Firefox, Chrome, Opera, Safari
             xmlhttp=new XMLHttpRequest();
         }else{							// code for IE6, IE5
             xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
         }

         xmlhttp.onreadystatechange = function(){
             if (xmlhttp.readyState==4 && xmlhttp.status==200)
             {
                 var msg = xmlhttp.responseText.toString().trim();
                 alert(msg); 
             }
         };
         xmlhttp.open("POST","saveWSDL.jsp" ,true);
         xmlhttp.send();
   }
   
   /*
    * Function for searching the ontology Called when the searhc icon on the 
    * ontology viewer is clicked. It makes ajax call to the searchOntology.jsp
    */
   function searchOntology(){
       var searchterm = document.getElementById("ontsearchterm").value;
       var xmlhttp;
       if (window.XMLHttpRequest){		// code for IE7+, Firefox, Chrome, Opera, Safari          
    	   xmlhttp=new XMLHttpRequest();
       }else{// code for IE6, IE5
    	   xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
       }

       xmlhttp.onreadystatechange=function()
       {
           if (xmlhttp.readyState==4 && xmlhttp.status==200)
           {
              var msg = xmlhttp.responseText.toString();
              var id;
              document.getElementById("searchResults").innerHTML=msg;
              $("#searchOntologyDialog").dialog("open");
              $("#searchReslutSelectable li.ontologySearchResullts").each(function(){
            	  //alert(this);
            	  $(this).dblclick(function(){
            		  var id = $(this).attr("data");
            		  $("#searchOntologyDialog").dialog("close");
            		  openOWLNode(id);
            	  });
              });
              
              $("#searchReslutSelectable").selectable({
            	  filter:'li',
            	  selected: function(){
            		  id = $("#searchReslutSelectable li.ui-selected").attr("data");
            	  }
              });
           }
        };
        xmlhttp.open("POST","searchOntology.jsp?term="+searchterm,true);
        xmlhttp.send();
   }
   
   /*
    * Function for setting the ontology options i.e. setting the operation and parametr iri/label for
    * using in recommending the terms. Makes ajax call to setOntologyOptions.jsp
    */
   function setOntologyOptions(){
       var op = encode64(document.getElementById("operationOption").value);
       var param = encode64(document.getElementById("parameterOption").value);
       var xmlhttp;
       if (window.XMLHttpRequest){		// code for IE7+, Firefox, Chrome, Opera, Safari          
    	   xmlhttp=new XMLHttpRequest();
       }else{							// code for IE6, IE5
    	   xmlhttp=new ActiveXObject("Microsoft.XMLHTTP"); 
       }
       xmlhttp.onreadystatechange=function()
       {
          if (xmlhttp.readyState==4 && xmlhttp.status==200){
        	  var msg = xmlhttp.responseText.toString();
              var index = msg.toString().indexOf("<->");
              var op = msg.toString().substring(0, index).trim();
              var param = msg.toString().substring(index+3, msg.toString().length).trim();
              alert(op+"\n"+param);
          }
       };
       xmlhttp.open("POST","setOntologyOptions.jsp?op="+op+"&param="+param,true);
       xmlhttp.send();
   }
   
   
	/*
	 * Function to add the file path input type when the file option in the Save WSDL dialog box 
     * is checked.
     *
     **/
	var isad = 0;
	var isop = 0;
	function addinput(){
		var fieldset = $('#saveDoc fieldset')[0];
        if(isad == 0){
        	isad = 1;
        	isop = 1;
        	$(fieldset).append("<div id='savewsdlpath'>Path to save file<input type='text' name='saveWSDLFilePath' /></div>");
        }else{
            if(isop==1){
            	$('#savewsdlpath').hide();
            	isop = 0;
            }else{
                $('#savewsdlpath').show();
                isop = 1;
            }
        }
	}
   
	function addDefinition(definition,term){
		//alert("def"+definition);
		$("#def").replaceWith("<div id=\"def\" style=\"padding:4px;border-bottom:1px solid #c5c5c5\">Definition : "+term+"</div>");
		$('#defvalue').replaceWith("<div id='defvalue' style='padding:4px; height: 35px; overflow:auto'>"+definition+"</div>");
	}
        
        
	/*
     * The functions removes the annotation from the screen and then call the method updateWSDL method
     * which makes an ajax call for removing the annotation from the wsdl file
     *
     *
     **/
    function removeAnnotation(divid,owlid,type,count,remoradd){
    	 //alert("in remove annotation");
    	 var wsdlid = $("#"+divid).attr("value");
    	 //var parts = wsdlid.toString().split(":", 2);
    	 //var id = "#"+parts[1];
    	 //var olid = "#selectable"+count;
    	 $("#"+divid).remove();
    	 updateWSDL(wsdlid,owlid,type,count,remoradd);
    }
        
	function remove(id){
    	if(confirm("Do you want to remove the annotation")){
            $("#progressbardialogue").dialog("open");
            var owlid = $('#'+id).attr("value");
            var value = $('#'+id).parent().parent().attr("value");
            var cls = $('#'+id).attr("class");
            var msg='';
            var wsdlele='';
            var eleType='';
            var annotType='';
            if(value.toString().indexOf(":") != -1)
            {
                var parts = value.toString().split(":", 2);
                eleType = parts[0];
                if(eleType == "element"){
                    var ele = parts[1];
                    var eleparts = ele.toString().split(" ", 2);
                    wsdlele = eleparts[0];
                    msg = eleparts[1];
                    
                }else
                    wsdlele = parts[1];
            }
            if(cls.toString().indexOf("liftingSchemaMapping") != -1){
                annotType="liftingSchemaMapping";
            }
            else
                if(cls.toString().indexOf("loweringSchemaMapping") != -1){
                annotType="loweringSchemaMapping";
            }
            else
            {
                annotType="modelReference";
            }

            updateWSDL(wsdlele,eleType,msg,owlid,annotType,id,"remove");
    	}
    }
        
    function reject(id){
    	if(confirm("Do you want to reject the term for annotation")){
    		$("#"+id).parent().remove();
    	}
    }
        
    function approve(id){
    	if(confirm("Do you want to approve the term for annotation")){
    		$("#progressbardialogue").dialog("open");   
    		//alert("In approve");  
            //$('#'+id).removeClass("suggested");
            //$('#'+id).addClass("annotated");
            //$('#approve'+id).remove();
            //$('#reject'+id).remove();
            var owlid = $('#'+id).attr("value");
            var value = $('#'+id).parent().parent().attr("value");
            var annotType = "modelReference";
            var msg='';
            var wsdlele='';
            var eleType='';
            if(value.toString().indexOf(":") != -1)
            {
                var parts = value.toString().split(":", 2);
                eleType = parts[0];
                if(eleType == "element"){
                    var ele = parts[1];
                    var eleparts = ele.toString().split(" ", 2);
                    wsdlele = eleparts[0];
                    msg = eleparts[1];
                }else{
                    wsdlele = parts[1];
                }
            }
            updateWSDL(wsdlele,eleType,msg,owlid,annotType,id,"approve");
    	}
    }
        
	/*
	 *  The function makes an ajax call to recommendTermsfroWSDL.jsp. This jsp 
	 *  gets the recommended aterms for annotations for the whole WSDL. Thens adds the 
	 *  recommended term to the respective element and is diplayed in red color and the 
	 *  user is gien the option to accept or reject the annotations.
	 **/
	function recommendTerms(){
		if (confirm('Do you want recommended terms for all the elements of WSDL '))
		{
        	//$('#pbar1').append("<div id=\"progressbar\" style=\"width:172px;height:13px\"></div>").fadeIn();
        	$("#progressbardialogue").dialog("open");
        	//$("#progressbar").progressbar({value: 100});       
        	var xmlhttp;
        	if (window.XMLHttpRequest){			// code for IE7+, Firefox, Chrome, Opera, Safari
                xmlhttp=new XMLHttpRequest();
            }else{// code for IE6, IE5
                xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
            }

            xmlhttp.onreadystatechange=function()
            {
            	//alert(xmlhttp.readyState);
            	//$('<div id="overlay" />').appendTo("body").fadeIn('normal');
            	if (xmlhttp.readyState==4 && xmlhttp.status==200)
            	{
            		$("#progressbardialogue").dialog("close");
            		//$('#progressbar').remove();
            		var temp = xmlhttp.responseText;
            		//alert("Present : " + temp.toString().indexOf("Error:"));
            		if(temp.toString().indexOf("Error:") != -1){
            			alert("E: "+temp.toString().trim());
            			if(temp.toString().indexOf("Set the options",0) != -1 );
            			$("#setOptionsDialog").dialog("open");
            		}else{
                    	var recommendations = temp.toString().split("***");
                    	//alert(recommendations);
                    	var item1 = recommendations[0];
                    	var length = recommendations.length;
                    	if(item1.toString().indexOf("Error:",0) != -1){
                    		//out.println(item.toString().trim());
                    		alert("e: "+item1.toString().trim());
                    	}else{
                            //alert("inside");
                            for(item in recommendations){
                            	var rec = recommendations[item];
                                //alert(rec);
                                var recparts = rec.toString().split("->",3);
                                var terms = recparts[1].toString().split("**",2);    
                                var owlId = encode64(terms[1]);
                                var owlnodeid = recparts[2];
                                var owlLabel = terms[0];
                                var id = '';
                                if(recparts[0].indexOf("op:") != -1){
                                	var opparts = recparts[0].toString().split(":",2);
                                	//alert("Opparts after splitting : "+opparts);
                                	id = opparts[1]+"opannotation";
                                }else if(recparts[0].indexOf("msg:") != -1){
                                	var msgparts = recparts[0].toString().split(":",2);
                                	//alert("msgaprts after splitting : "+msgparts);
                                	id = msgparts[1]+"msgannotation";
                                }else if(recparts[0].indexOf("ele:") != -1){
                                	var eleparts = recparts[0].toString().split(":",2);
                                	id = eleparts[1]+"annotation";
                                }
                                $.count = $.count+1;
                                $('#'+id).append("<div style='margin:10px'><span id=\""+$.count+"\" class=\"suggested hove ui-widget-content ui-corner-all\" style='margin:10px' value='"+owlId+"' onclick=\"openOWLNode('"+owlnodeid+"')\">"+owlLabel+"</span><span id=\"approve"+$.count+"\" class='hove ui-icon ui-icon-circle-check' onclick=\"approve('"+$.count+"')\" style='float:left'></span><span id=\"reject"+$.count+"\" class='hove ui-icon ui-icon-circle-close'  style='float:left' onclick=\"reject('"+$.count+"')\"></span><div>");
                            }
                        }
                    }
                }
            };
            xmlhttp.open("POST","recommendTermsforWSDL.jsp",true);
            xmlhttp.send();
		}
	 }
        
        
        
        
	/* 
     * The function makes Ajax call o recommendAnnotation.jsp which performs the recommendation 
     * for the wsdl element and then opens a dailog box showing the recommended annotations
     * in the form of radio buttons. The user can select the concept and click Annotate
     * to annotate the element with the slected concept.
     *
     **/
     function recommend(wsdlElementName,wsdlElementType,wsdlEleDoc,message){
     	//alert("Provide recommended annotations for "+wsdlid);
        if (confirm('Provide recommended terms for '+wsdlElementName+' ?'))
        {
            //var url='updateWsdl.jsp?wsdlID='+wsdlID+'&mode='+mode+'&uri='+uri;
            //alert(url);
            //var eleparts = wsdlid.toString().split(":", 2);
            $("#progressbardialogue").dialog("open");
            var xmlhttp;
            if (window.XMLHttpRequest){			// code for IE7+, Firefox, Chrome, Opera, Safari
            	xmlhttp=new XMLHttpRequest();
            }else{								// code for IE6, IE5
                xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
            }

            xmlhttp.onreadystatechange=function(){
                //alert(xmlhttp.readyState);
                if (xmlhttp.readyState==4 && xmlhttp.status==200)
                {
                	var temp = xmlhttp.responseText.toString();
                	document.getElementById("recommendform").innerHTML=xmlhttp.responseText;
                	//alert(temp.toString().trim())
                	if(temp.toString().trim() == 'Error:ontology not loaded'){
                		$("#progressbardialogue").dialog("close");
                		alert('Ontology not loaded');
                	}else{
                        $("#progressbardialogue").dialog("close");
                        $('#recommendform').dialog("open");
                    }
                }
            };
            xmlhttp.open("POST","recommendAnnotation.jsp?wsdlEleName="+wsdlElementName+"&wsdlEleType="+wsdlElementType+"&wsdlEleDoc="+wsdlEleDoc+"&message="+message,true);
            xmlhttp.send();
        }
     }
        
	/*
     * The function to add lifting or lowering schema to the WSDL doc. This function opens a dialog box which 
     * takes the url of the mapper. The URL should be available on the Web. Then a call is made to the 
     * updateWSDL function which actually adds the schemaMapper to the WSDL file.
     *
     *
     **/
    function addSchemaMapper(wsdlElename,wsdlEleType,wsdlmsg){
    	wsdleleName = wsdlElename;
     	wsdleleType = wsdlEleType;
     	wsdlelemsg = wsdlmsg;
     	$("#SchemaMapping").dialog("open");
    }
        
	/*
     * Function loads the WSDL file using Ajax call to the ajaxFetch.jsp 
     *
     *
     **/    
    function loadwsdl(){
        var xmlhttp;
        if (window.XMLHttpRequest){			// code for IE7+, Firefox, Chrome, Opera, Safari
        	xmlhttp=new XMLHttpRequest();
        }else{								// code for IE6, IE5
            xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
        }
        xmlhttp.onreadystatechange=function()
        {
            if (xmlhttp.readyState==4 && xmlhttp.status==200)
            {
            	var temp = xmlhttp.responseText;
            	var len = temp.length;
            	var count = 0;
                document.getElementById("loadwsdl").innerHTML=temp;
            
                $(document).ready(function(){
                
                	/*
                     * The function making the wsdl in the form of a tree Structure
                     *
                     **/
                    $("#wsdltree").treeview({
                    	persist: "location",
                    	animated: "slow",
                    	zIndex:1000,
                    	handle:'span',
                    	containment:'document',
                    	collapsed: true,
                    	unique: true
                    });
                    $("#download").hide();
                    //.bind("loaded.jstree", function (event, data) { alert("tree loaded");});
                    
                    /*
                     * Switching WSDL view between XML view and the tree view
                     */
                    $("#switchWSDLView").toggle(function(){
						$("#wsdlxmlview").append("<div id=\"wsdlxmlviewContainer\" class=\"ui-widget-content ui-coner-all\" style=\"overflow:auto;margin:6px; height:315px; padding:10px\"></div>");
                        var jqxhr = $.ajax({
                            type:'get',
                            url:'ajaxFetch.jsp',
                            data:'param=wsdlxml',
                            success:function(result){
                               $(result).appendTo('#wsdlxmlviewContainer');
                            }
						});
                        
                        $("#switchWSDLView").text("WSDLTree View");
                        $("#wsdltree").hide();
                        
                    }, function(){
                    	$("#wsdltree").show();
                    	$("#switchWSDLView").text("WSDLXML View");
                    	$("#wsdlxmlviewContainer").remove();
                    });
                        
                    /*
                     * Adding legend to the page
                     */
                     $('#legend').replaceWith("<div style=\"width:99%;float:left;height:54px\" class=\"ui-widget-content ui-corner-all\"><div style=\"width:99%; height:18px;border-bottom: 1px solid #C5C5C5;padding-left:10px;padding-top:2px\"><i><b>Legend</b></i></div><div style=\"padding:2px\"><span style=\"padding:2px\"><span class=\"ui-icon ui-icon-circle-check\" style=\"float:left\"></span>" + 
                     							"<span style=\"float:left\">  Approve Suggested Term</span></span><span style=\"padding:2px\"><span class=\"ui-icon ui-icon-circle-close\" style=\"float:left\">\" \"</span><span style=\"float:left\"> Reject Suggested Term</span></span><span style=\"padding:2px\"><span class=\"ui-icon ui-icon-close\" style=\"float:left\"></span>" + 
                     							"<span style=\"float:left\"> Remove annotation</span></span><span style=\"padding:2px\"><span style=\"background:#C28585;color:#C28585;margin:2px\">\" \"</span><span> Suggested Term</span></span><span style=\"padding:2px\"><span style=\"background:#5E7D7E;color:#5E7D7E;margin:2px\">\" \"</span><span> Pre-existing annotation</span></span>" + 
                     							"<span style=\"padding:2px\"><span style=\"background:#99CC99;color:#99CC99;margin:2px\">\" \"</span><span> Approved Term</span></span><span style=\"padding:2px\"><span style=\"background:#FFCC99;color:#FFCC99;margin:2px\">\" \"</span><span> Operation </span></span><span style=\"padding:2px\"><span style=\"background:#C0C0C0;color:#C0C0C0;margin:2px\">\" \"</span>" + 
                     							"<span> Messages (Input/Output)</span></span><span style=\"padding:2px\"><span style=\"background:#CCCC99;color:#CC9;margin:2px\">\" \"</span><span> Parameters (Inputs/Outputs)</span></span></div></div>");

                    /*
                     * The function adds drop down to the annotations.
                     * The dropdown is for removing the annotation.
                     *
                     **/
                    $.isOpen = [];
                    $.isAdded = [];
                        
                    $("#dialog").dialog({autoOpen : false});
                    $('div span.annotated').each(function(index) {
                        count = index;
                        $.count = index;
                        //alert(index);
                        $.isOpen[index] = 0;
                        $.isAdded[index]=0;
                        var id = "#"+this.id;
                        //$.owlids[index] = $(id).attr("value");
                        //$.wsdlids[index] = $(id).parent().attr("value");
                        var owlid = $(id).attr("value");
                        //var wsdlid = $(id).parent().attr("value");
                        var type = $(id).parent().attr("class");
                        var divid = $(id).parent().attr("id");
                        //alert("divid : "+divid);
                        //alert("wsdlid : "+wsdlid + " owlid : "+owlid);
                        $(this).click(function(){
                        	//alert("current : "+$.isOpen[index])
                        	//var id;
                        	if($.isAdded[index]==0){
                        		$.isAdded[index]=1;
                        		$.isOpen[index]=1;
                        		$(this).parent().append("<ol id=\"selectable"+index+"\" class=\"selectable\">\n\
                        									<p class='ui-widget-content ui-conrners-all hove ui-state-default' \n\
                                                            style='width:120px;text-align:center;margin:5px' \n\
                                                            onClick=\"removeAnnotation('"+divid+"','"+owlid.toString().trim()+
                                                            "','"+type+"',"+index+",'remove')\">Remove</p></ol>");
    						}else{
                        		if($.isOpen[index] == 0){
                                	$.isOpen[index]=1;
                                	$("#selectable"+index).show("clip");
                        		}else if($.isOpen[index]==1){
                        			$.isOpen[index] = 0;
                        			$('#selectable'+index).hide('fast');
                        		}
                        	}
                        });
                    });
                        
                    /*
                     * Code for opening the intial tree branch
                     *
                     **/
                    //alert(this.parent);
                    $(".portType").each(function(){
                    	var id = $("#"+this.id).parent().attr("id");
                    	//alert(id);
                    	expandBranch(id);
                    });
                        
                        
					/*
                     * This deals with the Drop part of the drag and drag
                     **/             
                    $('#wsdltree .drop').droppable({
                    	tolerence:'touch',
                    	drop:function(){
                    		//alert("In drop")
                    		$("#progressbardialogue").dialog("open");
                    		var id = this.id;
                    		var isopen = 0;
                    		var isadded = 0;
                    		var message = '';
                    		var eleName = '';
                    		var element = $("#"+this.id).attr("value");
                    		var owllabel = $.draggingOWLNodeLabel;
                    		//alert("In drop function : "+id+" - "+$.draggingOWLNodeLabel);
                    		count = count+1;
                    		$.count = $.count+1;
                    		if(element.toString().indexOf(":") != -1){
                    			//alert('contians : ');
                    			var eleParts = element.toString().split(":", 2);
                    			element = eleParts[0];
                    			//message = eleParts[1];
                    			if(element=='element'){
                    				var parts = eleParts[1].split(" ", 2);
                    				message = parts[1];
                    				eleName = parts[0];
                    				//alert("Mesaage : "+message);
                    			}else{
                    				eleName=eleParts[1];
                    				//element = ;
                    			}
                    		}
                    		updateWSDL(eleName,element,message,parent.draggingID,'modelReference',count,'add',owllabel,$.draggingOWLNodeId);
                    	}
                    });
                });
				
                // Adding recommend ontology to the ontology viewer
                //$("#ontologyrecommender").
                $("#recommendOntology").replaceWith("<span id=\"ontrec\" onclick=\"recommendOntologies('"+sesid+"')\" class=\"ui-button ui-corner-all\" style=\"color:white;background:#616D7E;padding:3px;margin-left:3px;margin-right:3px;\">Recommend Ontologies</span>");
                $('#save').click(function(){
                	//alert('Saving WSDL');
                	saveWSDL();
                });
                $("#progressbar").remove();    
            }
        };
        xmlhttp.open("POST","ajaxFetch.jsp?param=wsdl",true);
        xmlhttp.send();
    }
    
</script>
<script type="text/javascript" src="./scripts_new/UpdateWSDL.js"></script>
<script type="text/javascript" src="./scripts_new/recommendOnt.js"></script>

	<link rel="stylesheet" href="./styles/jquery.treeview.css"/>
    <link rel="stylesheet" href="./styles/screen.css"/>
    <link rel="stylesheet" media="screen" type="text/css" href="./styles/kltooltips.css" />
    <link rel="stylesheet" media="screen" type="text/css" href="./styles/example.css" />
    
    <script type="text/javascript" src="/TKSS/struts/js/base/jquery-1.7.1.min.js"></script>
	<script type="text/javascript" src="/TKSS/struts/js/base/jquery.ui.core.min.js?s2j=3.3.0"></script>
	<script type="text/javascript" src="/TKSS/struts/js/plugins/jquery.subscribe.min.js"></script>
	<script type="text/javascript" src="/TKSS/struts/js/struts2/jquery.struts2-3.3.0.min.js"></script>
        
	<script type="text/javascript" src="./scripts_new/tabber.js"></script>
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
    <script type="text/javascript" language="javascript" src="./scripts_new/kltooltips.evaluation.js"></script>
	
	<script type="text/javascript" language="javascript" src="./script/radiant.js"></script>
	<script type="text/javascript" language="javascript" src="./script/ontology.js"></script>
	<script type="text/javascript" language="javascript" src="./script/treeviewAction.js"></script>
	
	<link rel="stylesheet" href="./styles/jquery-ui/base/jquery.ui.all.css">
    <link rel="stylesheet" href="./styles/default.css">
    <link rel="stylesheet" href="./styles/jBox.css">
    <link rel="stylesheet" href="./styles/themes/apple/style.css">
    <link rel="stylesheet" href="./styles/fixedMenu_style1.css">
    <link rel="stylesheet" href="./styles/css/website.css" type="text/css" media="screen"/>


<style type="text/css">
	input.hide1
	{
		position:relative;
		left:-162px;
		-moz-opacity:0 ;
		filter:alpha(opacity:0);
		opacity: 0;
		z-index: 2;
	    width: 70px;
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
        
</head>
<body>
<div id="main" style="width:98%; height:auto">
    <div style="width:100%; height:50px; text-align:center; vertical-align:middle; border-bottom: 1px solid #C5C5C5" >
        <h1><i>Radiant Web - Semantic Annotation Tool</i></h1>
    </div>
    
    <div id="wsdl" class='ui-widget-content ui-corner-all' style="width:60%; height:660px; float:left; margin-top: 8px; margin-right:0.3em; margin-left:0.3em; margin-bottom: 6px ">
        <div style="width:100%; height:30px; text-align:center; vertical-align:middle; border-bottom: 1px solid #C5C5C5" >
            <h3><i>WSDL/WADL Viewer</i></h3>
        </div>
        <div style="width:100%; height:auto; float:left; padding:4px;">
            <s:form id="wsdl_form" action="loadWS" theme="simple" method="POST" enctype="multipart/form-data">
				<div style="width:100%; height:auto; float:left; padding:4px;">
					WSDL location <input type="text" id="onlinewsdl" name="olwsdl" size="50" >
				</div>
				<input type="button" style='z-index:1;width:80px;height:24px;font-size:12px;' value="Browse" id="owlfilebutton">
				<input type="submit" id="submit_ws_ajax" value="Load WS" style="width:80px;"/>
				<input type="file" class="hide1" id="WSDLFile" name="WSDLFile" size="1" onchange="selectfile('WSDLFile','onlinewsdl')"/>
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
					 });  
				</script>
			</s:form>
			<div id="myAjaxTarget1" style="display:none;" >
			</div>
        </div>
        <br><br><br><br><hr>
        <center><div id="pbar1"></div></center>
        <div id="loadwsdl" style="width:100%; overflow:auto;">
        
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
        
        <div id="SchemaMapping" title="Schema Mapper">
        	<p>Schema Mapping Annotation</p>
            <form name="schemaMapping">
            	<fieldset>
                	Lowering Schema Mapping<input id="schemaMapper" name="1" value="loweringSchemaMapping" type="radio"/><br>
                    Lifting Schema Mapping<input id="schemaMapper" name="1" value="liftingSchemaMapping" type="radio"/><br>
                    Url : <input id="mapperurl" id="mapperurl" type="text" />
                </fieldset>
           	</form>
		</div>
        
        <div id="saveDoc" title="Save Document">
			<p>Save the Document to</p>
			<form name="savedoc">
				<fieldset>
                	Repository <input id="savetoDB" name="1" value="loweringSchemaMapping" type="checkbox"/><br>
                    File <input id="savetoFile" name="1" value="liftingSchemaMapping" type="checkbox" onclick="addinput()"/><br>
               	</fieldset>
            </form>
        </div>
    </div>

    <div id="owl" class='ui-widget-content ui-corner-all' style= "width:38%; height:660px; margin-left:0.3em; margin-right:0.3em; float:left; margin-top: 8px; margin-bottom: 6px">
		<div style="width:100%; height:30px; text-align:center; vertical-align:middle; border-bottom: 1px solid #C5C5C5" >
			<h3><i>Ontology Viewer</i></h3>
		</div>
		<div style="width:100%; height:auto; float:left; padding:4px;">
			<s:form id="owl_form" action="loadOWL" theme="simple" method="POST" enctype="multipart/form-data">
				<div style="width:100%; height:auto; float:left; padding:4px;">
					Owl Location <input type="text" id="owlloc" name="owlloc" size="38" >
				</div>
				<input type="button" style='z-index:1;width:80px;height:24px;font-size:12px;' value="Browse" id="owlfilebutton">
				<input type="submit" id="submit_owl_ajax" value="Load OWL" style="width:80px;"/>
				<input type="file" class="hide1" id="OWLFile" name="OWLFile" size="1" onchange="selectfile('OWLFile','owlloc')"/>
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
						
						jQuery.struts2_jquery.bind(jQuery('#submit_owl_ajax'),options_submit_ajax);
						
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
        <hr>
        
        <center><div id="pbar2"></div></center>
        
        <div id="ontologyinfo" class="ui-widget-content ui-corner-all ui-state-default" style="width:97%; margin:3px;padding:3px"></div>
       	
       	<div style="background-image:url('./styles/images/step1.gif');height: 70px; margin: 4px; " class="ui-widget-content ui-corner-all" id="definition">
        	<div style="padding:4px;border-bottom:1px solid #c5c5c5" id="def">Definition</div>
        	<div id="defvalue" style="padding:4px;overflow: auto"></div>
        </div>
        
        <div id="owltabs" class="tabber" style="padding:2px;">
            <div id="loadowl" class='tabbertab' style="height:425px; overflow: auto"><h2>Class</h2></div>
        	<div id="loadowlProp" class='tabbertab' style="height:408px; overflow: auto"><h2>ObjectProperty</h2></div>
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
            
	</div>
    <div id='legend'></div>
    <div style="width:100%;margin-top: 6px; height:20px; text-align: center; float: left ;vertical-align:middle; border-top: 1px solid #C5C5C5"><br>Version 1.0 The project is developed at <a href="http://uga.edu/">The University of Georgia</a>. For feedback and suggestions <a href="http://mango.ctegd.uga.edu/jkissingLab/SWS/RadiantWeb/index.html">contact us</a></div>
    </div>
</body>
</html>