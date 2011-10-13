package org.pocketcampus.server.plugin.takeoutreceiver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.pocketcampus.platform.sdk.shared.restaurant.CookReceivedOrder;

public class IdManager {
	private static Map<Long, Object> mapFromIdToObject = new HashMap<Long, Object>();
	private static Map<Object, Long> reverseMap = new HashMap<Object, Long>();
	private static long id=1;

	public static long getID(Object object) {
		Long existingId = reverseMap.get(object);
		if (existingId != null) {
			return existingId;
		}
		mapFromIdToObject.put(id, object);
		reverseMap.put(object, id);
		return id++;
	}

	public static Object getObjectForId(long id) {
		return mapFromIdToObject.get(id);
	}
	
	public static List<Object> getAllObjects() {
		List<Object> objects = new ArrayList<Object>();
		Iterator<Entry<Long, Object>> it = mapFromIdToObject.entrySet().iterator();
		
		while (it.hasNext()) {
			Entry<Long, Object> pair = it.next();
			objects.add(pair.getValue());
		}
		
		return objects;
	}
	
	public static void deleteObject(Object object) {
		long id = reverseMap.get(object);
		reverseMap.remove(object);
		mapFromIdToObject.remove(id);
	}
}
