package org.pocketcampus.plugin.positioning;

import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.shared.plugin.map.Position;

import android.content.Context;

public class Grid implements IGrid {
	
	private Context ctx_;
	private Position initialPosition_;
	private double DMax_;
	private double DMin_;
	private double cellLength_;
	private int  rowCell_;
	private List<Node> nodes_;
	private List<AccessPoint> APGrid_;
	private WifiLocation wifiLocation_;
	private Node PMin_;
	public Grid(Context _ctx){
		
		this.wifiLocation_ = new WifiLocation(_ctx);
		this.initialPosition_ = getInitialPosition();
		this.APGrid_ = getApGrid();
		this.DMax_ = getDMax();
		this.DMin_ = getDMin();
		this.cellLength_ = getCellLength();
		this.rowCell_ = getRowCell();
		this.nodes_ = getNodesList();
		
	}

	@Override
	public List<Node> getNodesList() {
		List<Node> nodeList = new ArrayList<Node>();
		CartesianPoint coordinates1,coordinates2,coordinates3;
		int RL = getRowCell();
		double CL = getCellLength();
		//Node PMin = getPMin();
		double size = RL+1;
		coordinates1 = PMin_.getCoordinates();
		double x1 = coordinates1.getX();
		double y1 = coordinates1.getY();
		double z1 = coordinates1.getZ();
		//nodeList.add(PMin);
		Node PNode ;
		// to take into cnsideration PMin
		Node PBuffer2 = PMin_; 
		for(int i=0;i<size;i++)
		{
			for(int j=0;j<size;j++)
			{
				double x,y,z;
				
				x = x1+j*CL;
				y = y1+i*CL;
				coordinates2 = new CartesianPoint(x, y, 0);
				PNode = new Node(coordinates2, 0);
				nodeList.add(PNode);
				
			}
		}
		return nodeList;
	}

	@Override
	public Position getInitialPosition() {
		return wifiLocation_.getWifiLocationPerCoefficient();
	}

	@Override
	public double getDMax() {
		AccessPoint ap ;
		ap = wifiLocation_.getWeakestAP();
		return ap.getEstimatedDistance();
	}

	@Override
	public double getDMin() {
		AccessPoint ap ;
		ap = wifiLocation_.getStrongestAP();
		return ap.getEstimatedDistance();
	}

	@Override
	public int getNodeMin() {
		int min = wifiLocation_.getMinNumberOfnodes(APGrid_);
		return min;
	}

	@Override
	public double getCellLength() {
		int CL,DMin;
		int NMin = getNodeMin();
		DMin = (int) getDMin();
		CL = 2*DMin/NMin;
		
		return CL;
	}
	

	@Override
	public int getRowCell() {
		int CL = 0;
		int RL = 4;
		CL= (int) getCellLength();
		double DMax = getDMax();
		if(CL!=0)
		RL = (int) (2 * Math.ceil(DMax/CL));
		return RL;
	}	

	@Override
	public AccessPoint getPMax() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node getPMin() {
		Node PMin;
		CartesianPoint P0,P1;
		double x,y,z,RL,CL;
		RL=getRowCell();
		CL=getCellLength();
		P0 = convertPositionToCartesian(initialPosition_);
//		x=P0.getX()-CL*(RL/2);
//		y=P0.getY()-CL*(RL/2);
		x=P0.getX()-getDMax();
		y=P0.getY()-getDMax();
		z=P0.getZ();
		P1=new CartesianPoint(x, y, z);
		PMin = new Node(P1,0);
		
		return PMin;
	}

	@Override
	public CartesianPoint convertPositionToCartesian(Position p) {
		CartesianPoint coordinates;
		double x,y,z;
		double lat = p.getLatitude();
		double lon = p.getLongitude();
		double alt = p.getAltitude();
		
		x = convertX(lat, lon, alt);
		y = convertY(lat, lon, alt);
		z = convertZ(lat, lon, alt);
		coordinates = new CartesianPoint(x, y, z);
		return coordinates;
	}
	
	public double convertX(double lat,double lon,double alt){
		double x = 0.0;
		double a = 6378137.0; // SemiAxisMajor
		double e2 = 0.00669438; // Eccentricity power2
		double alpha = Math.sqrt(1-e2*(Math.sin(lat)*Math.sin(lat)));
		double factor = (a/alpha)+alt;
		x = factor * Math.cos(lat)*Math.cos(lon);
		
		return x;
	}

	public double convertY(double lat,double lon,double alt){
		double y = 0.0;
		double a = 6378137.0; // SemiAxisMajor
		double e2 = 0.00669438; // Eccentricity power2
		double alpha = Math.sqrt(1-e2*(Math.sin(lat)*Math.sin(lat)));
		double factor = (a/alpha)+alt;
		y = factor * Math.cos(lat)*Math.sin(lon);
		
		return y;
	}
	
	public double convertZ(double lat,double lon,double alt){
		double z = 0.0;
		double a = 6378137.0; // SemiAxisMajor
		double e2 = 0.00669438; // Eccentricity power2
		double alpha = Math.sqrt(1-e2*(Math.sin(lat)*Math.sin(lat)));
		double factor = (a/alpha)+alt;
		z = factor * Math.sin(lat);
		
		return z;
	}
	
	@Override
	public Position convertCartesianToPosition(CartesianPoint cp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AccessPoint> getApGrid(){
		return wifiLocation_.getAccessPoints();
	}

}
