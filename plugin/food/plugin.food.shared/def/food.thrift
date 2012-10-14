namespace java org.pocketcampus.plugin.food.shared

include "../../../../platform/sdk/platform.sdk.shared/def/common.thrift"

struct Restaurant {
	1: required common.Id restaurantId;
	2: required string name;
	3: optional common.Location location;
}

enum SubmitStatus {
	ALREADY_VOTED;
	VALID;
	TOO_EARLY;
	ERROR;
}

struct Rating {
	1: required double ratingValue;
	2: required i32 numberOfVotes;
	3: required double sumOfRatings;
}

struct Meal {
	1: required common.Id mealId;
	2: required string name;
	3: required string mealDescription;
	4: required Restaurant restaurant;
	5: required Rating rating;
	6: optional double price;
}

struct Sandwich {
	1: required common.Id sandwichId;
	2: required Restaurant restaurant;
	3: required string name;
}

service FoodService {
	list<Meal> getMeals();
	list<Restaurant> getRestaurants();
	list<Sandwich> getSandwiches();
	Rating getRating(1: Meal meal);
	bool hasVoted(1: string deviceId);
	map<common.Id, Rating> getRatings();
	SubmitStatus setRating(1: common.Id mealId, 2: double rating, 3: string deviceId);
}