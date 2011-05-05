package org.pocketcampus.shared.plugin.food;

/**
 * Campus restaurant class
 * 
 * @status incomplete
 * @author elodie
 * @license 
 *
 */

import java.io.Serializable;

public class Restaurant implements IRestaurant, Serializable {
	private static final long serialVersionUID = -6383431312307333482L;
	private String name_;
	//private PositionData position;
	
	public Restaurant(){}
	
	public Restaurant(String name/*, PositionData position*/) {
		this.name_ = name;
		//this.position = position;
		valid();
	}
	
	private void valid() {
		//if(position == null) throw new IllegalArgumentException("position is null");
	}

	@Override
	public String getName() {
		return this.name_;
	}

	/*@Override
	public PositionData getPosition() {
		return this.position;
	}*/

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name_ == null) ? 0 : name_.hashCode());
		return result;
	}
	
	public String toString() {
		return name_ + "";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Restaurant other = (Restaurant) obj;
		if (name_ == null) {
			if (other.name_ != null)
				return false;
		} else if (!name_.equals(other.name_))
			return false;
		return true;
	}
	public static double starRatingToDouble(StarRating rating){
		switch(rating){
		case STAR_0_0:
			return 0.0;
		case STAR_0_5:
			return 0.5;
		case STAR_1_0:
			return 1.0;
		case STAR_1_5:
			return 1.5;
		case STAR_2_0:
			return 2.0;
		case STAR_2_5:
			return 2.5;
		case STAR_3_0:
			return 3.0;
		case STAR_3_5:
			return 3.5;
		case STAR_4_0:
			return 4.0;
		case STAR_4_5:
			return 4.5;
		case STAR_5_0: 
			return 5.0;
		default : 
			return 0.0;
		}
	}
	public static StarRating doubleToStarRating(double rating){
		if(rating < 0.25){
			return StarRating.STAR_0_0;
		}else if(rating < 0.75){
			return StarRating.STAR_0_5;
		}else if(rating < 1.25){
			return StarRating.STAR_1_0;
		}else if(rating < 1.75){
			return StarRating.STAR_1_5;
		}else if(rating < 2.25){
			return StarRating.STAR_2_0;
		}else if(rating < 2.75){
			return StarRating.STAR_2_5;
		}else if(rating < 3.25){
			return StarRating.STAR_3_0;
		}else if(rating < 3.75){
			return StarRating.STAR_3_5;
		}else if(rating < 4.25){
			return StarRating.STAR_4_0;
		}else if(rating < 4.75){
			return StarRating.STAR_4_5;
		}else{
			return StarRating.STAR_5_0;
		}
	}
}
