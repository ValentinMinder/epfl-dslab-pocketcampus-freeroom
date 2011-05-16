package org.pocketcampus.plugin.positioning;

import org.pocketcampus.plugin.positioning.utils.Matrix;
import org.pocketcampus.shared.plugin.map.Position;



public class Taylor {

//private Cercle c1_,c2_,c3_,c4_;
private AccessPoint ap1_,ap2_,ap3_,ap4_;	
private double D1_,D2_,D3_,D4_;
private Matrix matrix_;
private Matrix vetcor_;
private Position position_;


public Taylor (AccessPoint ap1,AccessPoint ap2,AccessPoint ap3,AccessPoint ap4){

    this.ap1_ = ap1;
    this.ap2_ = ap2;
    this.ap3_ = ap3;
    this.ap4_ = ap4;
	this.matrix_ = matrixA(ap1_,ap2_,ap3_,ap4_);
	this.vetcor_ = matrixB(ap1_,ap2_,ap3_,ap4_);
	this.position_ = taylorEquation();
}


private Matrix matrixB(AccessPoint ap1,AccessPoint ap2,AccessPoint ap3,AccessPoint ap4) {
	double b1,b2,b3;
	double x1,x2,x3,x4;
	double y1,y2,y3,y4;
	x1 = ap1.position().getLatitude();
	x2 = ap2.position().getLatitude();
	x3 = ap3.position().getLatitude();
	x4 = ap4.position().getLatitude();
	y1 = ap1.position().getLongitude();
	y2 = ap2.position().getLongitude();
	y3 = ap3.position().getLongitude();
	y4 = ap4.position().getLongitude();
	return null;
}


private Matrix matrixA(AccessPoint ap1,AccessPoint ap2,AccessPoint ap3,AccessPoint ap4) {
	double a1,a2;
	double b1,b2;
	double c1,c2;
	double x1,x2,x3,x4;
	double y1,y2,y3,y4;
	x1 = ap1.position().getLatitude();
	x2 = ap2.position().getLatitude();
	x3 = ap3.position().getLatitude();
	x4 = ap4.position().getLatitude();
	y1 = ap1.position().getLongitude();
	y2 = ap2.position().getLongitude();
	y3 = ap3.position().getLongitude();
	y4 = ap4.position().getLongitude();
	a1 = x1-x2;
	a2 = y1-y2;
	b1 = x1-x3;
	b2 = y1-y3;
	c1 = x1-x4;
	c2 = y1-y4;
	
	
	
	return null;
}


private Position taylorEquation() {
	
	return null;
}
	
}
