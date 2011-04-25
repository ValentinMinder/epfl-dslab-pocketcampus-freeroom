package org.pocketcampus.shared.plugin.transport;

import java.io.Serializable;

public class RailwayMember implements Serializable, Comparable<RailwayMember> {
	private static final long serialVersionUID = -5872110731793642173L;
	private String type_;
	private int ref_;
	private String role_;
	private int num_;
	
	public void setType(String type) {
		type_ = type;
	}

	public void setRef(int ref) {
		ref_ = ref;
	}

	public void setRole(String role) {
		role_ = role;
	}

	@Override
	public int compareTo(RailwayMember arg0) {
		return (num_<arg0.getNum())?0:1;
	}

	private int getNum() {
		return num_;
	}

	public void setNum(int memberNum) {
		num_ = memberNum;
	}
	
	@Override
	public String toString() {
		return "("+num_+":"+ref_+","+type_+","+role_+")\n";
	}

	public String getRole() {
		return role_;
	}

	public int getRef() {
		return ref_;
	}

	public String getType() {
		return type_;
	}
}
