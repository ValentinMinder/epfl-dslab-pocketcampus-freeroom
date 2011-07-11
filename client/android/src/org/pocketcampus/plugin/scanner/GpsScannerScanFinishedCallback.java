package org.pocketcampus.plugin.scanner;

import java.util.ArrayList;

import android.location.Location;

public abstract class GpsScannerScanFinishedCallback {
	public abstract void call(ArrayList<Location> fixes_);
}
