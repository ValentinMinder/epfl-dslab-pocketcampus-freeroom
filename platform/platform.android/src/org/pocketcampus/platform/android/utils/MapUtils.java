package org.pocketcampus.platform.android.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.support.v4.util.LongSparseArray;
import android.util.SparseArray;

public class MapUtils {

	public static <K, V> Map<K, V> subMap(Map<K, V> map, Collection<K> subKeys) {
		Map<K, V> newMap = new HashMap<K, V>();
		for(K k : subKeys) {
			if(map.containsKey(k))
				newMap.put(k, map.get(k));
		}
		return newMap;
	}

	public static <V> SparseArray<V> subMap(SparseArray<V> map, Collection<Integer> subKeys) {
		SparseArray<V> newMap = new SparseArray<V>();
		for(int k : subKeys) {
			if(map.indexOfKey(k) >= 0)
				newMap.put(k, map.get(k));
		}
		return newMap;
	}

	public static <V> LongSparseArray<V> subMap(LongSparseArray<V> map, Collection<Long> subKeys) {
		LongSparseArray<V> newMap = new LongSparseArray<V>();
		for(long k : subKeys) {
			if(map.indexOfKey(k) >= 0)
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
