#import "AutomationUtilities.js"

#import "CamiproTest.js"


UIATarget.onAlert = function onAlert(alert) {
	var title = alert.name();
	log.logDebug("Alert "+title+" fired");
	if (title == "Statistics" || title == "Reload the card") {
		alert.cancelButton().tap();
		return true;
	}
	// test if your script should handle the alert, and if so, return true
	
	// otherwise, return false to use the default handler
	return false;
}


testCamipro();