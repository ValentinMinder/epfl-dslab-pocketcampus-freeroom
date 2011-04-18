package org.pocketcampus.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Util class to show a toast 
 * 
 * @status Complete
 * 
 * @author Jonas
 *
 */
public class Notification {
	
	/**
	 * Show a toast with short duration
	 * 
	 * @param ctx The context that is used to show the toast
	 * @param message The message
	 */
	public static void showToast(Context ctx, String message) {
		Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Show a toast with short duration
	 * 
	 * @param ctx The context that is used to show the toast
	 * @param message Ressource ID of the message
	 */
	public static void showToast(Context ctx, int id) {
		String msg = ctx.getResources().getString(id);
		showToast(ctx, msg);
	}

}
