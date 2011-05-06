package org.pocketcampus.core.parser;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

/**
 * Singleton class to handle JSON parsing using the Gson library. 
 * @author Florian
 */
public class Json {
	static private Gson gson_;
	
	private static Gson getInstance() {
		if(gson_ == null) {
			gson_ = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss Z").create();
		}
		
		return gson_;
	}
	
	public static <T> T fromJson(String json, Type typeOfT) throws JsonException {
		try {
			return getInstance().fromJson(json, typeOfT);
		} catch (JsonParseException e) {
			throw new JsonException();
		}
	}
}
