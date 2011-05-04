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



/**
 * Author : Tarek
 *          Benoudina
 *          
 * Measure class ,
 * 
 * returns : collection of signal strength for each AP found at given time.
 * 
 */

import java.util.HashMap;
import java.util.List;


public class Measure {
	
	private HashMap<String,Integer> measureVector_;
	private List<AccessPoint> APList_;
	
	public Measure(List<AccessPoint> _APList){
		this.APList_ = _APList;
		measureVector_ = ApSignalHash();
	}
	
	
	
	public HashMap<String,Integer> ApSignalHash(){
		HashMap<String,Integer> hash = new HashMap<String,Integer>();
		
		int size = APList_.size();
		AccessPoint currentAp = null;
		
		for(int i = 0;i<size;i++){
			 
			currentAp = APList_.get(i);
			hash.put(currentAp.getSSID() , currentAp.getSignalLevel());
		}
		
		
		return hash;
	}
	
	
	public HashMap<String,Integer> getMeasure(){
		return  measureVector_;
	}

}
