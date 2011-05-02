package org.pocketcampus.core.communication.packet;

import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Encapsulate data relative to the protocol used by a PCP Packet
 */
public class Protocol {
	private String name;
	private String version;
	
	/**
	 * Creates a new Protocol object
	 * @param protocol
	 * @param version
	 */
	public Protocol(String protocol, String version) {
		this.name = protocol;
		this.version = version;
		validate();
	}
	
	/**
	 * @return the protocol name (currently, only 'PCP' is valid)
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * @return returns the protocol version
	 */
	public String getVersion() {
		return this.version;
	}
	
	/**
	 * Returns the complete protocol string (<i>[Name]/[Version]</i>)
	 */
	public String toString() {
		return this.name + "/" + this.version;
	}
	
	/**
	 * Validates this object and throws an IllegalArgumentException if not valid.
	 */
	private void validate() {
		// Currently, only "PCP" is a valid protocol name
		Pattern name = Pattern.compile("^PCP$");
		Pattern version = Pattern.compile("^[-0-9A-Za-z. ]+$");
		
		if (!name.matcher(this.name).matches() ||
				!version.matcher(this.version).matches())
			throw new IllegalArgumentException("Invalid protocol name or version");
	}
	
	/**
	 * Type adapter used by GSON to serialize and deserialize Protocol objects
	 */
	public static class GsonAdapter implements JsonSerializer<Protocol>, JsonDeserializer<Protocol> {

		@Override
		public Protocol deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context)
				throws JsonParseException {
			Pattern p = Pattern.compile("^(PCP)/([-0-9A-Za-z. ]+)$");
			Matcher m = p.matcher(json.toString());
			
			if (!m.matches()) {
				throw new JsonParseException("Malformed Protocol Declaration");
			}
			
			String pcpProtocol = m.group(1);
			String pcpVersion = m.group(2);
			
			return new Protocol(pcpProtocol, pcpVersion);
		}

		@Override
		public JsonElement serialize(Protocol src, Type typeOfSrc,
				JsonSerializationContext context) {
			
			return new JsonPrimitive(src.toString());
		}
		
	}
}