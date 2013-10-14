#import "AuthenticationUtils.js"
#import "Credentials.js"
#import "AutomationUtilities.js"

// in Credentials.js, put 2 fields
// username = "username";
// password = "super secret passwords";
// never add Credentials.js to the SVM repo
function printFunctionName(){
	var myName = arguments.callee.caller.caller.toString();
   	myName = myName.substr('function '.length);
   	myName = myName.substr(0, myName.indexOf('('));
	logStart(myName);
}

function logStart(myName){
	log.logStart("====start "+myName+"====");
}

nbOfTimes = 5;
//////////////
function testAuthentication(){
	return (
	testCancelLoggingInSavePasswd() && 
	testAuthenticationFailed() &&	
	testAuthenticateMissingPassword() && 
	testAuthenticateMissingUsername() &&
	testAuthenticationSuccessfulMultipleTimes(true) &&
	testAuthenticationSuccessfulMultipleTimes(false) &&
	testAuthenticationSuccessfulMultipleTimesMixed());
}

/////////////
function testAuthenticationSuccessfulMultipleTimes(savePasswd){
	for(var i=0; i<nbOfTimes; i++){
		log.logDebug("Run #"+i);
		assertTrue(testAuthenticationSuccessful(savePasswd), "Try #"+i);
	}
	return true;
}

/////////////
function testAuthenticationSuccessfulMultipleTimesMixed(){
	for(var i=0; i<nbOfTimes; i++){
		log.logDebug("Run #"+i);
		assertTrue(testAuthenticationSuccessful(true), "Try #"+i);
		assertTrue(testAuthenticationSuccessful(false), "Try #"+i);
		assertTrue(testAuthenticationSuccessful(false), "Try #"+i);
	}
	return true;
}

/////////////
function testAuthenticationSuccessful(savePasswd){
	printFunctionName();
	assertTrue(logIn(username, password, "Camipro", savePasswd), "Cannot log in");
	return leavePlugin(savePasswd);
}

//////////////
function testAuthenticateMissingUsername(){
	logStart("testAuthenticateMissingUsername");
	fillInCredentials("", "pass", "Camipro", false);
	login = window.tableViews()[0].cells()[2].staticTexts()[0];
	login.tap();
	if(isCurrentNavBarTitle("Gaspar account")){
		log.logPass("Login button is not enabled"); 
	}else{
		log.logFail("Login button IS enabled");
		return false;
	}
	window.navigationBar().rightButton().tap();
	delay(2);
	return true;
}

//////////////
function testAuthenticateMissingPassword(){
	logStart("testAuthenticateMissingPassword");
	fillInCredentials("username", "", "Camipro", false);
	login = window.tableViews()[0].cells()[2].staticTexts()[0];
	login.tap();
	if(isCurrentNavBarTitle("Gaspar account")){
		log.logPass("Login button is not enabled"); 
	}else{
		log.logFail("Login button IS enabled");
		return false;
	}
	window.navigationBar().rightButton().tap();
	delay(2);
	return true;
}

///////////////
function testAuthenticationFailed(){
	logStart("testAuthenticationFailed");
	fillInCredentials("username", "password", "Camipro", false);
	login = window.tableViews()[0].cells()[2].staticTexts()[0];
	login.tap();
	if(isCurrentNavBarTitle("Gaspar account")){
		log.logDebug("Login button is not enabled"); 
	}else{
		log.logFail("Login button IS enabled");
		return false;
	}
	if(searchForText(window, "Wrong username or password")){
		log.logDebug("Wrong username or password"); 
	}else{
		log.logFail("Wrong username or password");
		return false;
	}
	window.navigationBar().rightButton().tap();
	delay(2);
	return true;
}

///////////////////
function testCancelLoggingIn(savePasswd){
	fillInCredentials(username, password, "Camipro", savePasswd);
	target.frontMostApp().mainWindow().tableViews()["Empty list"].cells()["Login"].tap();
	target.frontMostApp().navigationBar().rightButton().tap();
	delay(1.5);
	if(!isCurrentNavBarTitle("PocketCampus EPFL")){
		log.logFail("Not back to Dashboard");
		return false;
	}
	window.elements()["Camipro"].tap();
	delay(2);
	if(!isCurrentNavBarTitle("Camipro")){
		log.logPass("Could NOT enter Camipro");
	}else{
		log.logFail("Could enter Camipro");
		return false;
	}
	window.navigationBar().rightButton().tap();
	return true;
}

///////////////////
function testCancelLoggingInSavePasswd(){
	logStart("testCancelLoggingInSavePasswd");
	return testCancelLoggingIn(true);
}

///////////////////
function testCancelLoggingInDONTSavePasswd(){
	return testCancelLoggingIn(false);
}