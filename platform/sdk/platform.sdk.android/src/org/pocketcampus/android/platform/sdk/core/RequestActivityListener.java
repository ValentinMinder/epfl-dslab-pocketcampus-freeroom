package org.pocketcampus.android.platform.sdk.core;

/**
 * Listener for network requests, typically to display a spinning icon in the ActionBar.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public abstract class RequestActivityListener {
	public abstract void requestsChanged(int count);
}
