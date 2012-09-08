function testTransport() {
	enterPluginAndTest("Transport");

	delay(1);
	
	LAUSANNE_FLON_COORDINATES = {'latitude':(46520795/1000000.0), 'longitude':(6630344/1000000.0)};
	BERN_COORDINATES = {latitude:(46948825/1000000.0), longitude:(7439122/1000000.0)};
	EPFL_COORDINATES = {latitude:(46522206/1000000.0), longitude:(6566134/1000000.0)}
	
	while(testAddRandomStation());
	
	while(testRemoveRandomStation());
	
	testNearestFavoriteStation();

	log.logDebug("Transport test finished");
	tapBack();
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

	function testSelectRandomDestinationAndDetailsAndReturn() {
		log.logStart("Select random destination and details");
		tableView = window.tableViews()[0];
		cells = tableView.cells();
		cells[randomRowIndex(tableView)].tap();
		delay(1.5);
		tableView = window.tableViews()[0];
		cells = tableView.cells();
		cells[randomRowIndex(tableView)].tap();
		delay(1.5);
		tapBack();
		delay(1.5);
		tapBack();
		log.logPass("Select random destination and details");
	}

	function tapBookmarks() {
		window.elements()["BookmarksButton"].tap();
		delay(1.5);
	}

	/* 
	 * returns name of add station
	 */ 
	function addStation(stationOrNullForRandom) {
		if (!isCurrentNavBarTitle("My stations")) {
			tapBookmarks();	
		}
		target.pushTimeout(1);
		if (window.tableViews()[0].cells().isValid() && window.tableViews()[0].cells().length == 11) { //"Automatic" + 10 fav stations
			log.logDebug("Cannot add more than 10 stations.");
			return false;
		}
		
		var didEnterEditMode = false;
		
		if (chance(0.4) && window.tableViews()[0].cells().length > 0) {
			didEnterEditMode = true; //add station can be done in both modes. Test both
			log.logDebug("Try add station in edit mode");
			window.navigationBar().elements()['Edit'].tap(); //edit button	
		}
		
		window.toolbar().elements()['Add'].tap(); //add (+) station button
		delay(1);
		var searchBar = window.elements()['SearchBar'];
		var selectedAlreadyFavorite = false;
		var selectedStation;
		do {
			selectedAlreadyFavorite = false;
			var text = stationOrNullForRandom;
			if (text == null) {
				text = randomString(2);
			}
			searchBar.setValue(text);
			delay(2); //wating for request to return
			tableView = window.tableViews()[0];
			cells = tableView.cells();
			var index = randomVisibleRowIndex(tableView);
			if (stationOrNullForRandom != null) {
				index = stationOrNullForRandom;
			}
			selectedStation = cells[index].name();
			cells[index].tap();
			if 	(app.alert().isValid()) {
				log.logDebug("Did select an already existing fav station : "+selectedStation);
				delay(1);
				if(stationOrNullForRandom != null) {
					searchBar.tapWithOptions({tapOffset:{x:0.90, y:0.81}}); //only way to access cancel button !!
					delay(1)
					window.navigationBar().leftButton().tap(); //done edit button
					return stationOrNullForRandom;
				}
				selectedAlreadyFavorite = true;
				delay(1);
			}
		} while (selectedAlreadyFavorite);
		delay(1)
		if (didEnterEditMode) {
			window.navigationBar().leftButton().tap(); //done edit button	
		}
		return selectedStation;
	}
	
	function testAddRandomStation() {
		log.logStart("Add random station and test");
		if (!isCurrentNavBarTitle("My stations")) {
			tapBookmarks();	
		}
		var addedStation = addStation(null);
		if (addedStation === false) {
			log.logDebug("Max number of stations reached");
			log.logPass("Add random station and test");
			return false;
		}
		log.logDebug("added station : "+addedStation);
		tableView = window.tableViews()[0];
		if(!tableViewContainsCellWithNameSubstring(tableView, addedStation)) {
			log.logDebug("Fail case 1");
			log.logFail("Add random station and test");
		}
		if (tableView.cells().length == 2) {
			log.logPass("Add random station and test"); //mean should add another station before checking main lsit
			return true;
		}
		window.navigationBar().rightButton().tap(); //done button
		delay(1);
		tableView = window.tableViews()[0];
		if(!tableViewContainsCellWithNameSubstring(tableView, addedStation) && !window.elements()[addedStation].isValid()) {
			target.captureScreenWithName("TestingStationPresence-"+addedStation);
			log.logDebug("Fail case 2");
			log.logFail("Add random station and test");
			return false;
		}
		
		log.logPass("Add random station and test");
		return true;
	}
	
	
	
	function testRemoveRandomStation() {
		if (!isCurrentNavBarTitle("My stations")) {
			tapBookmarks();	
		}
		log.logStart("Remove random station");
		if (app.navigationBar().elements()['Edit'].isValid()) {
			app.navigationBar().elements()['Edit'].tap();	
		}
		delay(1);
		tableView = window.tableViews()[0];
		cells = tableView.cells();
		var nbCellsBeforeRemoval = cells.length;
		if (nbCellsBeforeRemoval == 0) {
			log.logDebug("No station, cannot remove station");
			app.navigationBar().rightButton().tap(); //done button
			log.logPass("Remove random station");
			return false;
		}
		var index = randomVisibleRowIndex(tableView);
		var removedStation = cells[index].name();
		log.logDebug("Removing station : "+removedStation);
		var cell = cells[index];
		cell.switches()[0].tap(); //show delete button
		cell.buttons()[0].tap(); //tap delete button
		delay(0.5);
		cells = tableView.cells();
		if (nbCellsBeforeRemoval > 1 && cells.length != nbCellsBeforeRemoval-1) {
			log.logDebug("Excpected "+(nbCellsBeforeRemoval-1)+", after removal found "+cells.length);
			log.logDebug("Fail case 1");
			log.logFail("Remove random station");
			app.navigationBar().leftButton().tap(); //leaving edit mode
			app.navigationBar().rightButton().tap(); //leaving fav stations view
			return false;
		} else if ((nbCellsBeforeRemoval == 1 && cells.length != 0) || (nbCellsBeforeRemoval == 0 && !window.elements().firstWithPredicate("name CONTAINS 'Touch the plus buton to add a station'").isValid())) {
			log.logDebug("Fail case 2");
			log.logFail("Remove random station");
			app.navigationBar().rightButton().tap(); //leaving fav stations view
		} else {
			//OK, continue
		}
		delay(1);
		if (cells.length > 0) {
			app.navigationBar().leftButton().tap(); //done edit button	
		}
		app.navigationBar().rightButton().tap(); //done button
		delay(1.5);
		tableView = window.tableViews()[0];
		if (nbCellsBeforeRemoval > 2 && tableView.cells().length == nbCellsBeforeRemoval-2 && !tableViewContainsCellWithNameSubstring(tableView, removedStation)) { //-1 for removed stationa and -1 because dep station is on top ("From :") label
			log.logDebug("State of test : removed station and at least 2 are remaining");
			log.logPass("Remove random station");
		} else if (nbCellsBeforeRemoval == 2 && window.elements().firstWithPredicate("name CONTAINS 'At least two stations are'").isValid() && !tableView.isVisible()) {
			log.logDebug("State of test : removed station and only 1 remaining");
			log.logPass("Remove random station");
		} else if (nbCellsBeforeRemoval == 1 && window.elements().firstWithPredicate("name CONTAINS 'Tap the bookmarks button to manage'").isValid() && !tableView.isVisible()) {
			log.logDebug("State of test : removed station and no station remaining");
			log.logPass("Remove random station");
		} else {
			log.logDebug("Fail case 3, nbCellsBeforeRemoval : "+nbCellsBeforeRemoval);
			log.logFail("Remove random station");
		}
		return true;
	}
	
	
	/* must be called with capacity for 3 stations to be added */
	function testNearestFavoriteStation() {
		addStation("Lausanne-Flon");
		addStation("EPFL");
		addStation("Bern");
		window.tableViews()[0].cells()[1].tap(); //select another cell (need to do that because if automatic cell is already selected, tapping it selects the first in stations
		window.tableViews()[0].cells()[0].tap(); //select "Automatic" cell 
		app.navigationBar().rightButton().tap(); //done button
		delay(1);
		
		log.logStart("Testing nearest station, location Lausanne-Flon");
		target.setLocation(LAUSANNE_FLON_COORDINATES);
		log.logDebug("Waiting 20 seconds that prev. location validity expires");
		delay(21); //must wait for previous location to expire
		window.navigationBar().rightButton().tap(); //refresh
		delay(4);
		window.logElementTree();
		if (window.elements()['Lausanne-Flon'].isValid()) {
			log.logPass("Testing nearest station, location Lausanne-Flon");
		} else {
			log.logFail("Testing nearest station, location Lausanne-Flon");
		}
		
		log.logStart("Testing nearest station, location EPFL");
		target.setLocation(EPFL_COORDINATES);
		log.logDebug("Waiting 20 seconds that prev. location validity expires");
		delay(21); //must wait for previous location to expire
		window.navigationBar().rightButton().tap(); //refresh
		delay(4);

		if (window.elements()['EPFL'].isValid()) {
			log.logPass("Testing nearest station, location EPFL");
		} else {
			log.logFail("Testing nearest station, location EPFL");
		}
		
		log.logStart("Testing nearest station, location Bern");
		target.setLocation(BERN_COORDINATES);
		log.logDebug("Waiting 20 seconds that prev. location validity expires");
		delay(21); //must wait for previous location to expire
		window.navigationBar().rightButton().tap(); //refresh
		delay(4);
		
		if (window.elements()['Bern'].isValid()) {
			log.logPass("Testing nearest station, location Bern");
		} else {
			log.logFail("Testing nearest station, location Bern");
		}
		
	}
}






