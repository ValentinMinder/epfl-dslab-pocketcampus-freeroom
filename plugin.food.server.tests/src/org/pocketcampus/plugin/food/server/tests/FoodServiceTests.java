package org.pocketcampus.plugin.food.server.tests;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import org.pocketcampus.plugin.food.server.*;
import org.pocketcampus.plugin.food.shared.*;

public class FoodServiceTests {
	private static final RatingDatabase DEFAULT_RATING_DATABASE =
			new RatingDatabase() {
				@Override
				public void insert(List<EpflRestaurant> menu) {
					// TODO Auto-generated method stub

				}

				@Override
				public void vote(long mealId, double rating) {
					// TODO Auto-generated method stub

				}

				@Override
				public void setRatings(List<EpflRestaurant> menu) {
					// TODO Auto-generated method stub

				}
			};

	private static final DeviceDatabase DEFAULT_DEVICE_DATABASE =
			new DeviceDatabase() {

				@Override
				public void insert(String deviceId) {
					// TODO Auto-generated method stub

				}

				@Override
				public boolean hasVotedToday(String deviceId) {
					// TODO Auto-generated method stub
					return false;
				}

			};

	private static final MealList DEFAULT_MEAL_LIST =
			new MealList() {

				@Override
				public MenuResult getMenu(MealTime time, Date date) throws Exception {
					// TODO Auto-generated method stub
					return null;
				}

			};
}