package org.pocketcampus;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.c2dm.C2DMBaseReceiver;
import com.google.android.c2dm.C2DMessaging;

/**
 * Base class to receive push notifications.
 * 
 * @author Florian <florian.laurent@epfl.ch>
 *
 */
public class C2DMReceiver extends C2DMBaseReceiver {

	public static final String SENDER_ID = "silviu.andrica@gmail.com";

	public C2DMReceiver() {
		super(SENDER_ID);
	}

	@Override
	public void onRegistered(Context context, String registration) {
		System.out.println("REGISTERED: " + registration);
		C2DMessaging.app.receivedRegistrationId(registration);
	}

	@Override
	public void onUnregistered(Context context) {
		System.out.println("onUnregistered");
	}

	@Override
	public void onError(Context context, String errorId) {
		System.out.println("onError");
	}

	@Override
	public void onMessage(Context context, Intent intent) {
		final Bundle extras = intent.getExtras();
		
		if (extras != null) {
			C2DMessaging.app.receivedUpdate(extras.getString("payload"));
		}
	}

}
