<span style="padding:2px;vertical-align: middle;"> Search: </span>
<div style="padding-bottom:4px;display: inline;vertical-align: middle;margin-top: auto;margin: auto;">
    <input type="text" id="ontsearchterm" name="ontsearchterm" class="ui-corner-all ui-autocomplete-input" style="vertical-align: middle;"
           onkeydown="getList(this.value);" autocomplete="off" role="textbox" aria-autocomplete="list" aria-haspopup="true" />
    <img id="searchontology" src="styles/images/search.gif" alt="" style="padding-left: 5px; height: 20px;
        vertical-align: middle;" onclick="searchOntology();" />
</div>
<span id="setoptions" class="ui-button ui-corner-all" style="color:white;background:#616D7E;padding: 6px;margin-left:3px;margin-right:3px;float: right;margin: auto;">
    Set Ontology Options
</span>

<#-- This is used when the page is NOT wsdl. Need to check how Discovery makes use of this. Might be deleted in the future.
<span style="width:40%;float:left;padding:2px">" + ontId + "</span>
<div style="padding-bottom:4px;float:left">
    <input type="text" id="ontsearchterm" name="ontsearchterm" class="ui-corner-all" style="float:left;" onkeydown="getList(this.value);" />
    <img id="searchontology" src="styles/images/search.gif" alt="" style="padding-right: 5px; height: 20px; padding-bottom: 2px;" onclick="searchOntology();" />
</div>
<span id="setoptions" class="ui-button ui-corner-all" style="color:white;background:#616D7E;padding:3px;margin-left:3px;margin-right:3px;" >Set Options</span>
</div>
-->