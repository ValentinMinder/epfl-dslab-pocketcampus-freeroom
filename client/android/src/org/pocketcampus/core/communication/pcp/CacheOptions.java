package org.pocketcampus.core.communication.pcp;

public class CacheOptions {
	/**
	 * The key that uniquely identifies the cached request. It has a plugin-wide scope (i.e.
	 * two plugins using the same key won't clash each other's cached request).
	 */
	public final String key;
	
	/**
	 * Indicates the amount of time (in seconds) a cached request remains valid in the cache.
	 */
	public final int expirationTimeout;
	
	/**
	 * Forces the associated cached request to be removed from the cache even if it is still
	 * valid. Acts as if the cached object expired.
	 */
	public final boolean forceClear;
	
	/**
	 * 
	 * @param key the key for the stored object in the cache in a plugin-wide scope.
	 * @param expirationTimeout in seconds, the amount of time that the cached object will
	 * remain valid.
	 * @param forceClear clears the cache for the given key, even if it is still valid. Acts
	 * as if the cached object expired.
	 */
	public CacheOptions(String key, int expirationTimeout, boolean forceClear) {
		this.key = key;
		this.expirationTimeout = expirationTimeout;
		this.forceClear = forceClear;
	}
}
