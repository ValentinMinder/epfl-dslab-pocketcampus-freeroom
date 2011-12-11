namespace java org.pocketcampus.plugin.takeoutreceiver.shared

include "../include/common.thrift"
include "../include/restaurant.thrift"

service TakeoutOrderService{
	restaurant.Restaurant getRestaurant(1: string restaurantId);
	bool versionMatches(1: common.Id version);
	restaurant.ClientOrderReceipt placeOrder(1: restaurant.OrderPlacedByClient order);
}

service TakeoutGateway{
	restaurant.AvailableRestaurants getRestaurants();
	restaurant.AvailableRestaurants getRestaurantForLocation(1: common.Location location);
}