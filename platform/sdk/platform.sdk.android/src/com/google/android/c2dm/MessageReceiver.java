package com.google.android.c2dm;

public interface MessageReceiver {
	public void receivedUpdate(final String newOrder);
	public void receivedRegistrationId(final String registrationId);
}
