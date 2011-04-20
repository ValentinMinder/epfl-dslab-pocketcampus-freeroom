package org.pocketcampus.core.communication;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import android.util.Log;

// TODO way to save the cache when closing the application
// TODO way to purge the cache to save memory

/**
 * Manages the cache for the <code>Request</code>s.
 * @author Florian
 * @status working, incomplete
 * @param <A>
 */
public class CacheManager<A> {
	private static final String TAG = "CacheManager";
	private static CacheManager instance_;
	HashMap<String, Date> expirationMap_;
	HashMap<String, A> cacheMap_;
	
	/**
	 * Gets the single instance.
	 * @return
	 */
	public static CacheManager<?> getInstance() {
		if(instance_ == null) {
			instance_ = new CacheManager();
		}
		
		return instance_;
	}
	
	/**
	 * Private constructor.
	 */
	private CacheManager() {
		expirationMap_ = new HashMap<String, Date>();
		cacheMap_ = new HashMap<String, A>();
	}
	
	/**
	 * Gets a resources from the cache, returns null if not found or expired.
	 * @param url Url of the resource
	 * @return
	 */
	public A getFromCache(String url) {
		
		if(!expirationMap_.containsKey(url)) {
			Log.d(TAG, "Not in cache:" + url);
			return null;
		}
		
		float delta = new Date().getTime() - expirationMap_.get(url).getTime();
		
		if(delta > 0) {
			Log.d(TAG, "Expired for " + delta/1000 + "s: " + url);
			return null;
		}
		
		Log.d(TAG, "Retrieved from cache, " + delta*(-1.0/1000.0) + "s left: " + url);
		return cacheMap_.get(url);
	}

	/**
	 * Adds an object to the cache.
	 * @param url URL of the object
	 * @param result Object to store
	 * @param secondsToExpiration Time to keep this object in cache
	 */
	public void putInCache(String url, Object result, int secondsToExpiration) {
		Date expiration = new Date();
		expiration.setTime(new Date().getTime() + secondsToExpiration*1000);
		expirationMap_.put(url, expiration);
		
		cacheMap_.put(url, (A) result);
		
		Log.d(TAG, "Added to cache: " + url);
	}

}

















