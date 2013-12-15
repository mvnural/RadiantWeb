<div class="ui-widget-content ui-corner-all ui-state-default" style="width:95%; margin:6px;padding:6px">
    <span style="width:40%;float:left;padding:2px">${fileName}</span>
    <span id="save" style="color:white;background:#616D7E;padding:3px;margin-right:3px" class="ui-button ui-corner-all"
          onclick="save();">Save WSDL</span>
    <span id="recommend" class="ui-button ui-corner-all"
          style="color:white;background:#616D7E;padding:3px;margin-right:3px;" onclick="recommendAll()">Recommend All Terms</span>
    <span id="switchWSView" style="color:white;background:#616D7E;padding:3px;" class="ui-button ui-corner-all">WSDLXML view</span>
    <span id="removeUnapprovedRecommendations" style="color:white;background:#616D7E;padding:3px;"
          class="ui-button ui-corner-all" onclick="removeUnapproved()">Remove Unapproved Recommendations</span>
</div>.

<div id="wsxmlview" style="widh:100%; overflow:auto; "></div>
<div id="wstree" class="ui-widget-content ui-corner-all" style="overflow:auto;margin:6px; height:475px; padding:10px">
    <div class="ui-widget-content ui-corner-all" style="padding:4px">${documentDefinition}</div>
    <#list portTypes as portType>
        <#assign portTypeName= portType.getQName().getLocalPart() />
        <ul>
            <li id="portType${portTypeName}">
                <div class="drop" value="portType:${portTypeName}" id="${portTypeName}">Identified Operations in the WSDL (portType : ${portTypeName} )</div>
            <div id="${portTypeName}annotation" class="annotation" value="portType:${portTypeName}"></div>
                <@printWSDL />
            </li>
        </ul>
    </#list>
</div>

<#macro printWSDL >
<ul>
    <#list operations as operation>
    <li id="li${operation.getName()}op">
        <div id="operation-${operation.getName()}-${operation.id}-droppable" name="ws-droppable" class="operation drop ui-widget-content ui-corner-all" value="operation:${operation.getName()}"
        title="${operation.getDoc()?replace('\'', '"')}">
            <span style="width:35%; float:left">${operation.getName()}</span>
            <span class="ui-button ui-corner-all" onclick="recommend(${operation.id},'${operation.getName()}','operation','${operation.getDoc()?replace('\'', '"')}')"
            style="background:#616D7E; color:white; text-align: center; width: 120px; margin: 2px; padding:1px">Recommend Terms</span>
        </div>
        <#assign attrValue = "operation:" + operation.getName() />
        <#if operation.functionality??>
            <#assign functionalityValues = operation.functionality.toString()?split(" ") />
            <#if functionalityValues?has_content>
                <#list functionalityValues as functionality>
                    <div id="preExist-${operation.getId()}modelReference${functionality_index}" value="${attrValue}">
                        <div style="margin:10px">
                            <span id=" ${operation.getId()}" class="preExisting ui-widget-content ui-corner-all"
                            <#assign functionalityName = functionality?substring(functionality?last_index_of("/") + 1) />
                            <#assign owlId = functionality?replace('.', '_')?replace('/', '_')?replace(':', '_')?replace('#', '_') />
                            value="${functionality}" title="${functionality}" onclick="openOWLNode('${owlId}')"> ${functionalityName}
                            </span>
                            <span id="remove${operation.getId()}" onclick="removeElement('wsdl', '${operation.getId()}', 'operation', 'modelReference', '${functionality?replace('\'', '"')}', 'preExist-${operation.getId()}-modelReference${functionality_index}')" class="ui-icon ui-icon-close hove" style="float:left">
                            </span>
                        </div>
                    </div>
                </#list>
            </#if>
        </#if>
        <div name="operation-${operation.getId()}-annotation" class="annotation" value="${attrValue}"></div>

        <ul>
            <#--Print the input message-->
            <#assign inputMessage = operation.getInput() />
            <li id="li${inputMessage.getName()}msg">
                <div id="${inputMessage.getName()}msg" class="message drop ui-widget-content ui-corner-all" value=" ${attrValue}" >
                    <span style='width:50%;'>Inputs (${inputMessage.getName()})</span>
                </div>
                <@printAnnotation annotationObject=operation.getInput() attrValue=attrValue/> <#--printMessageAnnotation(buf, operation.getInput(), attrval);-->
                <div name="message-${inputMessage.getId()}-annotation" class="annotation" value="${attrValue}"></div>
                <ul>
                    <@printMessage message=inputMessage /> <#--printMessage(operation.getInput(), buf);-->
                </ul>
           </li>
           <#--Print the output message-->
           <#assign outputMessage = operation.getOutput() />
           <li id="li${outputMessage.getName()}msg">
                <div id="${outputMessage.getName()}msg" class="drop ui-widget-content ui-corner-all message" value="${attrValue}">
                    <span style='width:50%;'>Outputs (${outputMessage.getName()})</span>
                </div>
                <@printAnnotation annotationObject=operation.getOutput() attrValue=attrValue/> <#--printMessageAnnotation(buf, operation.getOutput(), attrval);-->
                <div name="message-${outputMessage.getId()}-annotation" class="annotation" value="${attrValue}"></div>
                <ul>
                    <@printMessage message=outputMessage /> <#--printMessage(operation.getOutput(), buf);-->
                </ul>
           </li>
        </ul>

    </li>
    </#list>
</ul>
</#macro>

<#macro printMessage message>
<#--Print simple types-->
<ul>
    <#list message.getSimpleType() as simple>
        <#assign attrValue = "element:" + simple.getName() + " " + message.getName() />
        <@printSimple simple=simple parent=message attrValue=attrValue/>
    </#list>
</ul>

<#--Print complex types-->
<ul>
    <#list message.getComplexType() as complex>
        <@printComplex complex=complex /><#--printComplex(complex, buf);-->
    </#list>
</ul>

</#macro>


<#macro printAnnotation annotationObject attrValue>
    <#if annotationObject.getSemanticConcept()??>
        <#assign modelReferenceValues = annotationObject.getSemanticConcept()?split(" ") />
        <#if modelReferenceValues?has_content>
            <#list modelReferenceValues as value>
                <#assign name = value.substring(value?last_index_of("/") + 1) />
                <div id="preExist-${annotationObject.getId()}-modelReference${value_index}" value="${attrValue}">
                    <div style="margin:10px">
                        <#assign owlId = value?replace('.', '_')?replace('/', '_')?replace(':', '_')?replace('#', '_') />
                        <span id="${annotationObject.getId()}" class="preExisting hove ui-widget-content ui-corner-all" value="${value}" title="${value}" onclick="openOWLNode('${owlId}')" >${name}</span>
                        <span id="remove${annotationObject.getId()}" class="ui-icon ui-icon-close hove" onclick="removeElement('wsdl', '${annotationObject.getId()}', 'annotationObject', 'modelReference', '${value?replace('\'', '"')}', 'preExist-${annotationObject.getId()}-modelReference${value_index}')" style="float:left"></span>
                    </div>
                </div>
            </#list>
        </#if>
    </#if>
</#macro>

<#macro printSimple simple parent attrValue>
<#assign elementDoc = simple.getDescription()?trim?replace('\'', '"') />
<li id="li${parent.getName()}${simple.getName()}">
    <div id="simple-${simple.getName()}-${simple.getId()}-droppable" name="ws-droppable" class="element drop ui-widget-content ui-corner-all"
         value="element:${simple.getName()} ${parent.getName()}" title="${elementDoc}">
        <span style="width:35%;float:left">${simple.getName()}<#if simple.isRequired()>(*)</#if></span>
        <span onClick="recommend(${simple.getId()} , '${simple.getName()}', 'simple' ,'${elementDoc}')" class="ui-button ui-state-default ui-corner-all" style="background:#616D7E;color: white; text-align:center;;width:120px;margin:2px">Recommend Terms</span>
    </div>
    <@printAnnotation annotationObject=simple attrValue=attrValue /> <#--printSimpleAnnotation(buf, simple, attrval);-->
    <div name="simple-${simple.getId()}-annotation" class="annotation" value="${attrValue}">
    </div>
</li>
</#macro>

<#macro printComplex complex>
    <#assign attrValue = "complex:" + complex.getName() />
    <#assign elementDoc = complex.getDescription()?trim?replace('\'', '"') />

<li id="li${complex.getName()}" >
    <div id="complex-${complex.getName()}-${complex.getId()}-droppable" name="ws-droppable" class="complex drop ui-widget-content ui-corner-all"
    value="complex:${complex.getName()}" title="${elementDoc}">
        <span style="width:35%;float:left">${complex.getName()}</span>
        <span onClick="recommend( ${complex.getId()}, '${complex.getName()}', 'complex', '${elementDoc}')" class="ui-button ui-state-default ui-corner-all" style="background:#616D7E;color: white; text-align:center;;width:120px;margin:2px">Recommend Terms</span>
    </div>
    <@printAnnotation annotationObject=complex attrValue=attrValue  /> <#--printComplexAnnotation(buf, complex, attrval);-->
    <div name="complex-${complex.getId()}-annotation" class="annotation" value="${attrValue}"></div>

    <#--Print simple types first-->
    <ul>
        <#list complex.getSimples() as simple>
            <@printSimple simple=simple parent=complex attrValue=attrValue />
        </#list>
    </ul>

    <#--Now, print child complex types -->
    <ul>
        <#list complex.getComplexTypes() as child><#--(ComplexTypeOBJ complex : complex.getComplexTypes()){-->
        <@printComplex complex=child /> <#--printComplex(complex, buf);-->
        </#list>
    </ul>
</li>
</#macro>