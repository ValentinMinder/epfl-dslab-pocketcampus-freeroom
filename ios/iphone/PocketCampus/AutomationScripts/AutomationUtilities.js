/* Global vars */

target = UIATarget.localTarget();

app = target.frontMostApp();

window = app.mainWindow();

log = UIALogger;

backButtonRect = null;

/*
 * Test
 */

function test(title, f, options) {
    if (options == null) {
        options = {
        logTree: true
        };
    }
    target = UIATarget.localTarget();
    application = target.frontMostApp();
    UIALogger.logStart(title);
    try {
        f(target, application);
        UIALogger.logPass(title);
    }
    catch (e) {
        UIALogger.logError(e);
        if (options.logTree) target.logElementTree();
        UIALogger.logFail(title);
    }
};

function assertEquals(expected, received, message) {
    if (received != expected) {
        if (! message) message = "Expected " + expected + " but received " + received;
        throw message;
    }
}

function assertTrue(expression, message) {
    if (! expression) {
        if (! message) message = "Assertion failed";
        throw message;
    }
}

function assertFalse(expression, message) {
    assertTrue(! expression, message);
}

function assertNotNull(thingie, message) {
    if (thingie == null || thingie.toString() == "[object UIAElementNil]") {
        if (message == null) message = "Expected not null object";
        throw message;
    }
}

/*
 * Utils
 */
 
function enterPluginAndTest(pluginName, dontTest) {
    if(!dontTest) {
        log.logStart("Enter "+pluginName);
    }
    window.elements()[pluginName].tap();
    delay(2.5);
    if(!dontTest) {
        if(isCurrentNavBarTitle(pluginName)) {
            log.logPass("Enter "+pluginName); 
        } else {
            log.logFail("Enter "+pluginName);
        }
    }
}
 
function tapBack() {
    if (backButtonRect == null) {
        backButtonRect = window.navigationBar().leftButton().rect();
    }
    target.tap(backButtonRect);
}
 
function isCurrentNavBarTitle(title) {
    return window.navigationBar().name() == title;
}

function chance(chance) {
    return (Math.random() <= chance);
}
 
function printElementsName(elements, recursive, level) {
    if (level == null) {
        level = 0;
    }
    for (var i = 0; i<elements.length; i++) {
        target.pushTimeout(0.1);
        if (elements[i] == UIAElementNil) {
            target.popTimeout();
            break;
        }
        target.popTimeout();
        log.logDebug(level+" : "+elements[i].name());
        if (recursive && elements[i].elements().toArray().length > 0) {
            printElementsName(elements[i].elements(), recursive, ++level);
        }
    }
}

function randomRowIndex(tableView){
    var cells = tableView.cells();
    var row = Math.round(Math.random()*(cells.length-1));
    return row;
}

function randomVisibleRowIndex(tableView){
    var cells = tableView.visibleCells();
    var row = Math.round(Math.random()*(cells.length-1));
    return row;
}
 
function randomString(string_length) {
    var chars = "abcdefghiklmnopqrstuvwxyz";
    var randomstring = '';
    for (var i=0; i<string_length; i++) {
        var rnum = Math.floor(Math.random() * chars.length);
        randomstring += chars.substring(rnum,rnum+1);
    }
    return randomstring;
}

//returns failing element index or true if check was ok
function checkElementsName(elements, regEx) {
    for (var i = 0; i<elements.length; i++) {
        if (elements[i].name().toLowerCase().match(regEx) == null) {
            return i;
        }
    }
    return true;
}

function tableViewContainsCellWithNameSubstring(tableView, name) {
    var cells = tableView.cells();
    for (var i = 0; i<cells.length; i++) {
        if (cells[i].name().toLowerCase().match(new RegExp(name, "i")) != null) {
            return true;
        }
    }
    return false;
}

function delay(seconds) {
    UIATarget.localTarget().delay(seconds);
}

function tapTab(name) {
    var window = UIATarget.localTarget().frontMostApp().mainWindow();
    window.tabBar().buttons()[name].tap();
}

// Poll till the item becomes visible, up to a specified timeout
function waitVisible(element, timeout) {
    return waitForVisible(element, timeout, 0.25);
}

function waitForVisible(element, timeout, step) {
    if (step == null) {
        step = 0.5;
    }
    var stop = timeout/step;
    for (var i = 0; i < stop; i++) {
        target.delay(step); // for the animation
        if (element.isVisible()) {
            return true;
        }
    }
    return false;
}

function waitForInvisible(element, timeout, step) {
    if (step == null) {
        step = 0.5;
    }
    var stop = timeout/step;
    for (var i = 0; i < stop; i++) {
        target.delay(step); // for the animation
        if (!element.isVisible()) {
            return true;
        }
    }
    return false;
}

function waitForLoaded(container, elementName) {
    return waitForLoadedWithParams(container, elementName, 5, 0.25);
}

function waitForLoadedWithParams(container, elementName, timeout, step) {
    if (step == null) {
        step = 0.5;
    }
    var stop = timeout/step;
    for (var i = 0; i < stop; i++) {
        delay(step); // for the animation
        log.logDebug("test : "+step);
        if (container.elements()[elementName] != UIAElementNil) {
            log.logDebug("OK");
            target.popTimeout(oldTimeout);
            return true;
        }
    }
    return false;
}

// Allows you to scroll to an element with a particular name and tap it.
function scrollToElementWithNameAndTap(scrollView, name) {
    var elementArray = scrollView.elements();
    if (scrollView instanceof UIATableView) {
        scrollToCellWithNameAndTap(scrollView, name);
        return;
    } else if (! (scrollView instanceof UIAScrollView)) {
		throw ("Expected a UIAScrollView");
	}
    
	var e = elementArray.scrollToElementWithName(name);
	waitForVisible(e, 5, 0.25);
	e.tap();
}

// Allows you to scroll to an cell with a particular name and tap it.
function scrollToCellWithNameAndTap(tableView, name) {
    var cellArray = tableView.cells();
	if (! (tableView instanceof UIATableView)) {
		throw ("Expected a UIAScrollView");
	}
    
    var e = cellArray.firstWithName(name);
	waitForVisible(e, 5, 0.25);
	e.tap();
}

UIAScrollView.prototype.scrollToElementWithNameAndTap = function(name){
    scrollToElementWithNameAndTap(this,name)
};

function dumpElements(elements) {
    for (var i in elements) {
        UIALogger.logDebug(elements[i].toString());
    }
}


function searchForText(root, text){
	if(!root.checkIsValid()){
		return false;
	}
	log.logDebug("Exploring: "+root.toString()+" with value: "+root.value()+ " and name: "+root.name());
	toCheck = root.value();
	if(toCheck != undefined && toCheck != null && toCheck.indexOf(text)!=-1){
 		return true;
 	}	
 	toCheck = root.name();
 	if(toCheck != undefined && toCheck != null && toCheck.indexOf(text)!=-1){
 		return true;
 	}
	for(var index = 0; index < root.elements().length; index++){
		elem = root.elements()[index];
		if(searchForText(elem, text)){
			return true;
		}
	}
	return false;
}