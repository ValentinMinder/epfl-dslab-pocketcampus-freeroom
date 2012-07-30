function testMap() {
	log.logStart("Enter Map");
	window.elements()["Map"].tap();
	delay(2);
	if(isCurrentNavBarTitle("Map")) {
		log.logPass("Enter Map"); 
	} else {
		log.logFail("Enter Map");
	}
	
	var mapView = window.elements()["EPFLMapView"];
	
	log.logStart("Random map mouvements");
	randomMapMovements(mapView);
	log.logPass("Random map mouvements");
	
	
		
	
	log.logDebug("Map test finished");
	return true;
	
}

function randomMapMovements(mapView) {
	var nbZoom = 4;
	
	for (var i = 0; i<nbZoom; i++) {
		var startOffset = {x:Math.random(),y:Math.random()};
		var endOffset = {x:Math.random(),y:Math.random()};
		mapView.dragInsideWithOptions({duration:Math.random(), startOffset:startOffset, endOffset:endOffset});
		mapView.dragInsideWithOptions({duration:Math.random(), startOffset:endOffset, endOffset:startOffset}); //keep sure that we stay on EPFL area	
		if (Math.random() < 0.4) {
			delay(3); //let yome time to overlay to load
		}
		mapView.doubleTap(); //zoom in
	}
	
	for (var i = 0; i<nbZoom; i++) {
		var startOffset = {x:Math.random(),y:Math.random()};
		var endOffset = {x:Math.random(),y:Math.random()};
		mapView.dragInsideWithOptions({duration:Math.random(), startOffset:startOffset, endOffset:endOffset});
		mapView.dragInsideWithOptions({duration:Math.random(), startOffset:endOffset, endOffset:startOffset}); //keep sure that we stay on EPFL area
		if (Math.random() < 0.4) {
			delay(3); //let yome time to overlay to load
		}
		mapView.tapWithOptions({touchCount:2}); //zoom out
	}
}