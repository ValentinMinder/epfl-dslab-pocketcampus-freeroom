package org.pocketcampus.shared.plugin.scanner;

import java.io.Serializable;
import java.util.Set;

public class GsmCellLocationBean implements Serializable {
	private int lac_;
	private int cid_;

	public void setCid(int cid_) {
		this.cid_ = cid_;
	}
	
	public void setLac(int lac_) {
		this.lac_ = lac_;
	}
	
	public int getCid() {
		return cid_;
	}
	
	public int getLac() {
		return lac_;
	}
}
