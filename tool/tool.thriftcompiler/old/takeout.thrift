namespace java org.pocketcampus.plugin.takeout.shared

/**
* Service definition for the preording plugin.
*/

include "../include/common.thrift"
include "../include/restaurant.thrift"

service TakeoutService {
	// for the restaurant clients
	restaurant.AvailableRestaurants getAvailableRestaurants();
	restaurant.ChosenOrderReceipt placeOrder(1: restaurant.ChosenOrder order);
}