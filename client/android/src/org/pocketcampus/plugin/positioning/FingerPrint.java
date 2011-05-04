/*
 ********************* [ P O C K E T C A M P U S ] *****************
 * [    LICENCE    ] 	see "licence"-file in the root directory
 * [   MAINTAINER  ]	tarek.benoudina@epfl.ch
 * [     STATUS    ]    under construction
 *
 **************************[ C O M M E N T S ]**********************
 *
 *
 *
 *******************************************************************
 */

package org.pocketcampus.plugin.positioning;

import java.util.Date;

import org.pocketcampus.shared.plugin.map.Position;


/**
 * Author : Tarek
 *          Benoudina
 *          
 * FingerPrint for wifi location, without AccessPoint reference
 * 
 * returns : asociated position with collection of WIFI signals.
 * 
 */
public class FingerPrint {
	
	private Measure measure_;
	private Position position_;
	private long time;
	
	
	public FingerPrint(Measure _measure,Position _position){
		this.measure_ = _measure;
		this.position_ = _position;
		this.time = getTime();
	}
	
	
	
	public long getTime(){
		Date date = new Date();
		return date.getTime();
	}
	
	
	public Measure getMeasure(){
		return measure_;
	}
	
	public Position getPosition(){
		return position_;
	}
	
	public void setMeasure(Measure _measure){
		this.measure_ = _measure;
	}
	
	public void setPosition(Position _position){
		this.position_ = _position;
	}

}

