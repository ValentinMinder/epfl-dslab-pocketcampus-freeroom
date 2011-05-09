package org.pocketcampus.plugin.transport;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.pocketcampus.core.plugin.Core;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class DateSerializer implements JsonSerializer<Date> {

	@Override
	public JsonElement serialize(Date date, Type arg1, JsonSerializationContext arg2) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		
		formatter.setTimeZone(TimeZone.getTimeZone(Core.INSTANCE_TIMEZONE));
		
		return new JsonPrimitive(formatter.format(date));
	}

}
