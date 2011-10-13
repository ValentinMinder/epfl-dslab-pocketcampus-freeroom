//package org.pocketcampus.server.plugin.takeoutreceiver;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.pocketcampus.platform.sdk.shared.restaurant.CookReceivedItem;
//import org.pocketcampus.platform.sdk.shared.restaurant.CookReceivedOrder;
//
//public class Database {
//	static List<CookReceivedOrder> mOrders = new ArrayList<CookReceivedOrder>();
//
//	public static void addOrder(CookReceivedOrder order) {
//		mOrders.add(order);
//	}
//	
//	public static List<CookReceivedOrder> retrieveOrders() {
//		// OrderPlacedByClient(chosenItems:[ChosenMenuItem(menuItemId:1, singleChoiceOptions:[], multiChoiceOptions:[], comments:)], expectedPrice:3.8, userId:0, howWillPay:PAY_BY_CASH, pickUp:false, address:inn 329, phoneNumber:, timestamp:1317826023, tableId:0, phoneId:<7697ec40 8076a102 9d7c2215 3b1651f1 77b4dda9 0828c275 5b745402 04d278d2>, orderId:16)
//		long orderId = 0;
//		List<CookReceivedItem> orderedItems = new ArrayList<CookReceivedItem>();
//		long userId = 174847;
//		long date = new Date().getTime();
//		
//		String name = "Spaghetti Bolognaise";
//		Map<String, String> singleChoices = new HashMap<String, String>();
//		Map<String, List<String>> multipleChoices = new HashMap<String, List<String>>();
//		String comments = "no comment";
//		orderedItems.add(new CookReceivedItem(name, singleChoices, multipleChoices, comments));
//		
//		mOrders.add(new CookReceivedOrder(orderId, orderedItems, userId, date));
//		return mOrders;
//	}
//
//	// XXX using the timestamp as an ID for now
//	public static void deleteOrder(long orderId) {
//		for(CookReceivedOrder item : mOrders) {
//			if(item.orderId == orderId) {
//				mOrders.remove(item);
//				return;
//			}
//		}
//		
//		System.out.println("Couldn't find order!");
//	}
//}
