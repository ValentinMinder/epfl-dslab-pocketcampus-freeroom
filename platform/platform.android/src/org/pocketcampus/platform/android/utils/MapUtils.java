package org.pocketcampus.platform.android.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MapUtils {

	public static <K, V> Map<K, V> subMap(Map<K, V> map, Collection<K> subKeys) {
		Map<K, V> newMap = new HashMap<K, V>();
		for(K k : subKeys) {
			if(map.containsKey(k))
				newMap.put(k, map.get(k));
		}
		return newMap;
	}

	public static <K, V> List<V> extractValuesInOrder(Map<K, V> map, List<K> keysList) {
		List<V> vals = new LinkedList<V>();
		for(K key : keysList) {
			vals.add(map.get(key));
		}
		return vals;
	}

}
