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

	@Override
	public Position getInitialPosition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getDMax() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getDMin() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNodeMin() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getCellLength() {
		// TODO Auto-generated method stub
		return 0;
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
	public int getRowCell() {
		// TODO Auto-generated method stub
		return 0;
	}

}
