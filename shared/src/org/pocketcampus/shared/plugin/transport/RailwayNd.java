package org.pocketcampus.shared.plugin.transport;

import java.io.Serializable;

public class RailwayNd implements Serializable, Comparable<RailwayNd> {
	private static final long serialVersionUID = 3806438787363820531L;
	private int num_;
	private int ref_;
	
	public void setNum(int num) {
		num_ = num;
	}
	
	public int getNum() {
		return num_;
	}
	
	@Override
	public int compareTo(RailwayNd other) {
		return (num_<other.getNum())?0:1;
	}

	public void setRef(int ref) {
		ref_ = ref;
	}
	
	public int getRef() {
		return ref_;
	}
	
	@Override
	public String toString() {
		return "("+num_+":"+ref_+")\n";
	}
}
