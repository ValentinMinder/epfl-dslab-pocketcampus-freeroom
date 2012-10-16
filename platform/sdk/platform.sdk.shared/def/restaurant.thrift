namespace java org.pocketcampus.platform.sdk.shared.restaurant

include "common.thrift"

///////////
// MENUS //
///////////

// FROM SERVER TO CLIENT
struct MenuItem {
	1: required common.Id itemId;
	2: required string name;
	3: required double price;
	4: required string pricingUnit;
	5: optional string itemDescription;
	
	// time in seconds from when the preparation is started (not from order!)
	6: optional common.PreparationTime preparationTime;
	
	// used eg for the cooking: one of {blue, rare, medium, well done}
	7: optional list<common.SingleChoiceOption> singleChoiceOptions;
	
	// used eg for toppings: any combination of {extra chocolate, extra ice-cream, extra chantilly}
	8: optional list<common.MultiChoiceOption> multiChoiceOptions;
	9: required common.Rating stars = Rating.UNKNOWN;
}

struct MenuSubCategory {
	1: required string name;
	2: required list<MenuItem> items;
	3: optional string subCategoryDescription;
	4: required common.Id subCategoryId;
}

struct MenuCategory {
	1: required string name;
	2: required list<MenuSubCategory> subCategories;
	3: optional string categoryDescription;
	4: required common.Id categoryId;
}

// FROM CLIENT TO SERVER
struct ChosenMenuItem {
	1: required common.Id menuItemId;
	2: optional list<common.ChosenSingleChoiceOption> singleChoiceOptions;
	3: optional list<common.ChosenMultiChoiceOption> multiChoiceOptions;
	4: optional string comments;
}


////////////
// ORDERS //
////////////

enum PaymentMethod{
	PAY_BY_CASH = 0;
	PAY_BY_CAMIPRO = 1;
	PAY_BY_PAYPAL=2;
	PAY_BY_CARD=3;
	PAY_BY_INVOICE=4;
}

// FROM CLIENT TO SERVER
struct OrderPlacedByClient {
	1: required list<ChosenMenuItem> chosenItems;
	2: required double expectedPrice;
	3: required common.Id userId;
	4: required PaymentMethod howWillPay;
	5: required bool pickUp;	
	6: optional string address;
	7: optional string phoneNumber;
	8: required common.Timestamp timestamp;
	9: optional common.Id tableId;
	10: required common.PushNotificationPhoneId phoneId;
	11: required common.Id orderId;
	12: required string username;
}

// FROM SERVER TO CLIENT
/**
 * What the user gets back from a ChosenOrder.
 */
struct ClientOrderReceipt{
	1: required common.Id userId;
	2: required byte nonce;
	3: required common.Id orderId;
}

/**
 * Order the restaurant gets from the server,
 * ie represents what a client has ordered
 */
struct Order {
	1: required common.Id id;
	2: required list<MenuItem> chosenItems;
	3: required common.Timestamp date;
	4: required common.Id userId;
}


//////////////////////////
// RECEIVED BY THE COOK //
//////////////////////////

struct CookReceivedItem{
	1: required string name;
	2: required map<string, string> singleChoices;
	3: required map<string, list<string>> multipleChoices;
	4: required string comments;
}

//delivered to waiter's phone from server
struct CookReceivedOrder{
	1: required common.Id orderId;
	2: required list<CookReceivedItem> orderedItems;
	3: required common.Id userId;
	4: required common.Timestamp date;
	5: required double price;
}

struct PendingOrders {
	1: list<CookReceivedOrder> orders;
}


/////////////////
// RESTAURANTS //
/////////////////

// Fanciness of an establishment.
enum RestaurantCategory {
	UNKNOWN;
	PUB;
	FAST_FOOD;
	AFFORDABLE;
	MEDIUM;
	EXPENSIVE;
	HIGHEST;
}

struct Restaurant {
	1: required string name;
	2: required list<MenuCategory> menuCategories;
	3: required common.Currency currency;
	4: required RestaurantCategory category;
	5: optional string restaurantDescription;
	6: required common.integer version;
	7: required common.Id restaurantId;
	8: required common.Rating stars = Rating.UNKNOWN;
	9: optional common.Location location;
	10: required bool payBeforeOrderIsPlaced;
	11: required set<PaymentMethod> acceptedPaymentMethods;
	12: optional string phoneNumber;
	13: optional string address;
}

struct AvailableRestaurants {
	1: required list<common.TakeoutServerAddress> restaurants;
	2: optional string area;
}

