package org.pocketcampus.core.communication.packet;

import java.lang.reflect.Type;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class Date {
	private DateTime date;
	
	/**
	 * Creates a new Date set to the current system date.
	 */
	public Date() {
		this(new DateTime());
	}
	
	/**
	 * Creates a new Date set to the given DateTime's date
	 * @param dt
	 */
	private Date(DateTime dt) {
		this.date = dt;
	}
	
	/**
	 * Returns a PCP-compliant representation of this date
	 * @return a date in the "yyyy-MM-dd HH:mm:ss zZ" format
	 */
	public String toString() {
		return this.date.withZone(DateTimeZone.UTC)
				.toString("yyyy-MM-dd HH:mm:ss zZ");
	}
	
	/**
	 * Type adapter used by GSON to serialize and deserialize Protocol objects
	 */
	public static class GsonAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {

		@Override
		public Date deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context)
				throws JsonParseException {
			
			DateTime dt = new DateTime(json.getAsString());
			return new Date(dt);
		}

		@Override
		public JsonElement serialize(Date src, Type typeOfSrc,
				JsonSerializationContext context) {
			
			return new JsonPrimitive(src.toString());
		}
		
	}
}