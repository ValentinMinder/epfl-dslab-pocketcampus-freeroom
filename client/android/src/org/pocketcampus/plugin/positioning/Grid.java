/*
 ********************* [ P O C K E T C A M P U S ] *****************
 * [   MAINTAINER  ]	tarek.benoudina@epfl.ch
 * [     STATUS    ]    stable
 *
 **************************[ C O M M E N T S ]**********************
 *
 * Represents a virtual Grid for HandOver algorithm
 *
 *******************************************************************
 */
package org.pocketcampus.plugin.positioning;

/**
 * Author:
 * Tarek Benoudina
 * 
 * Grid class 
 * contains nodes surrounding the access points 
 * to handle the user position
 */

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
	private VirtualConvert conversion_;
	
	
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
		return ap.getDistance();
	}

	@Override
	public double getDMin() {
		AccessPoint ap ;
		ap = wifiLocation_.getStrongestAP();
		return ap.getDistance();
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
		P0 = conversion_.convertPositionToCartesian(initialPosition_);
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
	public List<AccessPoint> getApGrid(){
		List<AccessPoint> ApListConvert = new ArrayList<AccessPoint>();
		List<AccessPoint> ApList =  wifiLocation_.getAccessPoints();
		List<Node> surroundNodeList = new ArrayList<Node>();
		AccessPoint apBuffer = null;
		for(AccessPoint ap : ApList){
			surroundNodeList = getSurroundedNeighbor(ap.getCoordinates());
			apBuffer = new AccessPoint(ap, surroundNodeList);
			ApListConvert.add(apBuffer);
		}
		return ApListConvert;
	}
	
	
	
	public List<Node> getSurroundedNeighbor(CartesianPoint point){
		List<Node> nodeList = getNodesList();
		List<Node> surroundedNodeList = new ArrayList<Node>();
		int size = nodeList.size()-4;
		Node node1 = null;//,node2,node3,node4;
		int length = nodeList.size();
		while(length >=size){
		double limit = getDMax();	
		for(Node node : nodeList){
			double dist = node.getPlanDistance(point);
			if(dist<limit){
				limit = dist;
				node1 = node;
			}		
		}
		surroundedNodeList.add(node1);
		nodeList.remove(node1);
		length = nodeList.size();
		}
		
		return surroundedNodeList; 
	}

}
