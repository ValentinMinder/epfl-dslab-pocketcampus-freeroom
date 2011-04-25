package org.pocketcampus.shared.plugin.transport;

import java.io.Serializable;
import java.util.TreeSet;

public class RailwayWay implements Serializable, Comparable<RailwayWay> {
	private static final long serialVersionUID = 5667853752543481303L;
	private TreeSet<RailwayNd> nds_;
	private int num_;
	
	public RailwayWay() {
		nds_ = new TreeSet<RailwayNd>();
	}
	
	public TreeSet<RailwayNd> getNds() {
		return nds_;
	}

	public void setNum(int id) {
		num_ = id;
	}
	
	public void addNd(RailwayNd node) {
		nds_.add(node);
	}

	public int getNum() {
		return num_;
	}
	
	@Override
	public String toString() {
		return "("+num_+": "+nds_+")";
	}

	@Override
	public int compareTo(RailwayWay other) {
		return (num_<other.getNum())?0:1;
	}
}
