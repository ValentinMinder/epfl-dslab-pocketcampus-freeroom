function testCamipro(){
	
	username = "andrica";
	password = "password";
	target.logElementTree();
	logIn(username, password, true);
	reload();
	logOut();
	return true;
}

function logOut(){
	tapBack();
	target.frontMostApp().mainWindow().buttons()[0].tap();
	target.frontMostApp().mainWindow().tableViews()["Empty list"].cells()["Gaspar account"].tap();
	target.frontMostApp().mainWindow().tableViews()["Empty list"].cells()["Logout"].tap();
	tapBack();
	target.frontMostApp().navigationBar().rightButton().tap();
}
function logIn(username, password, savePassword){
	pluginName="Camipro";
	log.logStart("Enter "+pluginName);
	window.elements()[pluginName].tap();
	delay(2);
	if(isCurrentNavBarTitle("Gaspar account")) {
		log.logPass("Enter "+pluginName); 
	} else {
		log.logFail("Enter "+pluginName);
		return false;
	}
	
	window.tableViews()[0].cells()[0].logElementTree();
	usernameField = window.tableViews()[0].cells()[0].textFields()[0];
	usernameField.setValue(username);
	
	passwordField = window.tableViews()[0].cells()[1].secureTextFields()[0];
	passwordField.setValue(password);
	
	if(!savePassword){
		saveSwitch=window.tableViews()[0].cells()[3].switches()[0];
		saveSwitch.setValue(false);
	}
	login = window.tableViews()[0].cells()[2].staticTexts()[0];
	login.tap();
	log.logDebug("Logged in");
	delay(10);
	
	log.logDebug("Getting to Camipro");
	if(isCurrentNavBarTitle(pluginName)) {
		log.logPass("Enter "+pluginName); 
	} else {
		log.logFail("Enter "+pluginName);
		return false;
	}
	delay(10);
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
