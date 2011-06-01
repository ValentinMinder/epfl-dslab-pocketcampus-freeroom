
/*
 ********************* [ P O C K E T C A M P U S ] *****************
 * [    LICENCE    ] 	see "licence"-file in the root directory
 * [   MAINTAINER  ]	tarek.benoudina@epfl.ch
 * [     STATUS    ]    stable
 *
 */

/**
 * Sphere Class represents the area where the Wifi signal 
 * is detected 
 * 
 */
package org.pocketcampus.plugin.positioning;

/**
 * Author : Tarek
 *          Benoudina
 *          
 * Sphere class ,
 * 
 * models the wifi signal sphere.
 * 
 */
public class Sphere {

	
	private double x_;
	private double y_;
	private int Level;
	private double radius_;
	
	
	public Sphere(double x,double y,int level,double radius){
		
		this.x_=x;
		this.y_=y;
		this.Level=level;
		this.radius_=radius;
	}
	
	public double getX(){
		return this.x_;
	}
	
	public double getY(){
		return this.y_;
	}
	
	public int getLevel(){
		return this.Level;
	}
	
	public double getRadius(){
		return this.radius_;
	}
	
	public boolean pointOfSphere(double x,double y,int z){
		boolean belongTo = false;
		double eq = (x_-x)*(x_-x)+(y_-y)*(y_-y)+(Level-z)*(Level-z);
		if(eq == (radius_*radius_))
			belongTo = true;
		
		return belongTo;
	}
}

