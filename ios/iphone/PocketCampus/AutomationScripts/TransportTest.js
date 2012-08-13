function testTransport() {
	enterPluginAndTest("Transport");

	delay(3);

	/*log.logStart("Select random destination and details");
	selectRandomDestinationAndDetailsAndReturn();
	log.logPass("Select random destination and details");*/

	testAddStation();
		
	/*if (selectDestinationAndDetailsAndReturn(addedStation)) {
		log.logPass("Add random station and test");
	} else {
		log.logFail("Add random station and test");
	}*/


	log.logDebug("Transport test finished");
	return true;

	function selectDestinationAndDetailsAndReturn(destination) {
		tableView = window.tableViews()[0];
		cells = tableView.cells();
		var found = false;
		for (var i = 0; i<cells.toArray().length; i++) {
			if (cells[i].name().indexOf(destination) != -1) {
				found = true;
				cells[i].tap();
				tableView = window.tableViews()[0];
				cells = tableView.cells();
				cells[randomRowInde(tableView)].tap();
				delay(1);
				tapBack();
				delay(1);
				tapBack();
				return true;
			}
		}
		if (!found) {
			log.logDebug("Could not find a cell for station "+destination);
			return false;
		}
	}

	function selectRandomDestinationAndDetailsAndReturn() {
		tableView = window.tableViews()[0];
		cells = tableView.cells();
		cells[randomRowIndex(tableView)].tap();
		delay(1);
		tableView = window.tableViews()[0];
		cells = tableView.cells();
		cells[randomRowIndex(tableView)].tap();
		delay(1);
		tapBack();
		delay(1);
		tapBack();

	}

	function tapBookmarks() {
		window.elements()["BookmarksButton"].tap();
		delay(1);
	}

	/* must be called from favorite stations view
	 * returns name of add station
	 */ 
	function addRandomStation() {
		window.navigationBar().leftButton().tap(); //edit button
		delay(0.5);
		window.navigationBar().rightButton().tap(); //add (+) station button
		delay(1);
		var searchBar = window.elements()['SearchBar'];
		var selectedAlreadyFavorite = false;
		do {
			selectedAlreadyFavorite = false;
			searchBar.setValue(randomString(2));
			delay(2); //wating for request to return
			tableView = window.tableViews()[0];
			cells = tableView.cells();
			var index = randomVisibleRowIndex(tableView);
			var selectedStation = cells[index].name();
			cells[index].tap();
			if 	(app.alert().isValid()) {
				log.logDebug("Did select an already existing fav station : "+selectedStation);
				selectedAlreadyFavorite = true;
			}
			delay(1);
		} while (selectedAlreadyFavorite);
		delay(1)
		window.navigationBar().leftButton().tap(); //done button
	}
	
	function testAddStation() {
		log.logStart("Add random station and test");
		tapBookmarks();
		var addedStation = addRandomStation();
		window.navigationBar().rightButton().tap(); //done button, leave fav stations view
		
		tableView = window.tableViews()[0];
		if(!tableViewContainsCellWithNameSubstring(tableView, addedStation)) {
			log.logFail("Add random station and test");
		}
		window.navigationBar().rightButton().tap();
		tableView = window.tableViews()[0];
		if(!tableViewContainsCellWithNameSubstring(tableView, addedStation)) {
			log.logFail("Add random station and test");
			return;
		}
		
		log.logPass("Add random station and test");
	}
}