package org.pocketcampus.platform.server;

import java.lang.ref.*;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

import org.joda.time.*;

/**
 * Creates proxies that (softly) cache method results from interfaces for a specified amount of time.
 * 
 * @author Solal Pirelli <solal@pocketcampus.org>
 */
public final class CachingProxy {
	@SuppressWarnings("unchecked")
	public static <T> T create(final T instance, final CacheValidator validator) {
		Class<T> iface = (Class<T>) getInterface(instance);
		final Map<Method, SoftMap<Integer, GeneratedValue>> cache = new HashMap<Method, SoftMap<Integer, GeneratedValue>>();
		return (T) Proxy.newProxyInstance(iface.getClassLoader(), new Class[] { iface },
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						if (!cache.containsKey(method)) {
							cache.put(method, new SoftMap<Integer, GeneratedValue>());
						}

						int hash = Arrays.deepHashCode(args);
						if (cache.get(method).containsKey(hash)) {
							GeneratedValue cached = cache.get(method).get(hash);
							if (validator.isValid(cached.generationDate)) {
								return cached.value;
							}
						}

						Object result = method.invoke(instance, args);
						cache.get(method).put(hash, new GeneratedValue(DateTime.now(), result));
						return result;
					}
				});
	}

	public static <T> T create(final T instance, final Duration cacheDuration, final boolean forceSameDay) {
		return create(instance, new CacheValidator() {
			@Override
			public boolean isValid(DateTime lastGenerationDate) {
				return (!forceSameDay || DateTime.now().dayOfYear().equals(lastGenerationDate.dayOfYear()))
						&& new Duration(lastGenerationDate, null).isShorterThan(cacheDuration);
			}
		});
	}

	private static Class<?> getInterface(Object obj) {
		return obj.getClass().getInterfaces()[0];
	}

	public static interface CacheValidator {
		boolean isValid(DateTime lastGenerationDate);
	}

	/** A map with hard keys and soft values. It doesn't implement Map<K, V> because it's not needed. */
	private static final class SoftMap<K, V> {
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

		public boolean containsKey(K key) {
			return _map.containsKey(key);
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

	/** A simple value wrapper with a time stamp. */
	private static final class GeneratedValue {
		public final DateTime generationDate;
		public final Object value;

		public GeneratedValue(DateTime generationDate, Object value) {
			this.generationDate = generationDate;
			this.value = value;
		}
	}
}