/*
 ********************* [ P O C K E T C A M P U S ] *****************
 * [   MAINTAINER  ]	tarek.benoudina@epfl.ch
 * [     STATUS    ]    stable
 *
 **************************[ C O M M E N T S ]**********************
 *
 *
 *******************************************************************
 */
package org.pocketcampus.plugin.positioning;

/**
 * Author:
 * Tarek Benoudina
 * 
 * IGrid interface
 */

import java.util.List;

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
public List<Node> getNodesList();
}
