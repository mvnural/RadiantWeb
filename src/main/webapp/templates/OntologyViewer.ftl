<#-- I'll stop considering "Thing" as a special case. I'll leave this just in case.
<div id="root">
    <ul>
        <li id="Thing">
            <span>Thing</span>
        <ul>
            <@recursive_print node=companyhome />
        </ul>
        </li>
    </ul>
</div>-->

<div id="root">
    <ul>
        <@recursive_print node=rootNode />
    </ul>
</div>

<#macro recursive_print node >
   <li id="${node.IRIFragment}" data="${node.definition}">
       <span id="${node.spanId}" title="${node.IRI}" value="${node.IRI}" onclick="selectOWLNode('${node.definition}','${node.value}','${node.spanId}')">
        ${node.value}
       </span>
        <#if node.children?has_content>
        <ul>
            <#list node.children as child>
                <@recursive_print node=child />
            </#list>
        </ul>
        </#if>
   </li>
</#macro>



