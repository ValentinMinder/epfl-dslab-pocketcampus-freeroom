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
	
	private HashMap<AccessPoint,Integer> measureVector_;
	private List<AccessPoint> APList_;
	
	public Measure(List<AccessPoint> _APList){
		this.APList_ = _APList;
		measureVector_ = ApSignalHash();
	}
	
	
	
	public HashMap<AccessPoint, Integer> ApSignalHash(){
		HashMap<AccessPoint,Integer> hash = new HashMap<AccessPoint,Integer>();
				
		for(AccessPoint currentAp :APList_){
			
			hash.put(currentAp , currentAp.getSignalLevel());
		}
	
		return hash;
	}
	
	
	
	public HashMap<AccessPoint,Integer> getMeasure(){
		return  measureVector_;
	}

}
