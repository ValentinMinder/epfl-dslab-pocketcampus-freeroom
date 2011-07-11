package org.pocketcampus.shared.plugin.scanner;

import java.io.Serializable;

public class ScanResultBean implements Serializable {
	private String bssid_;
	private String capabilities_;
	private int frequency_;
	private int level_;
	private String ssid_;

	public void setBssid(String bssid_) {
		this.bssid_ = bssid_;
	}
	
	public void setCapabilities(String capabilities) {
		this.capabilities_ = capabilities;
	}
	
	public void setFrequency(int frequency) {
		this.frequency_ = frequency;
	}
	
	public void setLevel(int level) {
		this.level_ = level;
	}
	
	public void setSsid(String ssid) {
		this.ssid_ = ssid;
	}

	public int getLevel() {
		return level_;
	}

	public String getBssid() {
		return bssid_;
	}

	public String getCapabilities() {
		return capabilities_;
	}

	public String getSsid() {
		return ssid_;
	}

	public int getFrequency() {
		return frequency_;
	}
}
