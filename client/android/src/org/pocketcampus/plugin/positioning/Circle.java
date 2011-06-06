/*
 ********************* [ P O C K E T C A M P U S ] *****************
 * [   MAINTAINER  ]	tarek.benoudina@epfl.ch
 * [     STATUS    ]    stable
 *
 **************************[ C O M M E N T S ]**********************
 *
 *******************************************************************
 */

package org.pocketcampus.plugin.positioning;

/**
 * Author : Tarek
 *          Benoudina
 *          
 * Circle class (triangulation)
 * 
 * returns intersection point 
 * 
 */

public class Circle {

	private AccessPoint Ap_;
	private double x_;
	private double y_;
	private double radius_;
	

	
	public Circle(AccessPoint _ap){
		
		this.Ap_ = _ap;
		this.x_=_ap.position().getLatitude();
		this.y_=_ap.position().getLongitude();
		this.radius_=_ap.getDistance();
	}
	
	public Circle(double x,double y,double radius){
		
		this.x_=x;
		this.y_=y;
		this.radius_=radius;
	}
	
	public double getX(){
		return this.x_;
	}
	
	public double getY(){
		return this.y_;
	}
	
	public double getRadius(){
		return this.radius_;
	}
	
	public boolean pointOfSphere(double x,double y){
		boolean belongTo = false;
		double eq = (x_-x)*(x_-x)+(y_-y)*(y_-y);
		if(eq == (radius_*radius_))
			belongTo = true;
		
		return belongTo;
	}

	
	
}
