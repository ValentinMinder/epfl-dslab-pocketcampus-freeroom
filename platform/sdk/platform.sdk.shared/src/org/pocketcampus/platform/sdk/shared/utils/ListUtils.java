package org.pocketcampus.platform.sdk.shared.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ListUtils {

	public static <T> List<List<T>> chunkList(List<T> bigList, int size) {
		List<List<T>> chunks = new LinkedList<List<T>>();
		List<T> chunk = new ArrayList<T>(size);
		for(T t : bigList) {
			chunk.add(t);
			if(chunk.size() == size) {
				chunks.add(chunk);
				chunk = new ArrayList<T>(size);
			}
		}
		if(chunk.size() > 0)
			chunks.add(chunk);
		return chunks;
	}
    
}













