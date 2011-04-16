package org.pocketcampus.core.communication.packet;

import java.util.Date;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Packet {
	private String handledPcpVersion = "0.1";
	
	@Expose
	@SerializedName("Protocol")
	private String protocol;
	
	@Expose
	@SerializedName("Date")
	private Date date;
	
	@Expose
	@SerializedName("Generator")
	private String generator;
	
	@Expose
	@SerializedName("Options")
	private Options options;
	
	@Expose
	@SerializedName("Payload")
	private Payload payload;
	
	private Packet() {}
}
