package org.pocketcampus.android.platform.sdk.core;

/**
 * Listener for network requests, typically to display a spinning icon in the ActionBar.
 * 
 * @author Florian <florian.laurent@epfl.ch>
 *
 */
public abstract class RequestActivityListener {
	public abstract void requestStarted();
	public abstract void requestStopped();
}
