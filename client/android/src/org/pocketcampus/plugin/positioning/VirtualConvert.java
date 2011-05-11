package org.pocketcampus.plugin.positioning;

import org.pocketcampus.shared.plugin.map.Position;

public class VirtualConvert {
	
	private Position position_;
	private CartesianPoint coordinates_;
	
	public VirtualConvert(Position pos){
		this.position_ = pos;
		this.coordinates_ = convertPositionToCartesian(pos);
		}
	
	public VirtualConvert(CartesianPoint coordinates){
		this.coordinates_ = coordinates;
		this.position_ = convertCartesianToPosition(coordinates);
	}

	
	
	
	public CartesianPoint convertPositionToCartesian(Position p) {
		CartesianPoint coordinates;
		double x,y,z;
		double lat = p.getLatitude();
		double lon = p.getLongitude();
		double alt = p.getAltitude();
		
		x = convertlatToX(lat, lon, alt);
		y = convertLongToY(lat, lon, alt);
		z = convertLatToZ(lat, lon, alt);
		coordinates = new CartesianPoint(x, y, z);
		return coordinates;
	}
	
	public double convertlatToX(double lat,double lon,double alt){
		double x = 0.0;
		double a = 6378137.0; // SemiAxisMajor
		double e2 = 0.00669438; // Eccentricity power2
		double alpha = Math.sqrt(1-e2*(Math.sin(lat)*Math.sin(lat)));
		double factor = (a/alpha)+alt;
		x = factor * Math.cos(lat)*Math.cos(lon);
		
		return x;
	}

	public double convertLongToY(double lat,double lon,double alt){
		double y = 0.0;
		double a = 6378137.0; // SemiAxisMajor
		double e2 = 0.00669438; // Eccentricity power2
		double alpha = Math.sqrt(1-e2*(Math.sin(lat)*Math.sin(lat)));
		double factor = (a/alpha)+alt;
		y = factor * Math.cos(lat)*Math.sin(lon);
		
		return y;
	}
	
	public double convertLatToZ(double lat,double lon,double alt){
		double z = 0.0;
		double a = 6378137.0; // SemiAxisMajor
		double e2 = 0.00669438; // Eccentricity power2
		double alpha = Math.sqrt(1-e2*(Math.sin(lat)*Math.sin(lat)));
		double factor = (a/alpha)+alt;
		z = factor * Math.sin(lat);
		
		return z;
	}
	
	public Position convertCartesianToPosition(CartesianPoint cp) {
		// TODO Auto-generated method stub
		return null;
	}

}
