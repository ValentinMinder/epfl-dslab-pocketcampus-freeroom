namespace java org.pocketcampus.plugin.food.shared

include "../include/common.thrift"

struct Restaurant {
	1: required common.Id Id;
	2: required string name;
	3: optional common.Location location;
}

enum RatingValue {
	STAR_0_0;
	STAR_0_5;
	STAR_1_0;
	STAR_1_5;
	STAR_2_0;
	STAR_2_5;
	STAR_3_0;
	STAR_3_5;
	STAR_4_0;
	STAR_4_5;
	STAR_5_0;
}

enum SubmitStatus {
	ALREADY_VOTED;
	VALID;
	TOOEARLY;
	ERROR;
}

struct Rating {
	1: required RatingValue ratingValue;
	2: required int nbVotes;
	3: required double totalRating;
}

struct Meal {
	1: required common.Id Id;
	2: required string name;
	3: required string mealDescription;
	4: required Restaurant restaurant;
	5: required Rating rating;
	6: optional double price;
}

struct Sandwich {
	1: required common.Id Id;
	2: required Restaurant restaurant;
	3: required string name;
}

service FoodService {
	list<Meal> getMeals();
	list<Restaurant> getRestaurants();
	list<Sandwich> getSandwiches();
	Rating getRating(1: Meal meal);
	map<i32, Rating> getRatings();
	SubmitStatus setRating(1: Rating rating, 2: Meal meal, 3: string deviceID);
}