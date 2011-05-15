package org.pocketcampus.shared.core.communication.packet;

import java.util.Date;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * POJO Structure for a PCP Packet
 */
public class Packet {
	@Expose
	@SerializedName("Protocol")
	private Protocol protocol;
	
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
	
	
	/**
	 * Creates a new POJO PCP Packet
	 * @param payload
	 * @param generator describes the entity that generated the packet
	 * @param options
	 */
	public Packet(Payload payload, String generator, Options options) {
		this.payload = payload;
		this.generator = generator;
		this.options = options;
		
		this.protocol = new Protocol("PCP", "1.0");
		this.date = new Date();
	}
	
	
	/**
	 * @return the protocol of this packet
	 */
	public Protocol getProtocol() {
		return this.protocol;
	}
	
	/**
	 * @return the generation date of this packet
	 */
	public Date getDate() {
		return this.date;
	}
	
	/**
	 * @return the string identifying the generator of this packet
	 */
	public String getGenerator() {
		return this.generator;
	}
	
	/**
	 * @return link options associated to this packet
	 */
	public Options getOptions() {
		return this.options;
	}
	
	/**
	 * @return this packet's payload
	 */
	public Payload getPayload() {
		return this.payload;
	}
}
