package org.pocketcampus.platform.server;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

import org.joda.time.*;
import org.pocketcampus.platform.shared.utils.SoftMap;

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