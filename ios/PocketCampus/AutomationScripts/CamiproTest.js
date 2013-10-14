#import "Credentials.js"
#import "AuthenticationUtils.js"

// in Credentials.js, put 2 fields
// username = "username";
// password = "super secret passwords";
// never add Credentials.js to the SVM repo
	
function testCamipro(){
	return (
		testCamiproReloadCardSavePasswd() &&
		testCamiproReloadCardDONTSavePasswd() &&
		
		testCamiproReloadBalanceSavePasswd() &&
		testCamiproReloadBalanceDONTSavePasswd() &&
		
		testCamiproLogInLogOutSavePasswd() &&
		testCamiproLogInLogOutDONTSavePasswd() &&  
		
		testStatisticsSavePasswd() &&
		testStatisticsDONTSavePasswd() && 
		
		testScrollUpAndDownSavePasswd() && 
		testScrollUpAndDownDONTSavePasswd() && 
		
		testCancelSavePasswd() &&
		testCancelDONTSavePasswd() &&
		
		testScrollDownActionStillAvailableSavePasswd() &&
		testScrollDownActionStillAvailableDONTSavePasswd());
}

function printFunctionName(){
	var myName = arguments.callee.caller.caller.toString();
   	myName = myName.substr('function '.length);
   	myName = myName.substr(0, myName.indexOf('('));
	log.logDebug("====start "+myName+"====");
}

///////////////////////////
function testCamiproLogInLogOut(savePasswd){
	printFunctionName();
	if(!logIn(username, password, "Camipro", savePasswd)){
		log.logDebug("Failed to log in");
		return false;
	}
	return leavePlugin(savePasswd);
}

function testCamiproLogInLogOutSavePasswd(){
	return testCamiproLogInLogOut(true);
}

function testCamiproLogInLogOutDONTSavePasswd(){
	return testCamiproLogInLogOut(false);
}


////////////////////////////
function testCamiproReloadBalance(savePasswd){
	printFunctionName();
	if(!logIn(username, password, "Camipro", savePasswd)){
		log.logDebug("Failed to log in");
		return false;
	}
	reload();
	return leavePlugin(savePasswd);
}

function testCamiproReloadBalanceSavePasswd(){
	return testCamiproReloadBalance(true);
}

function testCamiproReloadBalanceDONTSavePasswd(){
	return testCamiproReloadBalance(false);
}

function reload(){
	balanceBefore=window.tableViews()["Empty list"].cells()[0].staticTexts()[0].value();
	log.logDebug("Before: "+balanceBefore);
	target.frontMostApp().navigationBar().rightButton().tap();
	delay(2);
	balanceAfter=window.tableViews()["Empty list"].cells()[0].staticTexts()[0].value();
	log.logDebug("After: "+balanceAfter);
	assertTrue(balanceBefore==balanceAfter);
}



/////////////////////////////////
function testCamiproReloadCard(savePasswd){
	printFunctionName();
	if(!logIn(username, password, "Camipro", savePasswd)){
		log.logDebug("Failed to log in");
		return false;
	}
	target.frontMostApp().toolbar().buttons()["Action"].tap();

	element = target.frontMostApp().actionSheet().buttons()["Reload the card"];
	assertTrue(element.isValid() && element.isVisible() && element.isEnabled());
	//the alert still doesnt work
	element.tap();
	//target.frontMostApp().actionSheet().cancelButton().tap();		
	delay(2);
	return leavePlugin(savePasswd);
}	

function testCamiproReloadCardSavePasswd(){
	return testCamiproReloadCard(true);
}

function testCamiproReloadCardDONTSavePasswd(){
	return testCamiproReloadCard(false);
}


//////////////////////////////////
function testStatistics(savePasswd){
	printFunctionName();
	if(!logIn(username, password, "Camipro", savePasswd)){
		log.logDebug("Failed to log in");
		return false;
	}
	target.frontMostApp().toolbar().buttons()["Action"].tap();
	element = target.frontMostApp().actionSheet().buttons()["Statistics"];
	assertTrue(element.isValid() && element.isVisible() && element.isEnabled());
	//the alert still doesnt work
	element.tap();
	delay(2);
	return leavePlugin(savePasswd);
}

function testStatisticsSavePasswd(){
	return testStatistics(true);
}

function testStatisticsDONTSavePasswd(){
	return testStatistics(false);
}


////////////////////////////////
function testScrollUpAndDown(savePasswd){
	printFunctionName();
	if(!logIn(username, password, "Camipro", savePasswd)){
		log.logDebug("Failed to log in");
		return false;
	}
	table = window.tableViews()["Empty list"];
	for(var i=0; i < 5; i++){
		table.scrollDown();
		delay(0.5);
	}
	for(var i=0; i < 5; i++){
		table.scrollUp();
		delay(0.5);
	}
	return leavePlugin(savePasswd);
}

function testScrollUpAndDownSavePasswd(){
	return testScrollUpAndDown(true);
}

function testScrollUpAndDownDONTSavePasswd(){
	return testScrollUpAndDown(false);
}


//////////////////////////////
function testCancel(savePasswd){
	printFunctionName();
	if(!logIn(username, password, "Camipro", savePasswd)){
		log.logDebug("Failed to log in");
		return false;
	}
	target.frontMostApp().toolbar().buttons()["Action"].tap();
	delay(1);
	target.frontMostApp().actionSheet().cancelButton().tap();
	return leavePlugin(savePasswd);
}

function testCancelSavePasswd(){
	testCancel(true);
}

function testCancelDONTSavePasswd(){
	testCancel(false);
}


/////////////////////////////
function testScrollDownActionStillAvailable(savePasswd){
	printFunctionName();
	if(!logIn(username, password, "Camipro", savePasswd)){
		log.logDebug("Failed to log in");
		return false;
	}
	
	table = window.tableViews()["Empty list"];
	for(var i=0; i < 5; i++){
		table.scrollDown();
		delay(0.5);
	}
	target.frontMostApp().toolbar().buttons()["Action"].tap();
	target.frontMostApp().actionSheet().cancelButton().tap();
	return leavePlugin(savePasswd);
}

function testScrollDownActionStillAvailableSavePasswd(){
	return testScrollDownActionStillAvailable(true);
}

function testScrollDownActionStillAvailableDONTSavePasswd(){
	return testScrollDownActionStillAvailable(false);
}	