package org.pocketcampus.core.communication.packet;

/**
 * The RawJson class allows an easy and straightforward serialization and deserialization
 * by GSON of the data contained in a JSON String.
 * It is intended to use for fields which Java type is unknown at the time of
 * deserialization, which GSON normally wouldn't allow to deserialize as a raw String (since
 * it is in fact a JSON Object of unknown Java type).
 * This workaround actually stores a String (which reprensents a raw JSON Object)
 * instead of an Object of unknown type (which is not feasible with GSON).
 */
public class RawJson {
	private String data;
	
	/**
	 * Instance creator for GSON
	 */
	private RawJson() {}
	
	public RawJson(String data) {
		this.data = data;
	}
	
	public String getRaw() {
		return this.data;
	}
	
	public String toString() {
		return this.data;
	}
}
