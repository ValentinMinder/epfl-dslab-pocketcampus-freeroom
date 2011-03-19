/*
 ********************* [ P O C K E T C A M P U S ] *****************
 * [    LICENCE    ]    see "licence"-file in the root directory
 * [   MAINTAINER  ]    andreas.kirchner@epfl.ch
 * [     STATUS    ]    Unstable
 *
 **************************[ C O M M E N T S ]**********************
 *
 * 
 *                      
 *******************************************************************
 */
package org.pocketcampus.plugin.food.menu;


public enum MealTag {

	MEAT {
		public String toString() {        
			return "Meat";    
		}
	},
	
	FISH {
		public String toString() {        
			return "Fish";    
		}
	},
	
	VEGETARIAN {
		public String toString() {        
			return "Vegetarian";    
		}
	}, 
	
	PASTA {
		public String toString() {        
			return "Pasta";    
		}
	},
	
	PORC {
		public String toString() {        
			return "Porc";    
		}
	},
	
	CHICKEN {
		public String toString() {        
			return "Chicken";    
		}
	},
	
	BEEF {
		public String toString() {        
			return "Beef";    
		}
	}
	, HORSE {
		public String toString() {        
			return "Horse";    
		}
	};
}

