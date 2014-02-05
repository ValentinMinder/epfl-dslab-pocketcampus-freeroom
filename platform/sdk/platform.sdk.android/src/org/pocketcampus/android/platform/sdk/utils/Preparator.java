package org.pocketcampus.android.platform.sdk.utils;

import java.util.Map;

public interface Preparator<T> {
	public Object content(int res, T item);
	public int[] resources();
	public void finalize(Map<String, Object> map, T item);
}
