//
///*
// ********************* [ P O C K E T C A M P U S ] *****************
// * [    LICENCE    ] 	see "licence"-file in the root directory
// * [   MAINTAINER  ]	tarek.benoudina@epfl.ch
// * [     STATUS    ]    under developement
// *
// */

/**
 * Intersection class compute the result of geometric
 * forms intersection such lines , spheres ,...etc
 */
//package org.pocketcampus.provider.positioning;
//
//import java.util.ArrayList;
//
//
//import org.pocketcampus.map.util.CoordinateConverter;
//
//import android.util.Log;
//
//
//public class Intersection {
//
//	private Sphere sph1_;
//	private Sphere sph2_;
//	private Sphere sph3_;
//	private Sphere sph4_;
//	
//	public Intersection(Sphere sph1,Sphere sph2,Sphere sph3,Sphere sph4){
//		this.sph1_ = sph1;
//		this.sph2_ = sph2;
//		this.sph3_ = sph3;
//		this.sph4_ = sph4;
//		
//	}
//	
//	
//	
//	public Intersection() {
//		
//	}
//
//
//
//	public ArrayList<Position> interSpheres(){
//		ArrayList<Position> intersection = new ArrayList<Position>();
//		Position result1,result2;
//		int rz1,rz2;
//		double rx1,ry1;
//		double rx2,ry2;
//		double i1,i2;
//		double x1 = sph1_.getX();
//		double x2 = sph2_.getX();
//		double x3 = sph3_.getX();
//		double y1 = sph1_.getY();
//		double y2 = sph2_.getY();
//		double y3 = sph3_.getY();
//		double z1 = sph1_.getLevel();
//		double z2 = sph2_.getLevel();
//		double z3 = sph3_.getLevel();
//		double d1 = sph1_.getRadius()*(Math.pow(10,-6));
//		double d2 = sph2_.getRadius()*(Math.pow(10,-6));
//		double d3 = sph3_.getRadius()*(Math.pow(10,-6));
//		
//		double s2 = (x1-x2)*(x1+x2)+(y1-y2)*(y1+y2)+(z1-z2)*(z1+z2);
//		double s3 = (x1-x3)*(x1+x3)+(y1-y3)*(y1+y3)+(z1-z3)*(z1+z3);
//		
//		double a2 = (x1-x2);
//		double b2 = (y1-y2);
//		double c2 = (z1-z2);
//		double a3 = (x1-x3);
//		double b3 = (y1-y3);
//		double c3 = (z1-z3);
//		
//		double D1 = Math.pow(d1, 2);
//		double D2 = Math.pow(d2, 2);
//		double D3 = Math.pow(d3, 2);
//		
//		double r2 = (D2-D1+s2)/2;
//		double r3 = (D3-D1+s3)/2;
//		
//		double ba2 = b2/a2;
//		double ba3 = b3/a3;
//		double ca2 = c2/a2;
//		double ca3 = c3/a3;	
//		double ra2 = r2/a2;
//		double ra3 = r3/a3;
//			
//		double h1 = ba2-ba3;
//		double h2 = ca2-ca3;
//		double h3 = ra2-ra3;
//		
//		double h31 = h3/h1;
//		double h21 = h2/h1;
//		
//		// y = h31-h21 *z;
//		
//		double a4 = (b2*h21-c2)/a2;
//		double b4 = (r2-b2*h31)/a2;
//		
//		// x = a4 * z + b4;
//		
//		double DD1 = D1-(x1*x1)-(y1*y1)-(z1*z1);
//		double t1 = (a4*a4+h21*h21+1);
//		double t2 = 2*((a4*b4)-(a4*x1)-(h31*h21)+(y1*h21)-z1);
//		double t3 = ((b4*b4)-2*(b4*x1)+(h31*h31)-2*(y1*h31))-DD1;
//		
//		double delta = t2*t2-4*t1*t3;
//		
//		System.out.println(delta);
//		
//		if(delta > 0){
//		
//		 i1 = (((-t2)-Math.sqrt(delta))/2*t1);
//		 i2 = (((-t2)+Math.sqrt(delta))/2*t1);
//		 rz1= (int) i1;
//		 rz2= (int) i2;
//		 
//		 rx1 = a4*rz1+b4;
//		 rx2 = a4*rz2+b4;
//		 ry1 = h31-h21*rz1;
//		 ry2 = h31-h21*rz2;
//
//		 if(rx1 >0  && ry1>0 ){
//	     result1 = CoordinateConverter.convertCH1903ToLatLong(rx1, ry1, rz1);	 
//		 //result1 = new Position(rx1, ry1, rz1);
//		 intersection.add(result1);
//		 }
//		 else result1 = null;
//		 if(rx2 >0  && ry2>0){
//	     result2 = CoordinateConverter.convertCH1903ToLatLong(rx1, ry1, rz1);	 
//		 //result2 = new Position(rx2, ry2, rz2);
//		 intersection.add(result2);
//		 }
//		 else result2 = null;
//		 
////		   To see with the group if necessary to use sphere 4
////         because when testing it's impossible to get the point which
////         belongs to 4 spheres at same tame with very accuarte values of coordinates
//		 
////		   if(sph4_.pointOfSphere(rx1, ry1, rz1)){
//	      
////		      }else
//	      //intersection.add(result2);
//		
//		}else if(delta == 0)
//		{
//		 i1= -t2/2*t1;
//		 
//		 rz1= (int) i1;
//		 rx1 = a4*rz1+b4;
//		 ry1 = h31-h21*rz1;
//		 if(rx1 >0  && ry1>0 ){
//		 result1 = CoordinateConverter.convertCH1903ToLatLong(rx1, ry1, rz1);
//		 
//		 intersection.add(result1);
//		   
//		 }else result1 = null;
//		}
//		else System.out.println("No intersection");
//		
//		return intersection;
//			
//	}
//
//
//
//	public Position interLines(AccessPoint ap1 , AccessPoint ap2) {
//		Position result=null;
//		double x,y;
//		double x1,x2,y1,y2;
//		double d1,d2;
//		if(valid(ap1)&&valid(ap2)){
//
//		x1 = ap1.position().getLat();
//		y1 = ap1.position().getLon();
//		x2 = ap2.position().getLat();
//		y2 = ap2.position().getLon();
//		d1 = ap1.getEstimatedDistance()*(1.06*Math.pow(10, -5));
//		d2 = ap2.getEstimatedDistance()*(1.06*Math.pow(10, -5));
//		Log.d("Distance: ","D1 :"+d1);
//		Log.d("Distance: ","D2 :"+d2);
//		
//		double D1 = d1*d1-x1*x1-y1*y1;
//		double D2 = d2*d2-x2*x2-y2*y2;
//		double a1 = x2-x1;
//		double a2 = y2-y1;
//		
//		double b1 = (D1-D2)/(2*a1);
//		double b2 = a2/a1;
//		
//		double t1 = b2*b2;
//		double t2 = 2*(b2-y1-b1*b2);
//		double t3 = b1*b1-2*x1*b1-D1;
//		
//		double delta = t2*t2-4*t1*t3;
//
//		if(delta > 0){
//			
//			double  rY1 = ((-t2)-Math.sqrt(delta))/2*t1;
//			double  rY2 = ((-t2)+Math.sqrt(delta))/2*t1;
//		    System.out.println("rY1    :"+rY1);
//		    System.out.println("rY2    :"+rY2);
//			if(rY1>0){
//				 y = rY1;
//				 x = b1-b2*rY1;
//				 
//				 result= new Position(x,y,0);
//			}else if (rY2>0){
//				 y = rY2;
//				 x = b1-b2*rY2;
//				 
//			 result = new Position(x,y,0);
//			}
//		}else result = ap1.position();		
//		}
//			
//		return result ;
//	}
//	
//    public boolean valid(AccessPoint ap){
//    	boolean valid = true;
//    	if (ap==null)
//    		valid = false;
//    	
//    	return valid;
//    }
//
//
//	
//}
