package org.pocketcampus.provider.positioning;

import java.util.List;

import org.pocketcampus.plugin.positioning.AccessPoint;
import org.pocketcampus.plugin.positioning.CartesianPoint;
import org.pocketcampus.plugin.positioning.Node;
import org.pocketcampus.shared.plugin.map.Position;

public interface IGrid {

public Position getInitialPosition();
public List<AccessPoint> getApGrid();
public double getDMax();
public double getDMin();
public int getNodeMin();
public double getCellLength(); 
public AccessPoint getPMax();
public Node getPMin();
public int getRowCell();
public CartesianPoint convertPositionToCartesian(Position p);
public Position convertCartesianToPosition(CartesianPoint cp);
public List<Node> getNodesList();
}
