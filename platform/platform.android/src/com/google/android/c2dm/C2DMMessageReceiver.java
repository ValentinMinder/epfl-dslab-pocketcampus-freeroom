package com.google.android.c2dm;

/**
 * Interface for class that will receive pushed messages.
 * 
 * Code provided by Google.
 * 
 * @author Florian <florian.laurent@epfl.ch>
 *
 */
public interface C2DMMessageReceiver {
	public void receivedUpdate(final String newOrder);
	public void receivedRegistrationId(final String registrationId);
}
