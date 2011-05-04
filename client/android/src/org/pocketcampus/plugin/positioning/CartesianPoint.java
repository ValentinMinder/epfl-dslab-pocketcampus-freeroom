package org.pocketcampus.plugin.positioning;

public class CartesianPoint {
	
	private double x_;
	private double y_;
	private double z_;
	
	public CartesianPoint(double _x,double _y,double _z){
		this.x_ = _x;
		this.y_ = _y;
		this.z_ = _z;
	}

	public double getX() {
		return x_;
	}

	public void setX(double _x) {
		this.x_ = _x;
	}

	public double getY() {
		return y_;
	}

	public void setY(double _y) {
		this.y_ = _y;
	}

	public double getZ() {
		return z_;
	}

	public void setZ(double _z) {
		this.z_ = _z;
	}
	
	
	

}
