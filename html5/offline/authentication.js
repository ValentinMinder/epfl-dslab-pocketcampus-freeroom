$( document ).delegate("#authentication", "pagebeforecreate", function() {
	console.log("page #authentication pagebeforecreate");
});
$( document ).delegate("#authentication", "pagecreate", function() {
	console.log("page #authentication pagecreate");
	$('input').keypress(function(e) {
		var kc = (e.keyCode || e.which || e.charCode);
		if(kc == 13) {
			//$('#authForm').submit();
			AuthenticationPlugin.loginUserToTequila();
		}
	});
});
$( document ).delegate("#authentication", "pageinit", function() {
	console.log("page #authentication pageinit");
});
$( document ).delegate("#authentication", "pagebeforeshow", function(event, data) {
	console.log("page #authentication pagebeforeshow");
	$('#authForm').find( 'input[name="password"]' ).val('');
});
$( document ).delegate("#authentication", "pageshow", function() {
	console.log("page #authentication pageshow");
	AuthenticationPlugin.authSucc = 0;
	$(':input:first').focus();
});
$( document ).delegate("#authentication", "pagehide", function() {
	console.log("page #authentication pagehide");
	if(!AuthenticationPlugin.authSucc && AuthenticationPlugin.callbackObject) {
		AuthenticationPlugin.callbackObject.authenticationCanceled();
	}
});




AuthenticationPlugin = function () {};






AuthenticationPlugin.authenticateToken = function (token, callback) {
	console.log("AuthenticationPlugin.authenticateToken");
	AuthenticationPlugin.tokenToAuthenticate = token;
	AuthenticationPlugin.callbackObject = callback;
	//localStorage.setItem("AUTHENTICATION_TOKEN_TO_AUTHENTICATE", token);
	//$.mobile.changePage("jqm.html#authentication", {transition: "pop", role: "dialog"});
	$.mobile.changePage("#authentication", "pop", false, false);
}

/*
AuthenticationPlugin.testing2 = function () {
	transport = new Thrift.Transport("http://128.178.77.233/a/thrift-js/pc-server.php/v3r1/json-authentication");
	protocol = new Thrift.Protocol(transport);
	client = new AuthenticationServiceClient(protocol);
	teqToken = client.getTequilaKeyForService(TypeOfService.SERVICE_MOODLE);
	alert(teqToken);
}
AuthenticationPlugin.testing4 = function () {
	transport = new Thrift.Transport("http://128.178.77.233/a/thrift-js/pc-server.php/v3r1/json-authentication");
	protocol = new Thrift.Protocol(transport);
	client = new AuthenticationServiceClient(protocol);
	sessId = client.getSessionIdForService(teqToken);
	localStorage.setObject('MOODLE_SESSION', sessId);
	alert(sessId);
	a = sessId.moodleCookie.split(';');
	for(i = 0; i < a.length; i++) {
		document.cookie = a[i];
	}
}
*/
AuthenticationPlugin.loginUserToTequila = function () {
	console.log("AuthenticationPlugin.loginUserToTequila");
	$.mobile.showPageLoadingMsg();
	username = $('#authForm').find( 'input[name="username"]' ).val();
	password = $('#authForm').find( 'input[name="password"]' ).val();
	$.post("tequila.php/cgi-bin/tequila/login", { username: username, password: password }).error(function(a){
		console.log("ERROR");
		$.mobile.hidePageLoadingMsg();
		PocketCampus.showToast(Strings.CONNECTION_ERROR);
	}).success(function(res){
		console.log("SUCCESS");
		if(res.indexOf("Set-Cookie: tequila_key=") == 0) {
			AuthenticationPlugin.tequilaCookie = res.split('=')[1].split(';')[0];
			AuthenticationPlugin.authenticateTokenRequest();
			AuthenticationPlugin.authSucc = 1;
			$(".ui-dialog").dialog("close");
		} else {
			$.mobile.hidePageLoadingMsg();
			$('#authForm').find( 'input[name="password"]' ).val('');
			PocketCampus.showToast(Strings.WRONG_CREDENTIALS);
		}
	}).complete(function(){
		console.log("COMPLETE");
	});
}

AuthenticationPlugin.authenticateTokenRequest = function () {
	console.log("AuthenticationPlugin.authenticateTokenRequest");
	document.cookie = "tequila_key=" + AuthenticationPlugin.tequilaCookie; // inject tequilaCookie
	$.get("tequila.php/cgi-bin/tequila/requestauth", { requestkey: AuthenticationPlugin.tokenToAuthenticate }).error(function(a){
		console.log("ERROR");
		$.mobile.hidePageLoadingMsg();
		PocketCampus.showToast(Strings.CONNECTION_ERROR);
	}).success(function(res){
		console.log("SUCCESS");
		if(res.indexOf("Location: ") == 0) { // if Tequila redirected
			AuthenticationPlugin.callbackObject.authenticationSucceeded();
		} else { // token timed out or user not signed in
			$.mobile.hidePageLoadingMsg();
			PocketCampus.showToast(Strings.BAD_TOKEN);
		}
	}).complete(function(){
		console.log("COMPLETE");
		document.cookie = "tequila_key=; expires=Thu, 01 Jan 1970 00:00:01 GMT;"; // destroy tequilaCookie
	});
}
