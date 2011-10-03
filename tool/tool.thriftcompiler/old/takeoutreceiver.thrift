namespace java org.pocketcampus.plugin.takeoutreceiver.shared

/**
* Service definition for the preording plugin.
*/

include "../include/common.thrift"
include "../include/restaurant.thrift"

service TakeoutReceiverService {
	// for the waiters/cooks
	restaurant.PendingOrders getPendingOrders();
	bool setOrderStatus(1: common.Id orderId);
}