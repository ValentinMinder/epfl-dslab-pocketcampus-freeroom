namespace java org.pocketcampus.platform.sdk.shared.food

include "common.thrift"

///////////
// MENUS //
///////////

// FROM SERVER TO CLIENT
struct MenuItem {
	1: required common.Id id;
	2: required string name;
	3: required double price;
	4: required string pricingUnit;
	5: optional string description;
	
	// time in seconds from when the preparation is started (not from order!)
	6: optional common.Duration preparationTime;
	
	// used eg for the cooking: one of {blue, rare, medium, well done}
	7: optional list<common.SingleChoiceOption> singleChoiceOptions;
	
	// used eg for toppings: any combination of {extra chocolate, extra ice-cream, extra chantilly}
	8: optional list<common.MultiChoiceOption> multiChoiceOptions;
}

struct MenuSubCategory {
	1: required string name;
	2: required list<MenuItem> items;
	3: optional string description;
}

struct MenuCategory {
	1: required string name;
	2: required list<MenuSubCategory> subCategories;
	3: optional string description;
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

// FROM CLIENT TO SERVER
struct ChosenOrder {
	1: required list<ChosenMenuItem> chosenItems;
	2: required double expectedPrice;
	3: required common.Id userSciper;
}

// FROM SERVER TO CLIENT
/**
 * What the user gets back from a ChosenOrder.
 */
struct ChosenOrderReceipt{
	1: required common.Id userId;
	2: required byte nonce;
}

/**
 * Order the restaurant gets from the server,
 * ie represents what a client has ordered
 */
struct Order {
	1: required common.Id id;
	2: required list<ChosenMenuItem> chosenItems;
	3: required common.Timestamp date;
	4: required common.Id userSciper;
}

struct PendingOrders {
	1: list<Order> orders;
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
	5: optional string description;
}

struct AvailableRestaurants {
	1: required list<Restaurant> restaurants;
	2: optional string area;
}
