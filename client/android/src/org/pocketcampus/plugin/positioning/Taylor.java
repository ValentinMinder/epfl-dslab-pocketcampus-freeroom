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
	Matrix vectorB;
	double[][] arrayB;
	double b1,b2,b3;
	double x1,x2,x3,x4;
	double y1,y2,y3,y4;
	double D1,D2,D3,D4;
	x1 = ap1.position().getLatitude();
	x2 = ap2.position().getLatitude();
	x3 = ap3.position().getLatitude();
	x4 = ap4.position().getLatitude();
	y1 = ap1.position().getLongitude();
	y2 = ap2.position().getLongitude();
	y3 = ap3.position().getLongitude();
	y4 = ap4.position().getLongitude();
	D1 = ap1.getDistance();
	D2 = ap2.getDistance();
	D3 = ap3.getDistance();
	D4 = ap4.getDistance();
	arrayB = new double [3][1];
	b1 = x1*x1-x2*x2+y1*y1-y2*y2+D2*D2-D1;
	b2 = x1*x1-x3*x3+y1*y1-y3*y3+D3*D3-D1;
	b3 = x1*x1-x4*x4+y1*y1-y4*y4+D4*D4-D1;
		
	arrayB [0][0]= b1/2;
	arrayB [1][0]= b2/2;
	arrayB [2][0]= b3/2;
	
	vectorB = new Matrix(arrayB);
	
	System.out.println("Level :"+ ap1.getSignalLevel()+"Path Loss : "+ap1.getPathLoss()+" Distance :"+ap1.getDistance());
	System.out.println("Level :"+ ap2.getSignalLevel()+"Path Loss : "+ap2.getPathLoss()+" Distance :"+ap2.getDistance());
	System.out.println("Level :"+ ap3.getSignalLevel()+"Path Loss : "+ap3.getPathLoss()+" Distance :"+ap3.getDistance());
	System.out.println("Level :"+ ap4.getSignalLevel()+"Path Loss : "+ap4.getPathLoss()+" Distance :"+ap4.getDistance());
	
	return vectorB;
}


private Matrix matrixA(AccessPoint ap1,AccessPoint ap2,AccessPoint ap3,AccessPoint ap4) {
	Matrix matrix;
	double arrayA[][];
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
	arrayA = new double[3][2];
	arrayA[0][0] = x1-x2;
	arrayA[1][0] = x1-x3;
	arrayA[2][0] = x1-x4;
	arrayA[0][1] = y1-y2;
	arrayA[1][1] = y1-y3;
	arrayA[2][1] = y1-y4;
	
	matrix = new Matrix(arrayA);
	
	return matrix;
}


public Position taylorEquation() {
	
	Matrix transposeA;
	Matrix inverseProduct;
	Matrix productAT;
	Matrix secondProductAT;
	Matrix vectorB;
	Matrix solutionVector;
	Position result;
	vectorB = this.vetcor_;
	transposeA = this.matrix_.transpose();
	productAT = transposeA.times(matrix_);
	inverseProduct = productAT.inverse();
	secondProductAT = inverseProduct.times(transposeA);
	solutionVector = secondProductAT.times(vectorB);
	
	if(solutionVector.getColumnDimension()==1){
	double lat = solutionVector.get(0,0);
	double lon = solutionVector.get(1,0);
	
	result = new Position(lat, lon, 0.0);
	System.out.println("Solution :"+ result.toString());
	return result;
	}else return null;
	
}

	
}
