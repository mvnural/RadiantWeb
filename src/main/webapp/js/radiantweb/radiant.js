/**
 * This script is used to store some common method of javascript 
 * which is frequently used by radiant web
 * 
 */


/**
 * The ajax call function. it is convenient to call struts2 action
 *  action : struts2 action
 *  form : input form id
 *  response : the method which is used after ajax response 
 */
function ajaxHttpSend(action, form, response)  
{  
	// url
    var url = action + ".action";
	//alert("url ok");
	
	// param serialize
	var params = $('#' + form).serialize();
	//alert("params ok");
	
	var xmlhttp;
    if (window.XMLHttpRequest)
    {// code for IE7+, Firefox, Chrome, Opera, Safari
		   xmlhttp=new XMLHttpRequest();
    }else{// code for IE 6, IE5
        xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }
    
    //alert(url + "?" + params);
    
    xmlhttp.open("POST", url + "?" + params, true);
    xmlhttp.send(); 
    xmlhttp.onreadystatechange=function()
    {
    	if (xmlhttp.readyState==4 && xmlhttp.status==200)
        {
    		response(xmlhttp.responseText);
    	}
    };
}

/**
 * get the checked radio box value 
 */
function getCheckedValue(radioObj) {
	if(!radioObj){
		return "";
	}
	var radioLength = radioObj.length;
	if(radioLength == undefined){
		if(radioObj.checked){
			return radioObj.value;
		}else{
			return "";
		}
	}
	for(var i = 0; i < radioLength; i++) {
		if(radioObj[i].checked) {
			return radioObj[i].value;
		}
	}
	return "";
}

/**
 * get the checked radio box id 
 */
function getCheckedId(radioObj) {
	if(!radioObj){
		return "";
	}
	var radioLength = radioObj.length;
	if(radioLength == undefined){
		if(radioObj.checked){
			return radioObj.id;
		}else{
			return "";
		}
	}
	for(var i = 0; i < radioLength; i++) {
		if(radioObj[i].checked) {
			return radioObj[i].id;
		}
	}
	return "";
}


/**
 * find parent node 
 */
function findParentNode(type, childObj) {
    var testObj = childObj.parentNode;
    while(true) {
        if (testObj.getAttribute('id') != null){
        	if (testObj.getAttribute('id').indexOf(type) != -1){
        		if (testObj.getAttribute('id').indexOf('drop') == -1) return testObj;
        	}
        }
        testObj = testObj.parentNode;
    }
}

/**
 *  a convenient replace all javascript method 
 */
function ReplaceAll(strOrg, strFind, strReplace){
	var index = 0;
	while(strOrg.indexOf(strFind,index) != -1){
		strOrg = strOrg.replace(strFind,strReplace);
		index = strOrg.indexOf(strFind,index);
	}
	return strOrg;	
}

String.prototype.trim = function () {
    return this.replace(/^\s*/, "").replace(/\s*$/, "");
};

/**
 *  a method which is used to simulate Click, because ios safari doesn't support click method
 */
function simulateClick(element, eventName)
{
	
	var browser = getbrowser();
	
	if (browser != 'safari'){
		element.click();
		return;
	}
	
    var options = extend(defaultOptions, arguments[2] || {});
    var oEvent, eventType = null;

    for (var name in eventMatchers)
    {
        if (eventMatchers[name].test(eventName)) { eventType = name; break; }
    }

    if (!eventType)
        throw new SyntaxError('Only HTMLEvents and MouseEvents interfaces are supported');

    if (document.createEvent)
    {
        oEvent = document.createEvent(eventType);
        if (eventType == 'HTMLEvents')
        {
            oEvent.initEvent(eventName, options.bubbles, options.cancelable);
        }
        else
        {
            oEvent.initMouseEvent(eventName, options.bubbles, options.cancelable, document.defaultView,
      options.button, options.pointerX, options.pointerY, options.pointerX, options.pointerY,
      options.ctrlKey, options.altKey, options.shiftKey, options.metaKey, options.button, element);
        }
        element.dispatchEvent(oEvent);
    }
    else
    {
        options.clientX = options.pointerX;
        options.clientY = options.pointerY;
        var evt = document.createEventObject();
        oEvent = extend(evt, options);
        element.fireEvent('on' + eventName, oEvent);
    }
    return element;
}
function extend(destination, source) {
    for (var property in source)
      destination[property] = source[property];
    return destination;
}
var eventMatchers = {
    'HTMLEvents': /^(?:load|unload|abort|error|select|change|submit|reset|focus|blur|resize|scroll)$/,
    'MouseEvents': /^(?:click|dblclick|mouse(?:down|up|over|move|out))$/
};
var defaultOptions = {
    pointerX: 0,
    pointerY: 0,
    button: 0,
    ctrlKey: false,
    altKey: false,
    shiftKey: false,
    metaKey: false,
    bubbles: true,
    cancelable: true
};

/**
 *  get the browser name
 */
function getbrowser() {
    var agt = navigator.userAgent.toLowerCase();
    if (agt.indexOf('msie') != -1) return "msie";
    if (agt.indexOf('firefox') != -1) return "firefox";
    if (agt.indexOf('safari') != -1) return "safari";
    if (agt.indexOf('seamonkey') != -1) return "seamonkey";
    if (agt.indexOf('netscape') != -1) return "netscape";
    if (agt.indexOf('opera') != -1) return "opera";
    return "unknown";
}

/**
 *  get the OS name
 */
function getOS(){
	// This script sets OSName variable as follows:
	// "Windows"    for all versions of Windows
	// "MacOS"      for all versions of Macintosh OS
	// "Linux"      for all versions of Linux
	// "UNIX"       for all other UNIX flavors 
	// "Unknown OS" indicates failure to detect the OS

	var OSName="Unknown OS";
	if (navigator.appVersion.indexOf("Win")!=-1) OSName="Windows";
	if (navigator.appVersion.indexOf("Mac")!=-1) OSName="MacOS";
	if (navigator.appVersion.indexOf("X11")!=-1) OSName="UNIX";
	if (navigator.appVersion.indexOf("Linux")!=-1) OSName="Linux";

	return OSName;
}

/**
 * get the select file name
 * @param fileId the input file tag id
 * @param textId the input text tad id (display file name)
 */
function selectfile(fileId, textId){
    var val = $('#' + fileId).val();
    val = val.replace("C:\\fakepath\\", "");
    $('#' + textId).val(val);
    $('#discovery-owlloc').val(val);
    //alert("selc " + val);   
}

/**
 * hit file button
 */
function hitFileBtn(fileBtnId){
	var fileEle = document.getElementById(fileBtnId);
	simulateClick(fileEle, "click");
}

