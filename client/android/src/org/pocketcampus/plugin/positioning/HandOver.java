package org.pocketcampus.plugin.positioning;

import org.pocketcampus.shared.plugin.map.Position;

import android.content.Context;

public class HandOver {
	
	private Grid grid_;
	private Position HORposition_;
	private Context ctx_;
	private boolean movement;
	
	public HandOver(Context ctx){
		this.movement = false;
		this.ctx_ = ctx;
		this.grid_ = new Grid(ctx_);
		this.HORposition_ = getHORPosition();
	}
	
	
	
	

	private Position getHORPosition() {
		if(movement==false){
			
			
		}else return onCompute();
		return HORposition_;
	}

	
	private Position onCompute() {
		// TODO Auto-generated method stub
		return null;
	}





	public boolean onMove(){
		Accelerometer accel = new Accelerometer();
		if(accel.onShake()){
			this.movement = true;
			return true;
		}else
		return false; 
	}
	
}
