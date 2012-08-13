function testTransport() {
	enterPluginAndTest("Transport");
	
	delay(3);

	log.logStart("Select random destination and details");
	selectRandomDestinationAndDetails();
	log.logPass("Select random destination and details");
	
	
	log.logDebug("Transport test finished");
	return true;
	
	function selectDestinationAndDetails(destination) {
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
				break;
			}
		}
		if (!found) {
			log.logDebug("Could not find a cell for station "+destination);
			return false;
		}
	}
	
	function selectRandomDestinationAndDetails() {
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
}