package org.pocketcampus.shared.core.communication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DefaultGson {
	private final static Gson gson_ =
		DefaultGson.getGsonBuilder().create();
	
	
	/**
	 * <p>Retrieves the Gson object that should be used to convert JSON into Java objects
	 * and vice-versa</p>
	 * 
	 * For an overview of the default options, see {@link #getGsonBuilder()}.
	 * @return a properly configured Gson object
	 */
	public static Gson getGson() {
		return gson_;
	}
	
	/**
	 * <p>Returns the default GsonBuilder that should be used to convert JSON into Java objects
	 * and vice-versa.<br />
	 * <i>All transmitted objects should be simple enough to be
	 * (de)serialized with the default Gson obtained with {@link #getGson()}.
	 * However, if needed, this GsonBuilder can be personalized (e.g. register a new Gson
	 * type adapter).</i></p>
	 * 
	 * Default options are :
	 * <ul>
	 * 	<li>HTML escaping : Disabled</li>
	 * 	<li>Special floating point values : Serialized</li>
	 * 	<li>Field without {@code @Expose} annotation : Excluded</li>
	 *  <li>'null' values : Not serialized
	 *  <li>Date format : yyyy-MM-dd HH:mm:ss zZ</li>
	 * </ul>
	 * @return a properly configured GsonBuilder
	 */
	public static GsonBuilder getGsonBuilder() {
		return new GsonBuilder()
			.disableHtmlEscaping()
			.serializeSpecialFloatingPointValues()
			.excludeFieldsWithoutExposeAnnotation()
			.setDateFormat("yyyy-MM-dd HH:mm:ss zZ");
	}
}
