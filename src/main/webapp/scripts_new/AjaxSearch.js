

function ajaxSearch()
{
      //$('<div id="overlay" />').appendTo("body").fadeIn('normal');
      var key=$('input[name=key]').val();
      System.out.println("It is in ajaxSearch");
        jQuery.ajax({
       type:'POST',
       url:'displaySimilarClasses.jsp',
       data:'key='+key,
       success:function(result){
           //$(result).appendTo('#results div');
           $("#results").html(result);
           //$("#results").RELOAD!!();
           ////.html(result);
           //parent.draggingID=null;
           //window.location.reload();
       }
    });
        //$('#overlay').fadeOut('400',function()
        //{
        //    $(this).remove();
        //});
}
