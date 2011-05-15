package org.pocketcampus.shared.core.communication.packet;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Payload {
	/**
	 * Identifies uniquely the module to which the request is destined.<br />
	 * Value : "<i>[Module ID]</i>/<i>[Action]</i>"
	 */
	@SerializedName("Module")
	@Expose
	private ModuleInfo moduleId;
	
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
	 * Creates a Payload
	 * @param json the JSON data that this Payload object will contain (the actual payload)
	 * @param moduleId an ID which identify the module ("<i>[Module ID]</i>/<i>[Action]</i>")
	 * @param interfaceVersion the version of the interface used by the module to generate
	 * the payload
	 */
	public Payload(String json, ModuleInfo moduleId, String interfaceVersion) {
		this.data = new RawJson(json);
		this.moduleId = moduleId;
		this.interfaceVersion = interfaceVersion;
	}
	
	/**
	 * @return a String that represents the JSON object stored in this payload
	 */
	public String getData() {
		return this.data.toString();
	}
	
	/**
	 * @return the version of the interface used by the module to generate the payload
	 */
	public String getInterfaceVersion() {
		return this.interfaceVersion;
	}
	
	/**
	 * @return informations about the targeted module
	 */
	public ModuleInfo getModuleInfos() {
		return this.moduleId;
	}
}
