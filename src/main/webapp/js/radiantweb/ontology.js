/**
 *  This javascript is used to set the ontology tree view, serch the concept or set ontology information
 */

	/*
    * Function for setting the ontology options i.e. setting the operation and parametr iri/label for
    * using in recommending the terms. Makes ajax call to struts2 action SetOntologyOptions
    */
    function setOntologyOptions(){
    	document.getElementById("setOntology-Operation").value = document.getElementById("operationOption").value;
    	document.getElementById("setOntology-Param").value = document.getElementById("parameterOption").value;
        ajaxHttpSend('setOntologyOptions', 'SetOntologyOptions', setOntologyOptionsProcess);
    }
    function setOntologyOptionsProcess(responseText){
    	try{
    		var response = eval("(" + responseText + ")");
        	var errormsg = response.errormsg;
        	//alert(errormsg);
        	if (errormsg == '' ){
        		alert("Set ontology successfully");
        	}else{
        		alert(errormsg);
        	}
        }catch(err){
    		alert("Internet connection problem");	
    	}
    	$('#setOptionsDialog').dialog("close");
    }
    
	/*
	 * Function for searching the ontology Called when the search icon on the 
	 * ontology viewer is clicked. Makes ajax call to struts2 action searchOntologyTerm
	 */
	 function searchOntology(){
	 	document.getElementById('searchOntology-term').value = document.getElementById("ontsearchterm").value;
	   	ajaxHttpSend('searchOntologyTerm', 'searchOntologyTerm', searchOntologyProcess);
	 } 
	 function searchOntologyProcess(responseText){
		 try{
			 var response = eval("(" + responseText + ")");
			 var errormsg = response.errormsg;
			 //alert(errormsg);
			 if (errormsg == '' ){
				 var id;
				 document.getElementById("searchResults").innerHTML = response.innerHtml;
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
        	 }else{
        		 alert(errormsg);
        	 }
         }catch(err){
    		 alert("Internet connection problem");	
    	 }
     }
	
	 
	/* Function for set the tree view of ontology
	 */
	function setOwlHierarchy(responseText){
		 //alert(responseText);
		 try{
			 var response = eval("(" + responseText + ")");
			 var errormsg = response.errormsg;
			 //var owlImportURL = response.owlImportURL;
			 //alert(owlImportURL);
			 //alert(errormsg);
			 if (errormsg == '' ){
				 document.getElementById("ontologyinfo").innerHTML = response.ontologyinfo;
				 
				 // check the active tab
				 var ulelement = document.getElementById("owltabs").firstChild;
				 var lielements = ulelement.childNodes;
				 var rootName = "#root";
				 for (var index = 0; index < lielements.length; index++) {
					 var className = lielements[index].getAttribute("class");
					 if (className != ''){
						 var owlTab = "owl" + (index + 1);
						 if (index > 0) {
							 document.getElementById(owlTab).innerHTML = response.classes_hierarchy.replace('<div id="root">', '<div id="root' + index + '">');
							 rootName = rootName + index;
						 }else{
							 document.getElementById(owlTab).innerHTML = response.classes_hierarchy;
						 }
					 }
				 }
				 $(document).ready(function(){
					 $(rootName).treeview({
	       				 persist: "location",
	        			 animated: "slow",
	        			 zIndex:1000,
	        			 handle:'span',
	        			 collapsed: true,
	        			 unique: true
	       			 });
					 $('#root span').draggable({
        				 zIndex:1000,
        				 opacity:0.0,
        				 revert:true,
        				 handle:'span',
        				 iframeFix:'true',
        				 scroll:false,
        				 helper:'clone',
        				 start:function(){
        					 var label = $(this).text();
        					 $.draggingOWLNodeLabel = label;
        					 $.draggingOWLNodeId = $(this).parent().attr("id");
        					 $.draggingAnnotationValue = $(this).attr('value');
        					 parent.draggingID=$(this).attr('value');
        					 //alert('on darg start : '+this.id);
        				 }
        			 });
                 	 expandBranch("Thing");
                 	 $("#setoptions").click(function(){
                 		 $("#setOptionsDialog").dialog("open");
                 	 });               
				 });
				 $("#ontologyinfo").show();
				 $('#definition').show();
				 $('#owltabs').show();
				 
				 // for wsdl annotation
				 try{
					 document.getElementById("recommend-ontology").style.opacity = 0;
				 }catch(err){
				 }
				 
       	    }else if (errormsg == 'importerror'){
       			document.getElementById("importURLEncoded").value = st[1];
            	$("#owlImportURL").text(st[1]);
            	$("#importErrorDialog").dialog('open');
       	    }else{
       	    	alert(errormsg);
       	    }
		 }catch(err){
			 alert(err);
			 alert("No file selected or file is over 10MB");
		 }
   	 }
	//before owl file upload process
	jQuery(document).subscribe('before_owl', function(event,data) {
		//alert("ok");
		document.getElementById('pbar2').innerHTML = "<img src='images/processBar.gif' alt='loading... please wait' />";
	});
	//complete owl file upload process
	jQuery(document).subscribe('complete_owl', function(event,data) {
		//alert("receive");
		document.getElementById('pbar2').innerHTML = "";
	 	setOwlHierarchy(event.originalEvent.request.responseText);
	});
	
	
	
	 
	 /* Function for put the definition text to ontology information html tag
	  */
	 function addDefinition(definition,term){
	 	 //alert("def"+definition);
	 	 $("#def").replaceWith("<div id=\"def\" style=\"padding:4px;border-bottom:1px solid #c5c5c5\">Definition : "+term+"</div>");
	 	 $('#defvalue').replaceWith("<div id='defvalue' style='padding:4px; height: 35px; overflow:auto'>"+definition+"</div>");
	 }
	 
	 
	 
	 /* Function for get the autocomplete suggestion
	  */
	 function getList(word){
		if (baseWord != word.substring(0, 3)){
			document.getElementById('suggest-baseWord').value = baseWord;
			ajaxHttpSend('loadSuggest', 'suggest-form', setAutoSuggest);
			baseWord = word.substring(0, 3);
		}
	 }
	 function setAutoSuggest(responseText){
		 try{
			var response = eval("(" + responseText + ")");
	    	var errormsg = response.errormsg;
	    	//alert(errormsg);
	    	if (errormsg == '' ){
	    		suggestWords = response.suggestWords;
	    		//alert("suggest words = " + suggestWords);
	    		$( "#ontsearchterm" ).autocomplete({
	    			source: suggestWords
	    		});
	    	}else{
	    		alert(errormsg);
	    	}
	    }catch(err){
	    	//alert("Internet connection problem");	
	    }
	 }