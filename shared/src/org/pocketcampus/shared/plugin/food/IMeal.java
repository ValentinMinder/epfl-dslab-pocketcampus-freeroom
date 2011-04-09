/*
 ********************* [ P O C K E T C A M P U S ] *****************
 * [    LICENCE    ]     see "licence"-file in the root directory
 * [   MAINTAINER  ]    claude.bossy@epfl.ch
 * [     STATUS    ]    stable
 *
 **************************[ C O M M E N T S ]**********************
 *
 * maybe we should store the collection of pictures separately and provide an addtional method at the server like:
 * Collection<IPicture> getPicturesOf(IMeal meal);
 *                      
 *******************************************************************
 */ 

package org.pocketcampus.shared.plugin.food;


public interface IMeal {
	
	IRestaurant getRestaurant();
	boolean isAvailable();
	String getDescription();
}
