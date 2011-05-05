package org.pocketcampus.plugin.positioning;

public class Node {
	
	private CartesianPoint coordinates_;
	private int value_;
	
	public Node(CartesianPoint _coord, int _value){
		this.coordinates_ = _coord;
		this.value_ = _value;
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
	
	
	
	

}
