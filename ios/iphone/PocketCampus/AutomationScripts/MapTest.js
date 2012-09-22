function testMap() {
	enterPluginAndTest("Map");
	
	/* prep */
	
	var searchStrings = ["Aebischer", "SG", "SG1", "BC", "Candea", "Silviu", "RLC", "CM3", "Metro", "Parking", "Nic"];
	
	var mapView = window.elements()["EPFLMapView"];
	
	var searchBar = window.elements()["SearchBar"];
	
	var actionsButtonRect = window.toolbar().elements()["Others"].rect();
	//window.logElementTree();
	
	var floorDownButtonRect = window.toolbar().elements()["Floor down"].rect();
	var floorUpButtonRect = window.toolbar().elements()["Floor up"].rect();
	
	var centerOnEPFLButtonRect = null; //will ne set later, as it is in the action sheet
	
	/* end of prep */
	
	for (var i = 0; i<1000; i++) {
		log.logStart("Random map mouvements");
		randomMapMovementAndZoom(mapView);
		log.logPass("Random map mouvements");
		log.logStart("Leave plugin test");
		if (chance(0.6)) {
			tapBack();
			delay(2);
			enterPluginAndTest("Map", true);
		}
		log.logPass("Leave plugin test");
		if (i%3 == 1) {
			centerOnEPFL();
		}
		if (chance(0.3)) {
			floorChangeRandom();
		}
		if (chance(0.8)) {
			log.logStart("Random search");
			randomSearch();
			log.logPass("Random search");
		}
	}

	log.logDebug("Map test finished");
	tapBack();
	return true;


	function randomSearch() {
		window.navigationBar().rightButton().tap(); //search button
		delay(2.0);
		var index = Math.abs(Math.ceil(Math.random()*searchStrings.length)-1);
		log.logDebug("index : "+index+", string : "+searchStrings[index]);
		window.elements()["SearchBar"].setValue(searchStrings[index]);
		app.keyboard().buttons()["Search"].tap();
		if (chance(0.6)) {
			delay(Math.random()*2);
			tapBack();
			delay(2);
			enterPluginAndTest("Map", true);
			return;
		}
		if (chance(0.2)) {
			log.logDebug("Test canceling search");
			window.navigationBar().rightButton().tap(); //cancel button
		} else {
			delay(3.0);
			//printElementsName(mapView.elements(), false, 4);
			window.navigationBar().rightButton().tap(); //cancel button	
		}
	}
	
	function randomMapMovementAndZoom(mapView) {
		var y  = Math.random();
		if (y < 0.2) {
			y = 0.2;
		} else if (y > 0.8) {
			y = 0.8;
		} else {
			//ok, no risk to touch a nav or toolbar button
		}
	
		var startOffset = {x:Math.random(),y:y};
		var endOffset = {x:Math.random(),y:y};
		mapView.dragInsideWithOptions({duration:Math.random(), startOffset:startOffset, endOffset:endOffset});
		
		for (var i = 0; i<Math.random()*5; i++) {
			mapView.doubleTap(); //zoom in	
		}
	}
	
	function floorChangeRandom() {
		if (Math.random() <= 0.5) {
			target.tap(floorUpButtonRect);
		} else {
			target.tap(floorDownButtonRect);
		}
	
	}

	function centerOnEPFL() {
		log.logDebug("Centering on EPFL");
		target.tap(actionsButtonRect);
		var actionSheet = app.actionSheet();
		
		if (centerOnEPFLButtonRect == null) {
			centerOnEPFLButtonRect = actionSheet.elements()[1].rect(); //bug with API, starts at 1
		}
		delay(0.5);
		target.tap(centerOnEPFLButtonRect);
		delay(0.5);
	}
		
}