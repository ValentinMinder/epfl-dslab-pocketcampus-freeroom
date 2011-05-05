package org.pocketcampus.plugin.positioning;

import java.util.List;

import org.pocketcampus.provider.positioning.IGrid;
import org.pocketcampus.shared.plugin.map.Position;

public class Grid implements IGrid {
	
	private Position initialPosition_;
	private double DMax_;
	private double DMin_;
	private double cellLength_;
	private int  rowCell_;
	private List<Node> nodes_;
	private List<AccessPoint> APGrid_;
	private WifiLocation wifiLocation_;
	
	public Grid(){
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CartesianPoint convertPositionToCartesian(Position p) {
		// TODO Auto-generated method stub
		return null;
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
