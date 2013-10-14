#import "AutomationUtilities.js"

#import "TransportTest.js"

UIATarget.onAlert = function onAlert(alert) {
	var title = alert.name();
	
	if (title = '“EPFL” Would Like to Use Your Current Location') {
		alert.defaultButton().tap();
		return true;
	}
	// test if your script should handle the alert, and if so, return true
	
	// otherwise, return false to use the default handler
	return false;
}

testTransport();