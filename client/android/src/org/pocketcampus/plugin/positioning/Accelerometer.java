package org.pocketcampus.plugin.positioning;

import android.app.Service;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

public class Accelerometer {
	
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
		// TODO Auto-generated method stub
		return false;
	}

}
