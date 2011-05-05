package org.pocketcampus.plugin.positioning;

import java.util.List;

import org.pocketcampus.provider.positioning.IGrid;
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
		
		return null;
	}

	@Override
	public Position getInitialPosition() {
		return wifiLocation_.getWifiLocationPerCoefficient();
	}

	@Override
	public double getDMax() {
		AccessPoint ap ;
		ap = wifiLocation_.getWeakestAP(APGrid_);
		return ap.getEstimatedDistance();
	}

	@Override
	public double getDMin() {
		AccessPoint ap ;
		ap = wifiLocation_.getStrongestAP(APGrid_);
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
	public AccessPoint getPMin() {
		
		return null;
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
