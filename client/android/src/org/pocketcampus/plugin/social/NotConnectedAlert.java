package org.pocketcampus.plugin.social;

import org.pocketcampus.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

public class NotConnectedAlert {
	private final Activity activity_;
	
	public NotConnectedAlert(Activity activity, String message) {
		activity_ = activity;
		
		AlertDialog.Builder builder = new AlertDialog.Builder(activity_);
		builder.setMessage(message)
		       .setCancelable(false)
		       .setPositiveButton(activity.getString(R.string.social_ok), new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   activity_.finish();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
}
