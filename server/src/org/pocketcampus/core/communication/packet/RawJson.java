package org.pocketcampus.core.communication.packet;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

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
	 * Creates a new RawJson object
	 * @param any raw JSON string
	 */
	public RawJson(String data) {
		this.data = data;
	}
	
	/**
	 * Returns the raw JSON string contained in this RawJson
	 */
	public String toString() {
		return this.data;
	}
	
	public static class GsonAdapter implements JsonSerializer<RawJson>, JsonDeserializer<RawJson> {

		@Override
		public RawJson deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			return new RawJson(json.getAsJsonObject().toString());
		}
		
		@Override
		public JsonElement serialize(RawJson src, Type typeOfSrc,
				JsonSerializationContext context) {
			
			JsonParser parser = new JsonParser();
			return parser.parse(src.data);
		}
	}
}
