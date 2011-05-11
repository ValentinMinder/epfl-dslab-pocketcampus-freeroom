package org.pocketcampus.plugin.positioning;

import java.util.ArrayList;
import java.util.List;

public class Node {
	
	private CartesianPoint coordinates_;
	private int value_;
	private List<AccessPoint> ApList_;
	
	public Node(CartesianPoint _coord, int _value){
		this.coordinates_ = _coord;
		this.value_ = _value;
		this.ApList_ = new ArrayList<AccessPoint>();
	}

	public CartesianPoint getCoordinates() {
		return coordinates_;
	}

	public void setCoordinates(CartesianPoint coordinates_) {
		this.coordinates_ = coordinates_;
	}

	public int getValue() {
		return value_;
	}

	public void setValue(int value_) {
		this.value_ = value_;
	}
	
	public void increaseValue(){
		this.value_++;
	}
	
	public void setApList(AccessPoint ap){
		this.ApList_.add(ap);
	}
	
	public List<AccessPoint> getApList(){
		return this.ApList_;
	}
	
	
	public double getPlanDistance(CartesianPoint point){
		
		double distance ;
		double x = coordinates_.getX();
		double y = coordinates_.getY();
		double x1 = point.getX();
		double y1 = point.getY();
		
		distance = Math.sqrt((x-x1)*(x-x1)+(y-y1)*(y-y1));
		
		return distance;
	}
	
	

}
