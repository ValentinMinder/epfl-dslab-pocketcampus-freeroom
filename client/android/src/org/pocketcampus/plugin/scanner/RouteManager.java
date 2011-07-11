package org.pocketcampus.plugin.scanner;

import java.io.File;
import java.util.ArrayList;

import android.os.Environment;

public class RouteManager {
	private static final String ROUTE_DATA_DIR = "Routes";
	
	private boolean sdAvailable_;
	private boolean sdWriteable_;
	
	private void checkSdAvailability() {
		sdAvailable_ = false;
		sdWriteable_ = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			sdAvailable_ = sdWriteable_ = true;
		    
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			sdAvailable_ = true;
			sdWriteable_ = false;
		    
		} else {
			sdAvailable_ = sdWriteable_ = false;
		}
	}
	
	/*public ArrayList<Route> loadRoutes() {
		checkSdAvailability();
		
		if(!sdAvailable_) {
			return null;
		}
		
		String path = Environment.getExternalStorageDirectory() + File.separator + ROUTE_DATA_DIR + File.separator;
		
		File dir = new File(path);
		File[] routeFileList = dir.listFiles();
		
		ArrayList<Route> routes = new ArrayList<Route>();
		
		for (int i = 0; i < routeFileList.length; i++) {
			routes.add(new Route(routeFileList[i]));
		}
		
		return routes;
	}

	public Route getRoute(long routeHash) {
		ArrayList<Route> routes = loadRoutes();
		
		for(Route route : routes) {
			if(route.hashCode() == routeHash) {
				return route;
			}
		}
		
		return null;
	}*/

}





















