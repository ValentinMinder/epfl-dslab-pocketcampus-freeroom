namespace java org.pocketcampus.plugin.takeoutreceiver.shared

/**
* Service definition for the preording plugin.
*/

include "../../../../platform/sdk/platform.sdk.shared/def/common.thrift"
include "../../../../platform/sdk/platform.sdk.shared/def/restaurant.thrift"

service TakeoutReceiverService {
	// for the waiters/cooks
	//restaurant.PendingOrders getPendingOrders();
	//bool setOrderStatus(1: common.Id orderId);
	
	common.Id registerCookAndroid(1: string username, 2: string password, 3: common.PushNotificationPhoneId uid);
	restaurant.PendingOrders getPendingOrders();
	bool setOrderReady(1: common.Id orderId);
	restaurant.CookReceivedOrder getOrder(1: common.Id orderId);
}