package org.pocketcampus.platform.shared.utils;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/** 
 * Map with hard keys and soft values, used for caches. 
 * It doesn't implement Map<K, V> because it's not needed. 
 * 
 * @author Solal Pirelli <solal@pocketcampus.org>
 */
public final class SoftMap<K, V> {
	private final ReferenceQueue<V> _queue;
	private final Map<K, Reference<V>> _map;
	private final Map<Reference<? extends V>, K> _inverseMap;

	private final Thread _cleanerThread;

	public SoftMap() {
		_queue = new ReferenceQueue<V>();
		_map = new HashMap<K, Reference<V>>();
		_inverseMap = new HashMap<Reference<? extends V>, K>();

		_cleanerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Reference<? extends V> valRef = _queue.remove();
						K key = _inverseMap.get(valRef);

						_map.remove(key);
						_inverseMap.remove(valRef);
					} catch (InterruptedException e) {
						// should never happen
					}
				}
			}
		});
		_cleanerThread.start();
	}

	public V get(K key) {
		Reference<V> refVal = _map.get(key);
		return refVal == null ? null : refVal.get();
	}

	public V put(K key, V value) {
		Reference<V> valRef = new SoftReference<V>(value, _queue);
		_map.put(key, valRef);
		_inverseMap.put(valRef, key);
		return null;
	}
}