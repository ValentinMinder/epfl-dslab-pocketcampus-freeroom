package org.pocketcampus.platform.android.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public  class Preparated<T> {
	List<T> events;
	Preparator<T> prep;
	List<Map<String, ?>> data = null;
	String[] keys = null;
	int[] res = null;
	public Preparated(List<T> events, Preparator<T> prep) {
		this.events = events;
		this.prep = prep;
		compute();
	}
	private void compute() {
		res = prep.resources();
		keys = new String[res.length];
		for(int j = 0; j < res.length; j++)
			keys[j] = "KEY_" + j;
		data = new LinkedList<Map<String,?>>();
		for(int i = 0; i < events.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			T e = events.get(i);
			for(int j = 0; j < res.length; j++)
				map.put(keys[j], prep.content(res[j], e));
			prep.finalize(map, e);
			data.add(map);
		}
	}
	public List<Map<String, ?>> getMap() {
		return data;
	}
	public String[] getKeys() {
		return keys;
	}
	public int[] getResources() {
		return res;
	}
}
