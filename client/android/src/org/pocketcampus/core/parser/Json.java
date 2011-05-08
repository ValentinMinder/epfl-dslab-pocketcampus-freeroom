package org.pocketcampus.core.parser;

import java.lang.reflect.Type;

import org.pocketcampus.plugin.transport.PartDeserializer;
import org.pocketcampus.shared.plugin.transport.Connection.Part;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

/**
 * Singleton class to handle Json parsing using the Gson library. 
 * @author Florian
 */
public class Json {
	/** Gson instance. */
	static private Gson gson_;
	
	/**
	 * Gets the singleton instance.
	 * @return the singleton instance.
	 */
	private static Gson getInstance() {
		if(gson_ == null) {
			GsonBuilder builder = new GsonBuilder();
			builder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			builder.registerTypeAdapter(Part.class, new PartDeserializer());
			gson_ = builder.create();
		}
		
		return gson_;
	}
	
	/**
	 * Creates an object of type T from a Json String.
	 * @param <T> type to return
	 * @param json Json String to parse
	 * @param typeOfT object Type containing the type of T
	 * @return the resulting object
	 * @throws JsonException if the Json string is malformed
	 */
	public static <T> T fromJson(String json, Type typeOfT) throws JsonException {
		try {
			return getInstance().fromJson(json, typeOfT);
		} catch (JsonParseException e) {
			e.printStackTrace();
			throw new JsonException();
		}
	}
}
