package com.google.android.c2dm;

public interface C2DMMessageReceiver {
	public void receivedUpdate(final String newOrder);
	public void receivedRegistrationId(final String registrationId);
}
