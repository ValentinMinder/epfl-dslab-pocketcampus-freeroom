package org.pocketcampus.core.communication.packet;

import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Payload {
	public static void main(String[] args) {
		Gson g = new Gson();
		
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("K1", "V1");
		hm.put("K2", "V2");
		
		Payload p = new Payload(g.toJson(hm), "Payload/test", "0.1");
		
		System.out.println(g.toJson(p));
		
		String s = "{\"Module\":\"Payload/test\",\"Interface\":\"0.1\",\"Data\":{\"data\":\"{\\\"K1\\\":\\\"V1\\\",\\\"K2\\\":\\\"V2\\\"}\"}}";
		
		Payload p2 = g.fromJson(s, Payload.class);
		
		System.out.println("--END--");
	}
	
	/**
	 * Identifies uniquely the module to which the request is destined.<br />
	 * Value : "<i>[Module ID]</i>/<i>[Action]</i>"
	 */
	@SerializedName("Module")
	@Expose
	private String moduleId;
	
	/**
	 * Specifies how the payload will be handled.<br />
	 * Value : "<i>[Interface version]</i>/GSON"
	 */
	@SerializedName("Interface")
	@Expose
	private String interfaceVersion;
	
	/**
	 * Actual payload.<br />
	 * Value : A String representing a JSON Object
	 */
	@SerializedName("Data")
	@Expose
	private RawJson data;
	
	/**
	 * Instance creator for GSON
	 */
	@SuppressWarnings("unused")
	private Payload() {}
	
	/**
	 * Creates a Payload
	 * @param json the JSON data that this Payload object will contain (the actual payload)
	 * @param moduleId an ID which identify the module ("<i>[Module ID]</i>/<i>[Action]</i>")
	 * @param interfaceVersion the version of the interface used by the module to generate
	 * the payload
	 */
	public Payload(String json, String moduleId, String interfaceVersion) {
		this.data = new RawJson(json);
		this.moduleId = moduleId;
		this.interfaceVersion = interfaceVersion;
	}
	
	/**
	 * @return a String that represents the JSON object stored in this payload
	 */
	public String getData() {
		return this.data.getRaw();
	}
	
	/**
	 * @return the version of the interface used by the module to generate the payload
	 */
	public String getInterfaceVersion() {
		return this.interfaceVersion;
	}
	
	/**
	 * @return the module's ID
	 */
	public String getModuleId() {
		return this.moduleId;
	}
}
