package org.pocketcampus.platform.sdk.shared.utils;

import java.util.LinkedList;
import java.util.List;

public class ListUtils {

	public static <T> List<List<T>> partitionList(List<T> collection, int batchSize) {
		int i = 0;
		List<List<T>> batches = new LinkedList<List<T>>();
		while (i < collection.size()) {
			int nextInc = Math.min(collection.size() - i, batchSize);
			List<T> batch = collection.subList(i, i + nextInc);
			batches.add(batch);
		}
		return batches;
	}
    
}













