/*
 ********************* [ P O C K E T C A M P U S ] *****************
 * [    LICENCE    ] 	see "licence"-file in the root directory
 * [   MAINTAINER  ]	tarek.benoudina@epfl.ch
 * [     STATUS    ]    stable
 *
 **************************[ C O M M E N T S ]**********************
 *
 * Represents an Accelerometer.
 *
 *******************************************************************
 */

package org.pocketcampus.plugin.positioning;

/**
 * Author : Tarek
 *          Benoudina
 *          
 * Accelerometer,
 * 
 * returns : x,y,z values , Onshake state .
 * 
 */

import android.app.Service;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorListener;
import android.hardware.SensorManager;

public class Accelerometer implements SensorListener {
	
	private SensorManager sensorManager_;
	private final Sensor accelerometer_;
	private Context context_;
	private double xValue;
	private double yValue;
	private double zValue;
	
	
	public Accelerometer(Context context){
		context_ = context;
		sensorManager_ = (SensorManager)context.getSystemService(context_.SENSOR_SERVICE);
        accelerometer_ = sensorManager_.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        

	}

	public boolean onShake() {
		
		
		
		return false;
	}

	@Override
	public void onAccuracyChanged(int arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(int arg0, float[] values) {
		
        xValue = values[0];

        yValue = values[1];

        zValue = values[2];		
	}

}
