/*
 ********************* [ P O C K E T C A M P U S ] *****************
 * [   MAINTAINER  ]	tarek.benoudina@epfl.ch
 * [     STATUS    ]    in progress
 *
 **************************[ C O M M E N T S ]**********************
 *
 *
 *
 *
 *******************************************************************
 */

package org.pocketcampus.plugin.positioning;

/**
 * Author : Tarek
 *          Benoudina
 *          
 * GpsLocation,
 * 
 * returns : The GPS position of the cellPhone .
 * 
 * 
 * 
 */
import android.content.Context;
import android.location.LocationManager;

public class GpsLocation extends AbstractPosition{
	public GpsLocation(Context context) {
		super(context, LocationManager.GPS_PROVIDER);
	}
}
