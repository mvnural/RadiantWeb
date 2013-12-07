<ol id='searchResultSelectable'>
<#list searchResults as result>
    <li data='${result.fragmentData}' class="ui-widget-content ontologySearchResults" style="margin:6px;padding:2px;">
        <span value='${result.fragmentData}' style='width:50%;float:left'>
        <b>${result.label} : ${result.score}</b>
        </span>
        <br/><span><#if result.definition?? >${result.definition} </#if></span>
    </li>
</#list>
</ol>