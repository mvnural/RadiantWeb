    // simulate the function click
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
