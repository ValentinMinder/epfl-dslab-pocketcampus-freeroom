package org.pocketcampus.utils;

import org.osmdroid.util.GeoPoint;


public class CoordinateConverter {

/**
 * 
 * @param x northing in EPSG:4326
 * @param y easting in EPSG:4326
 * @return A Position in standard lat/long coordinates (WGS84)
 */
	public static GeoPoint convertEPSG4326ToLatLong(double x, double y) {	
		double a = 6378137;
		double lat = (Math.PI/2.0 - 2.0 * Math.atan(Math.exp(-y / a)));
		double lon = adjust_lon(x/a);

		//convert to degree
		lat = lat/Math.PI*180;
		lon = lon/Math.PI*180;

		return new GeoPoint(lat, lon);
	}

	/**
	 * @param lat Latitude in WGS84
	 * @param lon Longitude in WGS84
	 * @return A Position in standard EPSG:4326
	 */
	public static GeoPoint convertLatLongToEPSG4326(double lat, double lon) {
		double a = 6378137;
		
		//convert to radian
		lat = lat*Math.PI/180;
		lon = lon*Math.PI/180;
		
		double x = a*adjust_lon(lon);
		double y = a*Math.log(Math.tan(Math.PI/4.0 + 0.5*lat));

		return new GeoPoint(x, y);
	}
	
	/**
	 * 
	 * @param x northing in CH1903
	 * @param y easting in CH1903
	 * @return A Position in standard lat/long coordinates (WGS84)
	 */
	public static GeoPoint convertCH1903ToLatLong(double x, double y) {	
		double y_aux = (y - 600000)/1000000;
		double x_aux = (x - 200000)/1000000;

		double lat = 16.9023892
		+  3.238272 * x_aux
		-  0.270978 * Math.pow(y_aux,2)
		-  0.002528 * Math.pow(x_aux,2)
		-  0.0447   * Math.pow(y_aux,2) * x_aux
		-  0.0140   * Math.pow(x_aux,3);

		lat = lat * 100/36;

		double lng = 2.6779094
		+ 4.728982 * y_aux
		+ 0.791484 * y_aux * x_aux
		+ 0.1306   * y_aux * Math.pow(x_aux,2)
		- 0.0436   * Math.pow(y_aux,3);

		lng = lng * 100/36;

		return new GeoPoint(lat, lng);
	}
	
	private static double adjust_lon(double x) {
		x = (Math.abs(x) < Math.PI) ? x: (x - (Double.compare(x, 0)*2*Math.PI) );
		return x;
	}
}
