package org.pocketcampus.plugin.scanner;

import android.location.Location;

public abstract class GpsScannerLocationChangedCallback {
	public abstract void call(Location location);
}
