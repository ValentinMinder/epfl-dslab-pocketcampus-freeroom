$( document ).delegate("#moodle", "pagebeforecreate", function() {
	console.log("page #moodle pagebeforecreate");
});
$( document ).delegate("#moodle", "pagecreate", function() {
	console.log("page #moodle pagecreate");
});
$( document ).delegate("#moodle", "pageinit", function() {
	console.log("page #moodle pageinit");
});
$( document ).delegate("#moodle", "pagebeforeshow", function(event, data) {
	console.log("page #moodle pagebeforeshow");
	MoodlePlugin.courseId = 0;
	MoodlePlugin.clearDisplayCourses();
});
$( document ).delegate("#moodle", "pageshow", function(event, data) {
	console.log("page #moodle pageshow");
	$.mobile.showPageLoadingMsg();
	if(data && data.prevPage.length && data.prevPage[0].id == "authentication") {
		console.log("already authenticating, should get a separate callback");
		return;
	}
	if(localStorage.getObject("MOODLE_SESSION")) {
		console.log("already signed in, requesting courses list");
		MoodlePlugin.injectMoodleCookies();
		MoodlePlugin.getCoursesList(0, 1);
	} else {
		console.log("not signed in, starting auth");
		MoodlePlugin.getTequilaTokenForMoodle();
	}
});


$( document ).delegate("#moodle-course", "pagebeforeshow", function(event, data) {
	console.log("page #moodle-course pagebeforeshow");
	MoodlePlugin.courseId = parseInt(window.location.hash.replace( /.*id=/, "" ));
	MoodlePlugin.clearDisplayCourseSections();
});
$( document ).delegate("#moodle-course", "pageshow", function(event, data) {
	console.log("page #moodle-course pageshow");
	$.mobile.showPageLoadingMsg();
	if(data && data.prevPage.length && data.prevPage[0].id == "authentication") {
		console.log("already authenticating, should get a separate callback");
		return;
	}
	if(localStorage.getObject("MOODLE_SESSION")) {
		console.log("already signed in, requesting course sections");
		MoodlePlugin.injectMoodleCookies();
		MoodlePlugin.getCourseSections(0, 1);
	} else {
		console.log("not signed in, starting auth");
		MoodlePlugin.getTequilaTokenForMoodle();
	}
});


MoodlePlugin = function () {};

MoodlePlugin.courseId = 0;
MoodlePlugin.coursesTitleMap = new Array();

//// AUTHENTICATION CALLBACKS

MoodlePlugin.authenticationSucceeded = function () {
	console.log("MoodlePlugin.authenticationSucceeded");
	MoodlePlugin.getMoodleSession();
}

MoodlePlugin.authenticationCanceled = function () {
	console.log("MoodlePlugin.authenticationCanceled");
	history.back();
}

//// BUTTONS HANDLERS

MoodlePlugin.logoutUser = function () {
	localStorage.removeObject("MOODLE_SESSION");
	history.back();
}

MoodlePlugin.refreshCoursesList = function () {
	$.mobile.showPageLoadingMsg();
	MoodlePlugin.getCoursesList(1, 1);
}

MoodlePlugin.refreshSectionsList = function () {
	$.mobile.showPageLoadingMsg();
	MoodlePlugin.getCourseSections(1, 1);
}

//// DYNAMIC PAGES REGISTRATION AND DELEGATION

PocketCampus.registerDynamicPage("moodle-course", MoodlePlugin);

MoodlePlugin.showPage = function (urlObj, options) {
	/*MoodlePlugin.courseId = parseInt(urlObj.hash.replace( /.*id=/, "" ));
	sections = MoodlePlugin.getCourseSections();*/
	
	$page = $( "#moodle-course" );
	//$header = $page.children( ":jqmData(role=header)" );
	//$content = $page.children( ":jqmData(role=content)" );
	
	/*markup = "<ul data-role='listview' data-theme='g'>";
	for ( i = 1; i < sections.length; i++ ) {
		if(sections[i].iResources.length) {
			markup += "<li data-role='list-divider'>Week " + i + "</li>";
			for ( j = 0; j < sections[i].iResources.length; j++ ) {
				translatedUrl = sections[i].iResources[j].iUrl.replace("http://moodle.epfl.ch", "http://128.178.77.233/a/thrift-js/moodle.php");
				markup += "<li><a target=\"_blank\" href=\"" + translatedUrl + "\">" + sections[i].iResources[j].iName + "</a></li>";
			}
		}
	}
	markup += "</ul>";
	
	$header.find( "h1" ).html( MoodlePlugin.coursesTitleMap[MoodlePlugin.courseId] );
	$content.html( markup );*/
	
	//$page.page();
	//$content.find( ":jqmData(role=listview)" ).listview();
	options.dataUrl = urlObj.href;
	$.mobile.changePage( $page, options );
}

//// HELPERS

MoodlePlugin.injectMoodleCookies = function () {
	moodleCookies = localStorage.getObject("MOODLE_SESSION");
	if(moodleCookies) {
		moodleCookies = moodleCookies.moodleCookie.split(';');
		for(i = 0; i < moodleCookies.length; i++) {
			document.cookie = moodleCookies[i];
		}
	}
}

MoodlePlugin.destroyDocumentCookies = function () {
	documentCookies = document.cookie.split(';');
	for(i = 0; i < documentCookies.length; i++) {
		document.cookie = documentCookies[i].split('=')[0] + "=; expires=Thu, 01 Jan 1970 00:00:01 GMT;";
	}
}

MoodlePlugin.buildRequest = function () {
	sessId = new SessionId();
	sessId.tos = TypeOfService.SERVICE_MOODLE;
	moodleSession = localStorage.getObject("MOODLE_SESSION");
	sessId.moodleCookie = moodleSession.moodleCookie;
	moodleRequest = new MoodleRequest();
	moodleRequest.iSessionId = sessId;
	moodleRequest.iLanguage = "en";
	moodleRequest.iCourseId = MoodlePlugin.courseId;
	return moodleRequest;
}

//// NETWORK REQUESTS

MoodlePlugin.getTequilaTokenForMoodle = function () {
	console.log("MoodlePlugin.getTequilaTokenForMoodle");
	transport = new Thrift.Transport("pc-server.php/v3r1/json-moodle");
	protocol = new Thrift.Protocol(transport);
	client = new MoodleServiceClient(protocol);
	client.getTequilaTokenForMoodle(1).error(function(a){
		console.log("ERROR");
		$.mobile.hidePageLoadingMsg();
		PocketCampus.showToast(Strings.CONNECTION_ERROR);
	}).success(function(a){
		console.log("SUCCESS");
		MoodlePlugin.teqToken = a;
		//localStorage.setObject("MOODLE_TEQUILATOKEN", teqToken);
		AuthenticationPlugin.authenticateToken(MoodlePlugin.teqToken.iTequilaKey, MoodlePlugin);
	}).complete(function(){
		console.log("COMPLETE");
	});
}

MoodlePlugin.getMoodleSession = function () {
	console.log("MoodlePlugin.getMoodleSession");
	transport = new Thrift.Transport("pc-server.php/v3r1/json-moodle");
	protocol = new Thrift.Protocol(transport);
	client = new MoodleServiceClient(protocol);
	//teqToken = localStorage.getObject("MOODLE_TEQUILATOKEN");
	//teqToken = new TequilaToken(teqToken);
	client.getMoodleSession(MoodlePlugin.teqToken, 1).error(function(a){
		console.log("ERROR");
		$.mobile.hidePageLoadingMsg();
		PocketCampus.showToast(Strings.CONNECTION_ERROR);
	}).success(function(res){
		console.log("SUCCESS");
		localStorage.setObject("MOODLE_SESSION", res);
		//MoodlePlugin.injectMoodleCookies();
		//MoodlePlugin.getCoursesList(0, 1);
		console.log("re-triggering pageshow on activePage");
		$("#" + $.mobile.activePage.data("url")).trigger("pageshow");
	}).complete(function(){
		console.log("COMPLETE");
	});
}

MoodlePlugin.getCoursesList = function (bypassCacheFlag, useCacheFlag) {
	console.log("MoodlePlugin.getCoursesList");
	var moodleRequestClosure = MoodlePlugin.buildRequest();
	if(!bypassCacheFlag)
		if(coursesListReplyCached = localStorage.getObject(moodleRequestClosure))
			return MoodlePlugin.displayCourses(coursesListReplyCached.iCourses);
	transport = new Thrift.Transport("pc-server.php/v3r1/json-moodle");
	protocol = new Thrift.Protocol(transport);
	client = new MoodleServiceClient(protocol);
	client.getCoursesList(moodleRequestClosure, 1).error(function(a){
		console.log("ERROR");
		$.mobile.hidePageLoadingMsg();
		PocketCampus.showToast(Strings.CONNECTION_ERROR);
	}).success(function(coursesListReply){
		console.log("SUCCESS");
		if(coursesListReply.iStatus == 200) {
			if(useCacheFlag)
				localStorage.setObject(moodleRequestClosure, coursesListReply);
			MoodlePlugin.displayCourses(coursesListReply.iCourses);
		} else if (coursesListReply.iStatus == 407) {
			localStorage.removeObject("MOODLE_SESSION");
			MoodlePlugin.getTequilaTokenForMoodle();
		} else { // 404
			$.mobile.hidePageLoadingMsg();
			PocketCampus.showToast(Strings.UPSTREAM_SERVER_DOWN);
		}
	}).complete(function(){
		console.log("COMPLETE");
	});
}

MoodlePlugin.getCourseSections = function (bypassCacheFlag, useCacheFlag) {
	console.log("MoodlePlugin.getCourseSections");
	var moodleRequestClosure = MoodlePlugin.buildRequest();
	if(!bypassCacheFlag)
		if(sectionsListReplyCached = localStorage.getObject(moodleRequestClosure))
			return MoodlePlugin.displayCourseSections(sectionsListReplyCached.iSections);
	transport = new Thrift.Transport("pc-server.php/v3r1/json-moodle");
	protocol = new Thrift.Protocol(transport);
	client = new MoodleServiceClient(protocol);
	client.getCourseSections(moodleRequestClosure, 1).error(function(a){
		console.log("ERROR");
		$.mobile.hidePageLoadingMsg();
		PocketCampus.showToast(Strings.CONNECTION_ERROR);
	}).success(function(sectionsListReply){
		console.log("SUCCESS");
		if(sectionsListReply.iStatus == 200) {
			if(useCacheFlag)
				localStorage.setObject(moodleRequestClosure, sectionsListReply);
			MoodlePlugin.displayCourseSections(sectionsListReply.iSections);
		} else if(sectionsListReply.iStatus == 407) {
			localStorage.removeObject("MOODLE_SESSION");
			MoodlePlugin.getTequilaTokenForMoodle();
		} else if(sectionsListReply.iStatus == 405) {
			$.mobile.hidePageLoadingMsg();
			PocketCampus.showToast("No course was specified");
		} else { // 404
			$.mobile.hidePageLoadingMsg();
			PocketCampus.showToast(Strings.UPSTREAM_SERVER_DOWN);
		}
	}).complete(function(){
		console.log("COMPLETE");
	});
}

//// UI

MoodlePlugin.displayCourses = function (coursesList) {
	console.log("MoodlePlugin.displayCourses");
	
	var $page = $( "#moodle" );
	var $header = $page.children( ":jqmData(role=header)" );
	var $content = $page.children( ":jqmData(role=content)" );
	
	var markup = "<ul data-role='listview' data-inset='true'>";
	MoodlePlugin.coursesTitleMap = new Array();
	for(i = 0; i < coursesList.length; i++) {
		MoodlePlugin.coursesTitleMap[coursesList[i].iId] = coursesList[i].iTitle;
		markup += "<li><a href=\"#moodle-course?id=" + coursesList[i].iId + "\">" + coursesList[i].iTitle + "</a></li>";
	}
	markup += "</ul>";
	if(!coursesList.length)
		markup += "<p class='middle-of-screen'>No courses.</p>";
	
	$header.find( "h1" ).html( "Courses" );
	$content.html( markup );
	
	$page.page();
	$content.find( ":jqmData(role=listview)" ).listview();
	
	$("#moodle").children(":jqmData(role=header)").append("<a href='javascript:MoodlePlugin.logoutUser()' data-icon='delete' class='ui-btn-left'>Logout</a>" ).trigger("create");
	$("#moodle").children(":jqmData(role=header)").append("<a href='javascript:MoodlePlugin.refreshCoursesList()' data-icon='gear' class='ui-btn-right'>Refresh</a>" ).trigger("create");
	$.mobile.hidePageLoadingMsg();
}

MoodlePlugin.displayCourseSections = function (sections) {
	console.log("MoodlePlugin.displayCourseSections");
	//$( ...new markup that contains widgets... ).appendTo( ".ui-page" ).trigger( "create" ); // not very much working
	//$( "#dashboard" ).append("<markup />" ).trigger('create') // working
	
	var $page = $( "#moodle-course" );
	var $header = $page.children( ":jqmData(role=header)" );
	var $content = $page.children( ":jqmData(role=content)" );
	
	var markup = "<ul data-role='listview'>";
	for ( i = 1; i < sections.length; i++ ) {
		if(!sections[i].iResources.length)
			continue;
		sections.notEmpty = 1;
		markup += "<li data-role='list-divider'>Week " + i;
		//markup += "<span class='ui-li-count'>" + sections[i].iResources.length + "</span>";
		markup += "</li>";
		for ( j = 0; j < sections[i].iResources.length; j++ ) {
			translatedUrl = sections[i].iResources[j].iUrl.replace("http://moodle.epfl.ch", "moodle-a.php");
			markup += "<li><a target=\"_blank\" href=\"" + translatedUrl + "\">";
			markup += "<h3>" + sections[i].iResources[j].iName + "</h3>";
			translatedUrl = translatedUrl.substring(0, ((translatedUrl.indexOf("?") + 1) || (translatedUrl.length + 1)) - 1);
			translatedUrl = translatedUrl.substring(translatedUrl.lastIndexOf("/") + 1, translatedUrl.length);
			markup += "<p>" + translatedUrl + "</p>";
			//markup += "<p class='ui-li-aside'>SAVED</p>";
			markup += "</a></li>";
		}
	}
	markup += "</ul>";
	if(!sections.notEmpty)
		markup += "<p class='middle-of-screen'>No sections.</p>";
	
	$header.find( "h1" ).html( MoodlePlugin.coursesTitleMap[MoodlePlugin.courseId] );
	$content.html( markup );
	
	$page.page();
	$content.find( ":jqmData(role=listview)" ).listview();
	
	$("#moodle-course").children(":jqmData(role=header)").append("<a href='javascript:MoodlePlugin.refreshSectionsList()' data-icon='gear' class='ui-btn-right'>Refresh</a>" ).trigger("create");
	$.mobile.hidePageLoadingMsg();
}

MoodlePlugin.clearDisplayCourses = function () {
	console.log("MoodlePlugin.clearDisplayCourses");
	
	var $page = $( "#moodle" );
	var $header = $page.children( ":jqmData(role=header)" );
	var $content = $page.children( ":jqmData(role=content)" );
	
	$header.find( "h1" ).html( "Courses" );
	$content.html( "" );
	
	$page.page();
	
	$("#moodle").children(":jqmData(role=header)").find(".ui-btn").remove();
}

MoodlePlugin.clearDisplayCourseSections = function () {
	console.log("MoodlePlugin.clearDisplayCourseSections");
	
	var $page = $( "#moodle-course" );
	var $header = $page.children( ":jqmData(role=header)" );
	var $content = $page.children( ":jqmData(role=content)" );
	
	$header.find( "h1" ).html( MoodlePlugin.coursesTitleMap[MoodlePlugin.courseId] );
	$content.html( "" );
	
	$page.page();
	
	$("#moodle-course").children(":jqmData(role=header)").find(".ui-btn").remove();
}


