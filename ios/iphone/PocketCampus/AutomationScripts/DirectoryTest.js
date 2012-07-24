#import "UIAutomationAddon.js"

target = UIATarget.localTarget();

window = target.frontMostApp().mainWindow();

log = UIALogger;

log.logStart("Enter Directory");

window.elements()["Directory"].tap();

if(!waitForLoaded(window, "Search bar")) {
	log.logFail("Enter Directory"); 
} else {
	log.logPass("Enter Directory");
}

log.logStart("Autocomplete ae");

searchBar = window.elements()["Search bar"];

searchBar.setValue("ae");

tableView = window.tableViews()[0];

loadingIndicator = window.activityIndicators()[0];

if (!waitVisible(tableView, 5)) {
	log.logFail("Autocomplete ae");
}

cells = tableView.cells();
var failedAutocomplete = false;
for (var i = 0; i<cells.length; i++) {
	if (cells[i].name().substring(0, 2).toLowerCase() != "ae") {
		log.logDebug("Found beginning with : "+cells[i].name().substring(0, 1).toLowerCase());
		failedAutocomplete = true;
		break;
	}
}

if (failedAutocomplete) {
	log.logFail("Autcomplete ae");
} else {
	log.logPass("Autcomplete ae");	
}

log.logStart("Autocomplete kjhsagdfkjhagsdjfh");

searchBar.setValue("kjhsagdfkjhagsdjfh"); //no result
target.delay(1);
if (waitVisible(tableView, 3)) { //tableView visible
	log.logDebug("tableView should not visible (no result)");
	log.logFail("Autocomplete kjhsagdfkjhagsdjfh"); //should not have displayed table view after no result
} else {
	log.logPass("Autocomplete kjhsagdfkjhagsdjfh");
}

for (var i = 0; i<20; i++) {
	
	var nb = Math.round(Math.random()*2);
	
	if (nb == 0) {
		nb = 1;
	}
	
	var string = randomString(nb);
	
	log.logStart("Autocomplete random : "+string);
	searchBar.setValue(string);
	delay(0.5);
	if (waitForVisible(tableView, 4)) {
		cells = tableView.cells();
		var row = Math.round(Math.random()*4);
		if (row >= cells.length) {
			row = cells.length-1;
		}
		log.logDebug("Found autocomplete result for : "+string+". Selecting row : "+cells[row].name());
		var tappedName = cells[row].name();
		if (tappedName.indexOf(",") != -1) { //direct result mode => skip
			log.logPass("Autocomplete random : "+string);
			continue;
		}
		cells[row].tap();
		delay(Math.round(Math.random()+0.51)); //autocomplete request are fast
		if (tableView.isVisible()) {
			log.logPass("Autocomplete random : "+string);
			cells = tableView.cells();
			var row2 = Math.round(Math.random()*4);
			if (row2 >= cells.length) {
				row2 = cells.length-1;
			}
			var personName = cells[row2].name();
			log.logStart("Search result selection : "+personName);
			cells[row2].tap();
			delay(Math.round(Math.random()+0.51));
			
			window.navigationBar().leftButton().tap(); //back
			log.logPass("Search result selection : "+personName);
			
		} else {
			log.logDebug("Autocomplete result "+tappedName+" lead to no search result. (Input : "+string+")");
			log.logFail("Autocomplete random : "+string);
		}
		
	} else {
		log.logDebug("No autocomplete result for : "+string+". Continuing.");
		log.logPass("Autocomplete random : "+string);
	}
}

log.logDebug("Autocomplete random tests finished");




