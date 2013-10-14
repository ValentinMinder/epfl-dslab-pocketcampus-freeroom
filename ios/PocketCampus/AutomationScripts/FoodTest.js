
function testFood() {
	enterPluginAndTest("Restaurants");
	
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
	
	var nbCells = tableView.cells().toArray().length;
	log.logDebug("Found "+nbCells+" restaurants to test");
	for (var i = 0; i<nbCells; i++) {
		delay(0.5);
		tableView = window.tableViews()[0];
		cells = tableView.cells();
		var row = i;
		var restaurant = cells[row].name();
		log.logStart("Restaurant "+row+" selection : "+ restaurant);
		cells[row].tap();
		delay(0.5);
		if (isCurrentNavBarTitle(restaurant)) {
			log.logPass("Restaurant "+row+" selection : "+ restaurant);
			tableView = window.tableViews()[0];
			for (var j = 0; j<Math.random()*8; j++) {
				tableView.scrollDown();	
			}
			var mapButton = window.navigationBar().rightButton();
			if (chance(1) && mapButton != null && mapButton.isValid()) { //some restaurants cannot be displayed on map
				log.logStart("Displaying restaurant on Map: "+ restaurant);
				mapButton.tap();
				delay(1);
				var mapView = window.elements()["EPFLMapView"];
				delay(4);
				var staticTexts = mapView.scrollViews()[0].popover().staticTexts();
				var found = false;
				for (var k = 0; k<staticTexts.length; k++) {
					var name = staticTexts[k].name();
					if (name != null && (name.indexOf(restaurant) != -1 || restaurant.indexOf(name) != -1)) {
						found = true;
						break;
					}
				}
				if (found) {
					log.logPass("Displaying restaurant on Map: "+ restaurant);
				} else {
					log.logFail("Displaying restaurant on Map: "+ restaurant);
				}
				tapBack();
			}
			delay(0.5);
			tapBack();
		} else {
			log.logFail("Restaurant "+row+" selection : "+ restaurant);
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
