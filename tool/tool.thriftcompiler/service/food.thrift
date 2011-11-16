namespace java org.pocketcampus.plugin.food.shared

include "../include/common.thrift"

struct Restaurant {
	1: required common.Id Id;
	2: required string name;
	3: optional common.Location location;
}

enum SubmitStatus {
	ALREADY_VOTED;
	VALID;
	TOOEARLY;
	ERROR;
}

struct Rating {
	1: required double ratingValue;
	2: required i32 nbVotes;
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
	bool hasVoted(1: string deviceId);
	map<i32, Rating> getRatings();
	SubmitStatus setRating(1: Rating rating, 2: Meal meal, 3: string deviceID);
}