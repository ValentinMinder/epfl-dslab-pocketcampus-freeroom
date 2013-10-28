package org.pocketcampus.plugin.food.server;

import java.util.List;

import org.pocketcampus.plugin.food.shared.*;

public interface RatingDatabase {
	void insert(List<EpflRestaurant> menu);
	void vote(long mealId, double rating);
	void setRatings(List<EpflRestaurant> menu);
}
