package org.pocketcampus.plugin.transport;

import java.lang.reflect.Type;

import org.pocketcampus.shared.plugin.transport.Connection.Footway;
import org.pocketcampus.shared.plugin.transport.Connection.Part;
import org.pocketcampus.shared.plugin.transport.Connection.Trip;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;


public class PartDeserializer implements JsonDeserializer<Part> {

	@Override
	public Part deserialize(JsonElement part, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss Z").create();
		
		String partString = gson.fromJson(part, String.class);
		
		Trip trip = gson.fromJson(partString, Trip.class);
		if(trip.departureTime != null) {
			return trip;
		}
		
		Footway footway = gson.fromJson(partString, Footway.class);
		return footway;
	}

}
