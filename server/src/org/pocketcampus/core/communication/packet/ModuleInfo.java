package org.pocketcampus.core.communication.packet;

import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pocketcampus.core.communication.Status;
import org.pocketcampus.core.communication.exceptions.PcpException;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Encapsulates data relative to the module to which the payload is targeted
 */
public class ModuleInfo {
	private String moduleId;
	private String action;
	
	/**
	 * Creates a new Module
	 * @param moduleId
	 * @param action
	 */
	public ModuleInfo(String moduleId, String action) {
		this.moduleId = moduleId;
		this.action = action;
	}
	
	/**
	 * @return the ID of the targeted module
	 */
	public String getModuleId() {
		return this.moduleId;
	}
	
	/**
	 * @return the targeted action
	 */
	public String getAction() {
		return this.action;
	}
	
	/**
	 * Returns a PCP-compliant representation of this ModuleInfo.
	 * @return a String in the "[Module ID]/[Action]" format
	 */
	public String toString() {
		return this.moduleId + "/" + this.action;
	}
	
	
	/**
	 * Type adapter used by GSON to serialize and deserialize Protocol objects
	 */
	public static class GsonAdapter implements JsonSerializer<ModuleInfo>, JsonDeserializer<ModuleInfo> {

		@Override
		public ModuleInfo deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			
			Pattern p = Pattern.compile("(.+)/(.+)");
			Matcher m = p.matcher(json.toString());
			
			if (!m.matches()) {
				PcpException bpe = new PcpException("Malformed module declaration", Status.BAD_REQUEST);
				throw new JsonParseException(bpe);
			}
			
			String moduleId = m.group(1);
			String action = m.group(2);
			
			return new ModuleInfo(moduleId, action);
		}

		@Override
		public JsonElement serialize(ModuleInfo src, Type typeOfSrc,
				JsonSerializationContext context) {
			return new JsonPrimitive(src.toString());
		}
		
	}
}
