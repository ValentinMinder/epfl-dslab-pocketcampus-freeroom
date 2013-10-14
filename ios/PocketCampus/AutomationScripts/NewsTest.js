function testNews() {
	log.logStart("Enter News");
	
	window.elements()["News"].tap();
	delay(2);
	if(isCurrentNavBarTitle("News")) {
		log.logPass("Enter News"); 
	} else {
		log.logFail("Enter News");
	}
	
	log.logStart("Check news list presence");
	target.pushTimeout(20);
	if (window.tableViews()["NewsList"].isValid()) {
		log.logPass("Check news list presence");
		target.popTimeout();
	} else {
		log.logFail("Check news list presence");
		target.popTimeout();
		return false;
	}
	
	var tableView = window.tableViews()["NewsList"];
	
	for (var i = 0; i<1000; i++) {
		
		var row = randomRowIndex(tableView);
		var name = tableView.cells()[row].name();
		log.logStart("Select news "+row+" : "+name);
		tableView.scrollToElementWithName(name);
		delay(0.2);
		tableView.cells()[row].tap();
		delay(1);
		
		if (isCurrentNavBarTitle(name)) {
			log.logPass("Select news "+row+" : "+name);
		} else {
			log.logFail("Select news "+row+" : "+name);
			return false;
		}
		
		
		var scrollView = window.scrollViews()["NewsItemScrollView"];
		if (Math.random()<0.5) {
			log.logStart("Scrolling into News item");
			for (var j = 0; j<5; j++) {
				scrollView.scrollDown();
				delay(0.2);
			}
			log.logPass("Scrolling into News item");
		}
		if (Math.random()<0.5) {
			log.logStart("Check ActionSheet");
			window.navigationBar().rightButton().tap(); //action button
			var actionSheet = app.actionSheet();
			if (actionSheet.isValid()) {
				log.logPass("Check ActionSheet");
				var cancelButton = actionSheet.elements()[2]; //bug with API, actionSheet.cancelButton() return null.
				cancelButton.tap();
			} else {
				log.logFail("Action sheet test");
			}
		}
		tapBack();
	}
	
	tapBack();
	log.logDebug("News test finished");
	return true;
	
}