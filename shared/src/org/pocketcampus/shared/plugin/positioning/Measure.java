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

package org.pocketcampus.shared.plugin.positioning;



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
	
	private HashMap<AccessP,Integer> measureVector_;
	private List<AccessP> APList_;
	
	public Measure(List<AccessP> _APList){
		this.APList_ = _APList;
		measureVector_ = ApSignalHash();
	}
	
	
	
	public HashMap<AccessP, Integer> ApSignalHash(){
		HashMap<AccessP,Integer> hash = new HashMap<AccessP,Integer>();
				
		for(AccessP currentAp :APList_){
			
			hash.put(currentAp , currentAp.getSignalLevel());
		}
	
		return hash;
	}
	
	
	
	public HashMap<AccessP,Integer> getMeasure(){
		return  measureVector_;
	}

}
