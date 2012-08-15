#import "Credentials.js"
#import "AuthenticationUtils.js"

function testMoodle() {

	if(!logIn(username, password, "Camipro", savePasswd)){
		log.logDebug("Failed to log in");
		return false;
	}
	
	log.logDebug("Map test finished");
	tapBack();
	return true;		
}