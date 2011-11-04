package org.pocketcampus.server.plugin.takeoutreceiver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javapns.Push;
import javapns.notification.PushNotificationPayload;

import org.apache.thrift.TException;
import org.pocketcampus.platform.sdk.shared.common.Choice;
import org.pocketcampus.platform.sdk.shared.common.ChosenMultiChoiceOption;
import org.pocketcampus.platform.sdk.shared.common.ChosenSingleChoiceOption;
import org.pocketcampus.platform.sdk.shared.common.MultiChoiceOption;
import org.pocketcampus.platform.sdk.shared.common.SingleChoiceOption;
import org.pocketcampus.platform.sdk.shared.restaurant.ChosenMenuItem;
import org.pocketcampus.platform.sdk.shared.restaurant.CookReceivedItem;
import org.pocketcampus.platform.sdk.shared.restaurant.CookReceivedOrder;
import org.pocketcampus.platform.sdk.shared.restaurant.MenuItem;
import org.pocketcampus.platform.sdk.shared.restaurant.OrderPlacedByClient;
import org.pocketcampus.platform.sdk.shared.restaurant.PendingOrders;
import org.pocketcampus.plugin.takeoutreceiver.shared.TakeoutReceiverService;

public class TakeoutReceiverServiceImpl implements TakeoutReceiverService.Iface {

	@Override
	public PendingOrders getPendingOrders() throws TException {
		System.out.println("---getPendingOrders---");
		List<CookReceivedOrder> orders = new ArrayList<CookReceivedOrder>();

		for (Object obj : IdManager.getAllObjects()) {
			if (obj instanceof OrderPlacedByClient) {
				System.out.println((OrderPlacedByClient) obj);

				long id = IdManager.getID((OrderPlacedByClient) obj);
				orders.add(getOrder(id));
			}
		}

		System.out.println("---end---");
		return new PendingOrders(orders);
	}

	@Override
	public long registerCookAndroid(String username, String password, String uid)
			throws TException {
		System.out.println("Registering cook...");
		Cook.createCook(uid, username);
		return 0;
	}

	@Override
	public boolean setOrderReady(final long orderId) throws TException {
		System.out.println("setOrderStatus: " + orderId);

		IdManager.deleteObject(IdManager.getObjectForId(orderId));

		// push to iphone
		final String orderID = "" + orderId;
		final String token = DeviceTokenManager
				.getDeviceTokenForOrderID(orderId);
		new Thread() {
			public void run() {
				PushNotificationPayload paylod = new PushNotificationPayload();
				   try {
				    paylod.addCustomDictionary("orderID", "" + orderId);
				    paylod.addAlert("Your order #" + orderId
				      + " is ready. Come pick it up!");
				    System.out.println(Push.payload(paylod,
				      "./certificate/APNCertificate.p12", "mywaiter",
				      false, token));
				   } catch (Exception e) {
				    e.printStackTrace();
				   }
			}
		}.start();
		
		return true;
	}

	@Override
	public CookReceivedOrder getOrder(long orderId) throws TException {
		OrderPlacedByClient order = (OrderPlacedByClient) IdManager
				.getObjectForId(orderId);
		CookReceivedOrder cookOrder = new CookReceivedOrder();
		cookOrder.setOrderId(order.getOrderId());
		cookOrder.setUserId(order.getUserId());
		cookOrder.setDate(order.getTimestamp());
		cookOrder.setPrice(order.getExpectedPrice());
		Vector<CookReceivedItem> vector = new Vector<CookReceivedItem>();
		for (ChosenMenuItem menuItem : order.getChosenItems()) {
			MenuItem item = (MenuItem) IdManager.getObjectForId(menuItem
					.getMenuItemId());
			CookReceivedItem cookItem = new CookReceivedItem();
			cookItem.setComments(menuItem.getComments());
			cookItem.setName(item.getName());

			Map<String, String> singleChoice = new HashMap<String, String>();
			for (ChosenSingleChoiceOption single : menuItem
					.getSingleChoiceOptions()) {
				SingleChoiceOption option = (SingleChoiceOption) IdManager
						.getObjectForId(single.getSingleChoiceId());
				Choice choice = (Choice) IdManager.getObjectForId(single
						.getChosenId());
				singleChoice.put(option.getName(), choice.getChoiceValue());
			}
			cookItem.setSingleChoices(singleChoice);

			Map<String, List<String>> multipleChoices = new HashMap<String, List<String>>();
			for (ChosenMultiChoiceOption multiple : menuItem
					.getMultiChoiceOptions()) {
				MultiChoiceOption option = (MultiChoiceOption) IdManager
						.getObjectForId(multiple.getMultiChoiceId());
				System.out.println(option);

				Vector<String> options = new Vector<String>();
				multipleChoices.put(option.getName(), options);
				for (long id : multiple.getChosenIds()) {
					Choice choice = (Choice) IdManager.getObjectForId(id);
					options.add(choice.getChoiceValue());
				}
			}
			cookItem.setMultipleChoices(multipleChoices);
			vector.add(cookItem);
		}

		cookOrder.setOrderedItems(vector);
		return cookOrder;
	}

}
