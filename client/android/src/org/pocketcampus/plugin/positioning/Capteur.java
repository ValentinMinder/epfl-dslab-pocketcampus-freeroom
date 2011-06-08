package org.pocketcampus.plugin.positioning;

import android.hardware.SensorListener;

public class Capteur implements SensorListener  {

	private float x_;
	private float y_;
	private float z_;
	
	@Override
	public void onAccuracyChanged(int arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(int sensor, float[] values) {
		
		x_ = values[0];
		y_ = values[1];
		z_ = values[2];
		
	}
	
	public float getX(){
		return this.x_;
	}
	
	public float getY(){
		return this.y_;
	}
	
	public float getZ(){
		return this.z_;
	}

}
