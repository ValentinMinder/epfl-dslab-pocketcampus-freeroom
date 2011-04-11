package org.pocketcampus.utils;

import android.content.Context;
import android.widget.Toast;

public class MyToast {
	public static void showToast(Context ctx, String message) {
		Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
	}
	
	public static void showToast(Context ctx, int id) {
		String msg = ctx.getResources().getString(id);
		showToast(ctx, msg);
	}

}
