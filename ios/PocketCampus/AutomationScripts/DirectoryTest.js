
function testDirectory() {

	log.logStart("Enter Directory");
	
	window.elements()["Directory"].tap();
	delay(2);
	if(isCurrentNavBarTitle("Directory")) {
		log.logPass("Enter Directory"); 
	} else {
		log.logFail("Enter Directory");
		return false;
	}
	
	searchBar = window.elements()["Search bar"];
	
	tableView = window.tableViews()[0];
	
	log.logStart("Autocomplete kjhsagdfkjhagsdjfh");
	
	searchBar.setValue("kjhsagdfkjhagsdjfh"); //no result
	delay(0.3);
	//app.keyboard().logElementTree();
	app.keyboard().buttons()["Search"].tap();
	target.delay(1);
	if (waitVisible(tableView, 2)) { //tableView visible
		log.logDebug("tableView should not visible (no result)");
		log.logFail("Autocomplete kjhsagdfkjhagsdjfh"); //should not have displayed table view after no result
	} else {
		log.logPass("Autocomplete kjhsagdfkjhagsdjfh");
	}
	
	for (var i = 0; i<1000; i++) {
		
		var nb = Math.round(Math.random()*2);
		
		if (nb == 0) {
			nb = 1;
		}
		
		var string = randomString(nb);
		
		log.logStart("Autocomplete random : "+string);
		searchBar.setValue(string);
		delay(0.3);
		app.keyboard().buttons()["Search"].tap();
		delay(Math.random()+1); //autocomplete request are fast
		if (!waitForVisible(tableView, 4)) {
			log.logDebug("No autocomplete result for : "+string+". Continuing.");
			log.logPass("Autocomplete random : "+string);
			continue;
		}
		cells = tableView.cells();
		var check = checkElementsName(cells, new RegExp("^"+string, "i"));
		log.logDebug("test : "+check);
		if (check !== true) {
			log.logDebug("Found autocomplete result "+cells[check].name());
			log.logFail("Autocomplete random : "+string);
			continue;
		}
		
		var row = randomVisibleRowIndex(tableView);
	
		log.logDebug("Found autocomplete result for : "+string+". Selecting row : "+cells[row].name());
		var tappedName = cells[row].name();
		if (tappedName.indexOf(",") != -1) { //direct result mode => skip
			log.logDebug("Autocomplete transformed into search. Continuing.")
			log.logPass("Autocomplete random : "+string);
			continue;
		}
		
		cells[row].tap();
		delay(Math.round(Math.random()+1.5)); //search request 
		if (!tableView.isVisible()) {
			log.logDebug("Autocomplete result "+tappedName+" lead to no search result. (Input : "+string+")");
			log.logFail("Autocomplete random : "+string);
			continue;
		}
		log.logPass("Autocomplete random : "+string);
		
		cells = tableView.cells();
		var row2 = randomVisibleRowIndex(tableView);
		var personNameWithOrg = cells[row2].name();
		var personName;
		var indexOfComma = personNameWithOrg.indexOf(",");
		if (indexOfComma == -1) { //not found
			personName = personNameWithOrg;
		} else {
			personName = personNameWithOrg.substring(0, indexOfComma);
		}
		log.logStart("Search result selection : "+personName);
		cells[row2].tap();
		delay(1);
		if (window.navigationBar().name() == personName) {
			log.logPass("Search result selection : "+personName);	
		} else {
			log.logDebug("Could not select row. Maybe search request took to much time.");
			log.logFail("Search result selection : "+personName);
			continue;
		}
		
		//window.tableViews()["ABPersonTableView"].logElementTree();
		target.pushTimeout(0.5);
		var officeCell = window.tableViews()["ABPersonTableView"].cells()["office"];
		target.popTimeout();
		if (!officeCell.isValid()) {
			log.logDebug("No office cell found. Continuing.");
			window.navigationBar().leftButton().tap(); //back
			continue;
		}
		
		log.logDebug("Office cell found : "+ officeCell);
		log.logStart("Loading map from office info");
		officeCell.tap();
		delay(Math.random()*3+1);
		window.navigationBar().leftButton().tap();
		delay(1.5);
		window.navigationBar().leftButton().tap();
		log.logPass("Loading map from office info");
			
	}
	
	log.logDebug("Autocomplete random tests finished");
	window.navigationBar().leftButton().tap(); //back
	log.logDebug("Directory tests finished");
	return true;
}


