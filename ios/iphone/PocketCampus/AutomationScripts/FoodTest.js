
function testFood() {
	log.logStart("Enter Food");
	
	window.elements()["Restaurants"].tap();
	delay(2);
	if(isCurrentNavBarTitle("Restaurants")) {
		log.logPass("Enter Food"); 
	} else {
		log.logFail("Enter Food");
		return false;
	}
	
	delay(1);
	
	log.logStart("Restaurants list");
	
	tableView = window.tableViews()[0];
	
	if (tableView.isVisible()) {
		log.logPass("Restaurants list");
	} else {
		if (new Date().getDay()%6==0) { //is weekend
			log.logPass("Restaurants list");
			log.logDebug("Weekend, exiting...");
			window.navigationBar().leftButton().tap(); //back
			return true;
		} else {
			log.logFail("Restaurants list");
			return false;
		}
	}
	log.logDebug("test");
	for (var i = 0; i<10; i++) {
		delay(0.5);
		tableView = window.tableViews()[0];
		cells = tableView.cells();
		var row = randomVisibleRowIndex(tableView);
		var restaurant = cells[row].name();
		log.logStart("Restaurant random selection : "+ restaurant);
		cells[row].tap();
		delay(0.5);
		if (isCurrentNavBarTitle(restaurant)) {
			log.logPass("Restaurant random selection : "+ restaurant);
			tapBack();
		} else {
			log.logFail("Restaurant random selection : "+ restaurant);
		}
	}
	
	/*delay(1);
	tableView = window.tableViews()[0];
	cells = tableView.cells();
	var rowRest = randomVisibleRowIndex(tableView);
	var ratingRest = cells[rowRest].name();
	//log.logStart("Menu rating testing. Selecting random restaurant : "+ratingRest);
	cells[rowRest].tap();
	delay(1);
	log.logDebug("test : "+window.tableViews().length);
	tableView = window.tableViews()[0];
	cells = tableView.cells();
	log.logDebug("test2 : "+cells[0].elements().length);
	log.logDebug("test3 : "+window.elements()["VotesLabel"].value());
	
	//log.logStart("Random meal rating (multiple times, must be enabled in app)");
	printElementsName(cells[0].elements());
	log.logDebug(cells[0].elements()["VotesLabel"].value());*/
}
