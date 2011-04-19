package org.pocketcampus.plugin.logging;

import org.pocketcampus.core.plugin.Core;

import android.content.Context;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class Tracker {
	private static Tracker tracker_ = new Tracker();
	private GoogleAnalyticsTracker googleTracker_ = null;
	
	private final String DEVELOPMENT_GA_PROFILE = "UA-22135241-2";
	private final String RELEASE_GA_PROFILE = "UA-22135241-3";
	private final int DISPATCH_PERIOD = 10;
	
	private Tracker() {
		googleTracker_ = GoogleAnalyticsTracker.getInstance();
	}
	
	public static Tracker getInstance() {
		return tracker_;
	}
	
	public void start(Context context) {
		try {
			if(Core.getApplicationMode() == Core.ReleaseMode.DEVELOPMENT) {
				googleTracker_.start(DEVELOPMENT_GA_PROFILE, DISPATCH_PERIOD, context);
			} else {
				googleTracker_.start(RELEASE_GA_PROFILE, DISPATCH_PERIOD, context);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void trackPageView(String arg0) {
		try{
			googleTracker_.trackPageView(arg0);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void trackEvent(String arg0, String arg1, String arg2, int arg3) {
		try {
			googleTracker_.trackEvent(arg0, arg1, arg2, arg3);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setCustomVar(int arg0, String arg1, String arg2) {
		try {
			googleTracker_.setCustomVar(arg0, arg1, arg2);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setCustomVar(int arg0, String arg1, String arg2, int arg3) {
		try {
			googleTracker_.setCustomVar(arg0, arg1, arg2, arg3);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void stop() {
		try {
			googleTracker_.stop();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
