<#--<p>Choose the best recommendation for this element : (Owl${tabIndex} classes)</p>-->
<p>Choose the best recommendation for this element</p>
<div style="overflow:auto;">
    <form name="recommend concept" id="${elementType} :${elementName}">
        <fieldset>
            <#list suggestions as suggestionObject>
            <input id="${suggestionObject.conceptLabel}" name="option" value="${suggestionObject.conceptIRI}" type="radio" title="${suggestionObject.conceptDoc}" />
            <label for="${suggestionObject.conceptLabel}" title="${suggestionObject.conceptDoc}"> ${suggestionObject.conceptLabel} : <b> ${suggestionObject.score?string("0.#####")} </b></label>
            <br>
            </#list>
        </fieldset>
    </form>
</div>
