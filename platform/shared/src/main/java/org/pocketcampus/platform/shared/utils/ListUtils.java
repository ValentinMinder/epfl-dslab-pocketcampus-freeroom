package org.pocketcampus.platform.shared.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * List utilities.
 * 
 * @author Solal Pirelli <solal@pocketcampus.org>
 */
public class ListUtils {
	/** Splits a large list into a list of chunks of the specified size. */
	public static <T> List<List<T>> chunkList(final List<T> list, final int size) {
		List<List<T>> parts = new ArrayList<List<T>>();
		final int totalSize = list.size();
		
		for (int i = 0; i < totalSize; i += size) {
			// create a new list to force a deep copy
			parts.add(new ArrayList<T>(list.subList(i, Math.min(totalSize, i + size))));
		}
		return parts;
	}
}