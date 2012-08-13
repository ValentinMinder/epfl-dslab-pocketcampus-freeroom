function logIn(username, password, pluginName, savePassword){
	window.elements()[pluginName].tap();
	delay(2);
	if(isCurrentNavBarTitle("Gaspar account")) {
		log.logPass("Enter "+pluginName); 
	} else {
		log.logFail("Enter "+pluginName);
		return false;
	}

	usernameField = window.tableViews()[0].cells()[0].textFields()[0];
	usernameField.setValue(username);
	
	passwordField = window.tableViews()[0].cells()[1].secureTextFields()[0];
	passwordField.setValue(password);
	
	saveSwitch=window.tableViews()[0].cells()[3].switches()[0];
	saveSwitch.setValue(savePassword);
	login = window.tableViews()[0].cells()[2].staticTexts()[0];
	login.tap();
	delay(5);
	log.logDebug("Getting to Camipro");
	return isCurrentNavBarTitle(pluginName);
		log.logPass("Enter "+pluginName); 
}

function logOut(){
	while(!isCurrentNavBarTitle("PocketCampus EPFL")){
		tapBack();
	}
	target.frontMostApp().mainWindow().buttons()[0].tap();
	target.frontMostApp().mainWindow().tableViews()["Empty list"].cells()["Gaspar account"].tap();
	target.frontMostApp().mainWindow().tableViews()["Empty list"].cells()["Logout"].tap();
	tapBack();
	target.frontMostApp().navigationBar().rightButton().tap();
	return true;
}