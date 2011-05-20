package org.pocketcampus.plugin.positioning;

import android.content.Context;
import android.location.LocationManager;

/**
 * Provides the GSM and Wifi location (provided by Google)
 * @author Johan
 *
 */
public class GsmNetworkPosition extends AbstractPosition {
	public GsmNetworkPosition(Context context) {
		super(context, LocationManager.NETWORK_PROVIDER);
	}
}
