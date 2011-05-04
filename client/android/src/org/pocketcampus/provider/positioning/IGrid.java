package org.pocketcampus.provider.positioning;

import org.pocketcampus.plugin.positioning.AccessPoint;
import org.pocketcampus.plugin.positioning.CartesianPoint;
import org.pocketcampus.shared.plugin.map.Position;

public interface IGrid {

public Position getReferencePosition();
public double getDMax();
public double getDMin();
public int getNodeMin();
public double getCellLength(); 
public AccessPoint getPMax();
public AccessPoint getPMin();
public CartesianPoint convertPositionToCartesian(Position p);
public Position convertCartesianToPosition(CartesianPoint cp);

}
