package org.pocketcampus.platform.android.core;

/**
 * Listener for network requests, typically to display a spinning icon in the ActionBar.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public abstract class RequestActivityListener {
	public abstract void requestsChanged(int count);
}
