
/*
 ********************* [ P O C K E T C A M P U S ] *****************
 * [    LICENCE    ] 	see "licence"-file in the root directory
 * [   MAINTAINER  ]	tarek.benoudina@epfl.ch
 * [     STATUS    ]    stable
 *
 */

/**
 * Cercle Class for triangulation 
 * 
 */
package org.pocketcampus.plugin.positioning;

public class  Cercle {

	
	private double x_;
	private double y_;
	private double radius_;
	
	
	public Cercle(double x,double y,double radius){
		
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
