package org.pocketcampus.android.platform.sdk.core;

import android.util.Log;

public class Logger {
	private String mLabel;
	
	public Logger() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Logs a message of low importance.
	 * @param msg message to log
	 */
	protected void log(String msg) {
		if(Config.LOG_LEVEL <= Log.VERBOSE) {
			Log.v(mLabel, msg);
		}
	}

	/**
	 * Logs a warning message.
	 * @param msg message to log
	 */
	protected void warn(String msg) {
		if(Config.LOG_LEVEL <= Log.WARN) {
			Log.w(mLabel, msg);
		}
	}

	/**
	 * Logs an error message.
	 * @param msg message to log
	 */
	protected void error(String msg) {
		if(Config.LOG_LEVEL <= Log.ERROR) {
			Log.e(mLabel, msg);
		}
	}
}
