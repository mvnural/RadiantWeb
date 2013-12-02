/**
 *  This script is used for action of wsdl annotation recommend, remove, add
 *  or Set the tree view of wsdl/wadl or load xml format of wsdl/wadl
 */	
	
	/* Function for set the tree view of wsdl/wadl
	 */
	function setWsHierarchy(responseText){
		try{
			var response = eval("(" + responseText + ")");
			var errormsg = response.errormsg;
			//alert(errormsg);
			if (errormsg == '' ){
				//alert(response.innerTreeHtml);
				document.getElementById("doc-type").value = response.type;
				document.getElementById("loadws").innerHTML = response.innerTreeHtml;
	            $(document).ready(function(){
	            	/*
                     * The function making the wsdl in the form of a tree Structure
                     *
                     **/
                    $("#wstree").treeview({
                    	persist: "location",
                    	animated: "slow",
                    	zIndex:1000,
                    	handle:'span',
                    	containment:'document',
                    	collapsed: true,
                    	unique: true
                    });
                    $("#download").hide();
                    
                    /*
                     * Switching WSDL view between XML view and the tree view
                     */
                    $("#switchWSView").toggle(function(){
						$("#wsxmlview").append("<div id=\"wsxmlviewContainer\" class=\"ui-widget-content ui-coner-all\" style=\" margin:6px; height:475px; padding:10px; overflow:auto; \"></div>");
						document.getElementById("ws-type").value = "wsdl";
						ajaxHttpSend('loadWSXml', 'loadWSXml', loadXmlProcess);
						$("#switchWSView").text("WSTree View");
                        $("#wstree").hide();
                    }, function(){
                    	$("#wstree").show();
                    	$("#switchWSView").text("WSXML View");
                    	$("#wsxmlviewContainer").remove();
                    });
                        
                    /*
                     * Adding legend to the page
                     */
                     $('#legend').replaceWith("<div style=\"width:99%;float:left;height:54px\" class=\"ui-widget-content ui-corner-all\">" +
                     							"<div style=\"width:99%; height:18px;border-bottom: 1px solid #C5C5C5;padding-left:10px;padding-top:2px\"><i><b>Legend</b></i></div>" +
                     								"<div style=\"padding:2px\">" +
                     									"<span style=\"padding:2px\"><span class=\"ui-icon ui-icon-circle-check\" style=\"float:left\"></span><span style=\"float:left\">  Approve Suggested Term</span></span>" +
                     									"<span style=\"padding:2px\"><span class=\"ui-icon ui-icon-circle-close\" style=\"float:left\">\" \"</span><span style=\"float:left\"> Reject Suggested Term</span></span>" +
                     									"<span style=\"padding:2px\"><span class=\"ui-icon ui-icon-close\" style=\"float:left\"></span><span style=\"float:left\"> Remove annotation</span></span>" +
                     									"<span style=\"padding:2px\"><span style=\"background:#C28585;color:#C28585;margin:2px\">\" \"</span><span> Suggested Term</span></span>" +
                     									"<span style=\"padding:2px\"><span style=\"background:#5E7D7E;color:#5E7D7E;margin:2px\">\" \"</span><span> Pre-existing annotation</span></span>" + 
                     									"<span style=\"padding:2px\"><span style=\"background:#99CC99;color:#99CC99;margin:2px\">\" \"</span><span> Approved Term</span></span>" +
                     									"<span style=\"padding:2px\"><span style=\"background:#FFCC99;color:#FFCC99;margin:2px\">\" \"</span><span> Operation </span></span>" +
                     									"<span style=\"padding:2px\"><span style=\"background:#C0C0C0;color:#C0C0C0;margin:2px\">\" \"</span><span> Messages (Input/Output)</span></span>" +
                     									"<span style=\"padding:2px\"><span style=\"background:#33FF66;color:#CC9;margin:2px\">\" \"</span><span> ComplexType (Inputs/Outputs)</span></span>" + 
                     									"<span style=\"padding:2px\"><span style=\"background:#CCCC99;color:#CC9;margin:2px\">\" \"</span><span> SimpleType (Inputs/Outputs)</span></span>" +
                     								"</div>" +
                     							"</div>");

                    /*
                     * The function adds drop down to the annotations.
                     * The dropdown is for removing the annotation.
                     **/
                    $.isOpen = [];
                    $.isAdded = [];
                        
                    $("#dialog").dialog({autoOpen : false});
                    $('div span.annotated').each(function(index) {
                        count = index;
                        $.count = index;
                        alert(index);
                        $.isOpen[index] = 0;
                        $.isAdded[index]=0;
                        var id = "#"+this.id;
                        var owlid = $(id).attr("value");
                        var type = $(id).parent().attr("class");
                        var divid = $(id).parent().attr("id");
                        //alert("divid : "+divid);
                        //alert("wsdlid : "+wsdlid + " owlid : "+owlid);
                        $(this).click(function(){
                        	//alert("current : "+$.isOpen[index])
                        	if($.isAdded[index]==0){
                        		$.isAdded[index]=1;
                        		$.isOpen[index]=1;
                        		$(this).parent().append("<ol id=\"selectable" + index + "\" class=\"selectable\">" + 
                        									"<p class='ui-widget-content ui-conrners-all hove ui-state-default'" +
                                                            "style='width:120px;text-align:center;margin:5px'" + 
                                                            "onClick=\"removeAnnotation('" + divid + "','" + owlid.toString().trim() +
                                                            "','" + type + "'," + index + ",'remove')\">Remove</p></ol>");
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
                    $('#wstree .drop').droppable({
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
                    		//updateWSDL(eleName,element,message,parent.draggingID,'modelReference',count,'add',owllabel,$.draggingOWLNodeId);
                    	}
                    });
                });
	            var owlloc = document.getElementById("owlloc").value;
//	            if (owlloc == ''){
//	            	document.getElementById("recommend-ontology").style.opacity = 100;
//	            }
	            
	            // set the droppable annotation tag 
	            var dropArr = document.getElementsByName('ws-droppable');
	            for (var i = 0; i < dropArr.length; i++) {
	            	setDroppable(dropArr[i].id);
	            }
	            

      	    }else{
      	    	alert(errormsg);
      	    }
		 }catch(err){
			 //alert("err = " + err);
			 alert("No file selected or file is over 10MB");
		 } 
  	}
    //before ws file upload process
	jQuery(document).subscribe('before_ws', function(event,data) {
		//alert("before");
		document.getElementById('pbar1').innerHTML = "<img src='images/processBar.gif' alt='loading... please wait' />";
	});
	//complete ws file upload process
	jQuery(document).subscribe('complete_ws', function(event,data) {
		//alert("complete");
	 	document.getElementById('pbar1').innerHTML = "";
	 	setWsHierarchy(event.originalEvent.request.responseText);
	});
	
	
	/**
	 * function for dropping the semantic concept then annotate with
	 */
	function setDroppable(id){
		$("#" + id).droppable({
			drop: function(event, ui) {
				var tempStr = this.id;
				var iri = ui.draggable.attr('title');
				var label = ui.draggable.text();
				var answer = confirm("annotate this with \"" + label + "\" ?");
				if (answer){
					// parse the id to get information
					var tempArr = tempStr.split("-");
					var id = tempArr[2];
					var name = tempArr[1];
					var type = tempArr[0];
					// put into ajax updateWS form
	        		document.getElementById("update-id").value = id;
            		document.getElementById("update-name").value = name;
        			document.getElementById("update-type").value = type;
        			document.getElementById("update-action").value = "add";
        			document.getElementById("update-attr").value = "modelReference";
        			document.getElementById("update-value").value = iri;
        			// call updateWs ajax
        			ajaxHttpSend('updateWS', 'updateWS', addModelRederenceProcess);
				}
			}
        });
	}
	
	
	
	/* Function for set the xml view of wsdl/wadl
	 */
	function loadXmlProcess(responseText){
		try{
			var response = eval("(" + responseText + ")");
			var errormsg = response.errormsg;
			//alert(errormsg);
			if (errormsg == '' ){
				//alert(response.innerXmlHtml);
				document.getElementById("wsxmlviewContainer").innerHTML = response.innerXmlHtml;
			}
		}catch(err){
			 alert("connection fails");
		}
	}
	
	/* open save dialog
	 */
	function save(){
		document.getElementById("refreshAlert").value = "false";
		$('#saveDoc').dialog("open");
	}
	
	/* save to DB
	 */
	function savetoDBProcess(responseText){
		try{
			
			var response = eval("(" + responseText + ")");
			var errormsg = response.errormsg;
			//alert(errormsg);
			if (errormsg == '' ){
				alert("Save to database successfully");
			}else{
				alert(errormsg);
			}
			
		}catch(err){
			 alert("connection fails");
		}
	}
	
	
	
	
	/* Function for remove wsdl/wadl annotation by id
	 */
	function removeElement(wsType, id, type, attr, value, divId){
		var answer = confirm("remove this annotation ?");
		if (answer){
			if (wsType == 'wsdl'){
				document.getElementById("update-id").value = id;
			}else if (wsType == 'wadl'){
				document.getElementById("update-name").value = id;
			}
			document.getElementById("update-type").value = type;
			document.getElementById("update-action").value = "remove";
			document.getElementById("update-attr").value = attr;
			document.getElementById("update-value").value = value;
			tagid = divId;
			ajaxHttpSend('updateWS', 'updateWS', removeProcess);
		}
    }
	function removeProcess(responseText){
		try{
			var response = eval("(" + responseText + ")");
			var errormsg = response.errormsg;
			//alert(errormsg);
			if (errormsg == '' ){
				var obj = document.getElementById(tagid);
				var removeTagParent = obj.parentNode;
	            removeTagParent.removeChild(obj);
	        }else{
				alert(errormsg);
			}
		}catch(err){
			 alert("connection fails");
		}
	}
	
	/* Function to add schema mapping 
	 **/
	function addSchemaMapper(id){
		document.getElementById("mapper-url").value = "";
		document.getElementById("mapper-eleId").value = id;
		$('#SchemaMapping').dialog("open");
	}
	function addSchemaMappingProcess(responseText){
		try{
			var response = eval("(" + responseText + ")");
			var errormsg = response.errormsg;
			//alert(errormsg);
			if (errormsg == '' ){
				var id = response.id;
				var type = response.type;
				var url = response.value;
				var attr = response.attribute;
				var objName = type + '-' + id + '-annotation';
				var arr = new Array(); 
	            arr = document.getElementsByName(objName); 
	            for (var i = 0; i < arr.length; i++) { 
	                var obj = document.getElementsByName(objName).item(i);
	                var divName = type + "-" + id + "-" + attr;
	                if (url != ''){
	                	var content = obj.innerHTML; 
	                	obj.innerHTML = content + '<div id="' + divName + '" name="' + divName + '" style="margin:10px" ><span class="annotated ui-widget-content ui-corner-all hove"><b>' + attr + " : " + url + '</b></span>' + 
										'<span class="hove ui-icon ui-icon-close" style="float:left" onclick="removeElement(' + id + ', \'' + type + '\', \'' + attr  + '\' , \'' + url + '\', \'' + divName + '\');"><b> X </b></span></div>' ;
	                }
	            }
			}else{
				alert(errormsg);
			}
		}catch(err){
			 alert("connection fails");
		}
	}
	
	
	
	/* Function to get recommend concept wsdl annotation by id
	 */
	function recommend(id, name, type, doc){
		var answer = confirm("Get recommendations for this annotation ?");
		if (answer){
			
			// check the active tab
			var ulelement = document.getElementById("owltabs").firstChild;
			var lielements = ulelement.childNodes;
			for (var index = 0; index < lielements.length; index++) {
				var className = lielements[index].getAttribute("class");
				if (className != ''){
					document.getElementById("suggest-Ontology-Tab").value = index;
				}
			}
			document.getElementById("suggest-EleID").value = id;
			document.getElementById("suggest-EleName").value = name;
			document.getElementById("suggest-EleType").value = type;
			document.getElementById("suggest-EleDoc").value = doc;
			document.getElementById("descriptionText").value = doc;
			$('#inputdescription').dialog("open");
		}
    }
	function recommendConcept(){
		ajaxHttpSend('recommendConcept', 'recommendConceptFrom', recommendConceptProcess);
	}
	function recommendConceptProcess(responseText){
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
			 alert("connection fails");
		}
	}
	function addModelRederenceProcess(responseText){
		try{
			var response = eval("(" + responseText + ")");
			var errormsg = response.errormsg;
			//alert("errormsg = " + errormsg);
			if (errormsg == '' ){
				
				var id = response.id;
				var name = response.name;
				var type = response.type;
				var iri = response.value;
				var attr = response.attribute;
				var label = response.label;
				var objName;
				if (id != 0){
					objName = type + '-' + id + '-annotation';
				}else{
					objName = type + '-' + name + '-annotation';
				}
				
				// replace all illegal characters
				var owlid = ReplaceAll(iri , "#", "_");
        		owlid = ReplaceAll(owlid , ":", "_");
        		owlid = ReplaceAll(owlid , "/", "_");
        		owlid = ReplaceAll(owlid , ".", "_");
				
				var arr = new Array(); 
	            arr = document.getElementsByName(objName); 
	            for (var i = 0; i < arr.length; i++) { 
	                var obj = document.getElementsByName(objName).item(i);
	                var divName;
	                if (id != 0 ){
	                	divName = type + "-" + id + "-" + attr;
	                }else{
	                	divName = type + "-" + name + "-" + attr;
	                }
	                if (iri != ''){
	                	var annotations = new Array(); 
	                	annotations = document.getElementsByName(divName);
	                	var total = annotations.length;
	                	var divId = divName + total;
	                	var content = obj.innerHTML;
	                	if (id != 0){
	                		obj.innerHTML = content + '<div id="' + divId + '" name="' + divName + '" style="margin:10px" ><span class="annotated ui-widget-content ui-corner-all hove" onclick="openOWLNode(\'' + owlid + '\')"><b>' + label + '</b></span>' + 
							'<span class="hove ui-icon ui-icon-close" style="float:left" onclick="removeElement(\'wsdl\', ' + id + ', \'' + type + '\', \'' + attr  + '\' , \'' + iri + '\', \'' + divId + '\');"><b> X </b></span></div>' ;
	                	}else{
	                		obj.innerHTML = content + '<div id="' + divId + '" name="' + divName + '" style="margin:10px" ><span class="annotated ui-widget-content ui-corner-all hove" onclick="openOWLNode(\'' + owlid + '\')"><b>' + label + '</b></span>' + 
							'<span class="hove ui-icon ui-icon-close" style="float:left" onclick="removeElement(\'wadl\', \'' + name + '\', \'' + type + '\', \'' + attr  + '\' , \'' + iri + '\', \'' + divId + '\');"><b> X </b></span></div>' ;
	                	}
	                }
	            }
			}else{
				alert(errormsg);
			}
		}catch(err){
			 alert("connection fails");
		}
	}
	
	/* Function to get all recommend concept wsdl annotation
	 */
	function recommendAll(){
		var answer = confirm("recommend all terms ?");
		if (answer){
			
			// check the active tab
			var ulelement = document.getElementById("owltabs").firstChild;
			var lielements = ulelement.childNodes;
			for (var index = 0; index < lielements.length; index++) {
				var className = lielements[index].getAttribute("class");
				if (className != ''){
					document.getElementById("suggest-Ontology-Tab").value = index;
				}
			}
			ajaxHttpSend('recommendAll', 'recommendConceptFrom', recommendAllProcess);
		}
    }
	function recommendAllProcess(responseText){
		try{
			var response = eval("(" + responseText + ")");
			var errormsg = response.errormsg;
			//alert(errormsg);
			if (errormsg == '' ){
				
				var eleIDs =  response.eleIDs;
				var eleNames = response.eleNames;
				var eleTypes = response.eleTypes;
				var attributes = response.attributes;
				var iris = response.IRIs;
				var labels = response.labels;
				
				for (var i = 0; i < eleIDs.length; i++) { 
					
					var id = eleIDs[i];
					var name = eleNames[i];
					var type = eleTypes[i];
					var iri = iris[i];
					var attr = attributes[i];
					var label = labels[i];
					var objName;
					
					if (id != 0){
						objName = type + '-' + id + '-annotation';
					}else{
						objName = type + '-' + name + '-annotation';
					}
					
					// replace all illegal characters
					var owlid = ReplaceAll(iri , "#", "_");
	        		owlid = ReplaceAll(owlid , ":", "_");
	        		owlid = ReplaceAll(owlid , "/", "_");
	        		owlid = ReplaceAll(owlid , ".", "_");
					
	        		var arr = new Array(); 
		            arr = document.getElementsByName(objName);
		            for (var j = 0; j < arr.length; j++) { 
		                var obj = arr[j];
		                var divName;
		                if (id != 0 ){
		                	divName = type + "-" + id + "-" + attr;
		                }else{
		                	divName = type + "-" + name + "-" + attr;
		                }
		                if (iri != ''){
		                	var annotations = new Array(); 
		                	annotations = document.getElementsByName(divName);
		                	var total = annotations.length;
		                	var divId = divName + total;
		                	var content = obj.innerHTML;
		                	if (id != 0){
		                		obj.innerHTML = content + '<div id="' + divId + '" name="' + divName + '" style="margin:10px" ><span class="suggested hove ui-widget-content ui-corner-all" onclick="openOWLNode(\'' + owlid + '\')"><b>' + label + '</b></span>' + 
								'<span id="approve11" class="approve-button hove ui-icon ui-icon-circle-check" style="float:left" onclick="approve(' + id + ', \'' + name + '\',\'' + type + '\' ,\'' + iri + '\', \'' + divId + '\')"></span>' + 
								'<span id="reject11" class="reject-button hove ui-icon ui-icon-circle-close" onclick="reject(\'' + divId + '\')" style="float:left"></span></div>' ;
		                	}else{
		                		obj.innerHTML = content + '<div id="' + divId + '" name="' + divName + '" style="margin:10px" ><span class="annotated ui-widget-content ui-corner-all hove" onclick="openOWLNode(\'' + owlid + '\')"><b>' + label + '</b></span>' + 
								'<span class="hove ui-icon ui-icon-close" style="float:left" onclick="removeElement(\'wadl\', \'' + name + '\', \'' + type + '\', \'' + attr  + '\' , \'' + iri + '\', \'' + divId + '\');"><b> X </b></span></div>' ;
		                	}
		                }
		            }
				}
				
			}else{
				alert(errormsg);
			}
		}catch(err){
			 alert("err=" + err);
			 alert("connection fails");
		}
	}
	
	
	
	/* Function to approve the suggestion semantic concept
	 */
	function approve(id, name, type, iri, divId){
		var answer = confirm("approve this suggestion ?");
		if (answer){
			// clear buttons and suggestion tag
			var obj = document.getElementById(divId);
			var removeTagParent = obj.parentNode;
	        removeTagParent.removeChild(obj);
	        
			// execute the annotation to ws file
			document.getElementById("update-id").value = id;
			document.getElementById("update-name").value = name;
			document.getElementById("update-type").value = type;
			document.getElementById("update-action").value = "add";
			document.getElementById("update-attr").value = "modelReference";
			document.getElementById("update-value").value = iri;
			ajaxHttpSend('updateWS', 'updateWS', addModelRederenceProcess);
		}
	}
	
	/* Function to remove the suggestion
	 */
	function reject(divId){
		var answer = confirm("remove this suggestion ?");
		if (answer){
			// clear buttons and suggestion tag
			var obj = document.getElementById(divId);
			var removeTagParent = obj.parentNode;
	        removeTagParent.removeChild(obj);
		}
	}
	
	function removeUnapproved(){
		window.old_alert = window.alert;
		window.alert = function(){}
		$(".reject-button").each(function(){
			$ (this).parent().remove();
		});
		
	}	
