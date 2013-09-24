/**
 * This script is used to do some action on tree view of ontology 
 */

   /*
    * Function for expanding the tree for a node for a given id
    * 
    **/
   	function expandBranch(id){
       	
    	var exp = $("#"+id).hasClass("expandable");
       	var lastExp = $("#"+id).hasClass("lastExpandable");
       	
       	$("#"+id).removeClass();
       	if(exp) $("#"+id).addClass("collapsable");
       	if(lastExp) $("#"+id).addClass("lastCollapsable");
       	
       	$("#"+id).children("div.hitarea").each(function(){
            $(this).removeClass();
            $(this).addClass("hitarea");
            if(exp) $(this).addClass("collapsable-hitarea");
            if(lastExp) $(this).addClass("lastCollapsable-hitarea");
		});
       	
       	$("#"+id).children("ul").each(function(){
            $(this).css("display", "block");
       	});
   	}
   
   	/*
    * Function for collapsing the tree for a node for a given id
    * 
    **/
	function collapseBranch(id){
       	
    	var exp = $("#"+id).hasClass("collapsable");
       	var lastExp = $("#"+id).hasClass("lastCollapsable");
       	$("#"+id).removeClass();
       	
       	if(exp) $("#"+id).addClass("expandable");
       	if(lastExp) $("#"+id).addClass("lastExpandable");
       	
       	$("#"+id).children("div.hitarea").each(function(){
            $(this).removeClass();
            $(this).addClass("hitarea");
            if(exp) $(this).addClass("expandable-hitarea");
            if(lastExp) $(this).addClass("lastExpandable-hitarea");
       	});
       	
       	$("#"+id).children("ul").each(function(){
            $(this).css("display", "none");
       	});
   	}
   	
   	
   	/*
    * Given the node id opens the node in the tree even if the tree is completly collapsed by expanding 
    * the necessary nodes and also selects it.
    * 
    **/
   	function openOWLNode(id){
    	
   		var exp;
       	collapseTree("Thing");
        var newId = '';
        
        $("[id="+id+"]").each(function(){
            
        	exp = $(this).parent().parent().hasClass("expandable");
           	var parentId = $(this).parent().parent().attr("id");
           	while(exp){
               	expandBranch(parentId);
               	exp = $("#"+parentId).parent().parent().hasClass("expandable");
               	parentId = $("#"+parentId).parent().parent().attr("id");
            }
            
           	$(this).children("span").each(function(){
                newId = this.id;
            });
           	
        });
        
        //alert("final id : "+newId)
        $("#loadowl").scrollTo("#"+newId,100,{offset:-50});
        var def = $("#"+id).attr("data");
        var label;
        $("#"+id+" span").each(function(){
            label = $(this).text();
        });
        //alert(label)
        selectOWLNode(def,label,id);
    }
   
   
   	/*
    * Given the id of the owlnode slects thet nodes and adds the defintion to 
    * the definition div.
    */
   	function selectOWLNode(definition,label,id){
        $(".selectedowl").each(function(){
            $(this).removeClass("selectedowl ui-state-focus");
        });
        if(id.toString().indexOf("spanidowlclass") != -1){
            $("#"+id).addClass("selectedowl ui-state-focus");
        }else{
        	$("[id="+id+"]").each(function(){
        		$(this).children("span").each(function(){
                	$(this).addClass("selectedowl ui-state-focus");
                  	//alert(this.id)
            	});
        	});
        }
        addDefinition(definition,label);
   	}
   
	/* 
   	* Given the tree root node id or its parents id collapses the entire tree
   	* Effective only for the used treeview plulgin  
   	*      
   	*/            
   	function collapseTree(treeroot)
   	{
       	$("#"+treeroot+" li.collapsable").each(function(){
           	var id = $(this).attr("id");
           	collapseBranch(id);
       	});
   	}
