package org.pocketcampus.android.platform.sdk.tracker;

import android.content.Context;
import org.pocketcampus.android.platform.sdk.core.Config;

//import com.google.android.apps.analytics.GoogleAnalyticsTracker;

/**
 * 
 * @author Oriane
 * 
 */
public class Tracker {
	private static Tracker tracker_ = new Tracker();
//	private GoogleAnalyticsTracker googleTracker_ = null;

	private final String DEVELOPMENT_GA_PROFILE = "UA-22135241-2";
	private final String RELEASE_GA_PROFILE = "UA-22135241-3";
	private final int DISPATCH_PERIOD = 10;

	/**
	 * 
	 */
	private Tracker() {
//		googleTracker_ = GoogleAnalyticsTracker.getInstance();
	}

	/**
	 * 
	 * @return
	 */
	public static Tracker getInstance() {
		return tracker_;
	}

	/**
	 * 
	 * @param context
	 */
	public void start(Context context) {
		try {
			if (Config.DEBUG) {
//				googleTracker_.start(DEVELOPMENT_GA_PROFILE, DISPATCH_PERIOD,
//						context);
			} else {
//				googleTracker_.start(RELEASE_GA_PROFILE, DISPATCH_PERIOD,
//						context);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param arg0
	 */
	public void trackPageView(String arg0) {
		try {
//			googleTracker_.trackPageView(Config.VERSION + "/" + arg0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public void trackEvent(String arg0, String arg1, String arg2, int arg3) {
		try {
//			googleTracker_.trackEvent(arg0, arg1, arg2, arg3);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public void setCustomVar(int arg0, String arg1, String arg2) {
		try {
//			googleTracker_.setCustomVar(arg0, arg1, arg2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public void setCustomVar(int arg0, String arg1, String arg2, int arg3) {
		try {
//			googleTracker_.setCustomVar(arg0, arg1, arg2, arg3);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	public void stop() {
		try {
//			googleTracker_.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
