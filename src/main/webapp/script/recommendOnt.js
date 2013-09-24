/* 
 * The JS file is for caalling the recommednOntology JSP inorder to recommend ontologies based on the 
 * the WSDL corpus
 */

function recommendOntologies(id){
    $("#progressbardialogue").dialog("open");
    $.ajax({
            type: "POST",
            url: "recommendOntology.jsp",
            //async:false,
            data: "&id="+id,
            success: function(data,status,xmlhttp) {
                
                var res = xmlhttp.responseText.toString().trim();
                if(res.toString().indexOf("Error:")>-1){
                    $("#progressbardialogue").dialog("close");
                    alert(res.trim());
                }
                else{
                    //$("#owlRecoFieldset").replaceWith(res.trim());
                    $("#ontreccontent").replaceWith("<div id=\"ontreccontent\">"+res.trim()+"</div>");
                    $("#progressbardialogue").dialog("close");
                    $("#ontrecdialogue").dialog("open");
                }
            }
    });        
}

