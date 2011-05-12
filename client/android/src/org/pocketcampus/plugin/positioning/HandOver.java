package org.pocketcampus.plugin.positioning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.pocketcampus.shared.plugin.map.Position;

import android.content.Context;

public class HandOver {
	
	private Grid grid_;
	private Position HORposition_;
	private Context ctx_;
	private boolean movement;
	private Measure measure_;
	
	public HandOver(Context ctx){
		this.movement = false;
		this.ctx_ = ctx;
		this.grid_ = new Grid(ctx_);
		this.measure_ = new Measure(grid_.getApGrid());
		this.HORposition_ = getHORPosition();
	}
	
	
	
	

	public Position getHORPosition() {
		if(movement==false){
			compareMeasure();
			
		}else return onCompute();
		return HORposition_;
	}

	
	
	public void compareMeasure() {
		List<AccessPoint> newApList = grid_.getApGrid(); 
		Measure secondMeasure = new Measure(newApList);
		Grid newGrid = new Grid(ctx_);
		evaluateNodes(measure_,secondMeasure);
	}





	public void evaluateNodes(Measure measure, Measure secondMeasure) {
		HashMap<AccessPoint,Integer> differenceValue = new HashMap<AccessPoint,Integer>();
		for(AccessPoint ap:measure.getMeasure().keySet()){
			if(secondMeasure.getMeasure().containsKey(ap)){
				int val1 = measure.getMeasure().get(ap);
				int val2 = secondMeasure.getMeasure().get(ap);
				int diff = val1-val2;
				if(diff<0){
					ap.increaseNode();
				}else if (diff>0){
					ap.decreaseNode();
				}
			}	
		}
	}





	public Position onCompute() {
		// TODO Auto-generated method stub
		return null;
	}





	public boolean onMove(){
		Accelerometer accel = new Accelerometer(ctx_);
		if(accel.onShake())
			this.movement = true;
	
	  return movement;
	}
	
}
