package org.pocketcampus.plugin.transport;

import java.lang.reflect.Type;

import org.pocketcampus.shared.plugin.transport.Connection.Footway;
import org.pocketcampus.shared.plugin.transport.Connection.Part;
import org.pocketcampus.shared.plugin.transport.Connection.Trip;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class PartSerializer implements JsonSerializer<Part> {
	GsonBuilder builder_;
	
	public PartSerializer(GsonBuilder builder) {
		builder_ = builder;
	}

	@Override
	public JsonElement serialize(Part part, Type arg1, JsonSerializationContext arg2) {
		Gson gson = builder_.create();
		
		if(part instanceof Trip) {
			String jsonString = (gson).toJson((Trip)part);
			return new JsonPrimitive(jsonString);
		}
		
		if(part instanceof Footway) {
			String jsonString = (new Gson()).toJson((Footway)part);
			return new JsonPrimitive(jsonString);
		}
		
		// shouldn't happen
		return null;
	}

}
