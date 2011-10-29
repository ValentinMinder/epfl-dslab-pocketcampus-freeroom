package org.pocketcampus.plugin.takeoutreceiver.android;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.thrift.TException;
import org.pocketcampus.C2DMReceiver;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.platform.sdk.shared.restaurant.ChosenMenuItem;
import org.pocketcampus.platform.sdk.shared.restaurant.OrderPlacedByClient;
import org.pocketcampus.platform.sdk.shared.restaurant.PaymentMethod;
import org.pocketcampus.R;
import org.pocketcampus.plugin.takeoutreceiver.android.request.LoadPendingOrdersRequest;
import org.pocketcampus.plugin.takeoutreceiver.android.request.RegisterCookTokenRequest;
import org.pocketcampus.plugin.takeoutreceiver.android.request.SetOrderStatusRequest;
import org.pocketcampus.plugin.takeoutreceiver.shared.TakeoutOrderService;
import org.pocketcampus.plugin.takeoutreceiver.shared.TakeoutOrderService.Iface;
import org.pocketcampus.plugin.takeoutreceiver.shared.TakeoutReceiverService;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.google.android.c2dm.C2DMessaging;
import com.google.android.c2dm.C2DMMessageReceiver;

public class TakeoutReceiverController extends PluginController implements C2DMMessageReceiver {
	private TakeoutReceiverModel mModel;
	private TakeoutReceiverService.Iface mClient;
	private String mPluginName = "takeoutreceiver";
	private NotificationManager mNotificationManager;

	@Override
	public void onCreate() {
		mModel = new TakeoutReceiverModel();
		mClient = (TakeoutReceiverService.Iface) getClient(new TakeoutReceiverService.Client.Factory(), mPluginName);

		System.out.println("C2DM registration...");
		C2DMessaging.register(this, C2DMReceiver.SENDER_ID);
		
		String ns = Context.NOTIFICATION_SERVICE;
		mNotificationManager = (NotificationManager) getSystemService(ns);
	}

	public void loadPendingOrders() {
		System.out.println("loadPendingOrders");
		TakeoutReceiverService.Iface client = (TakeoutReceiverService.Iface) getClient(new TakeoutReceiverService.Client.Factory(), mPluginName);
		new LoadPendingOrdersRequest().start(this, client, (Integer)null);
	}

	protected void setOrderStatus(long orderStatus) {
		new SetOrderStatusRequest().start(this, mClient, orderStatus);
	}

	@Override
	public void receivedUpdate(String newOrder) {
		System.out.println("Update!");
		fireNotification();
		loadPendingOrders();
	}

	public void fireNotification() {
		int icon = R.drawable.icon;
		CharSequence tickerText = "Nouvelle commande!";
		long when = System.currentTimeMillis();

		Context context = getApplicationContext();
		CharSequence contentTitle = "Nouvelle commande";
		CharSequence contentText = "Une nouvelle commande a �t� d�pos�e.";
		Intent notificationIntent = new Intent(this, TakeoutReceiverMainView.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		Notification notification = new Notification(icon, tickerText, when);
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		notification.defaults = Notification.DEFAULT_ALL;
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		mNotificationManager.notify(1, notification);
	}

	@Override
	public void receivedRegistrationId(final String cookToken) {
		System.out.println("Received C2DM token, sending to server.");

		TakeoutReceiverService.Iface client = (TakeoutReceiverService.Iface) getClient(new TakeoutReceiverService.Client.Factory(), mPluginName);
		new RegisterCookTokenRequest().start(TakeoutReceiverController.this, client, cookToken);
	}


	// XXX just for test!
	void addBogusOrder() {
		Iface client = (TakeoutOrderService.Iface) getClient(new TakeoutOrderService.Client.Factory(), "takeout-order");

		List<ChosenMenuItem> chosenItems = new ArrayList<ChosenMenuItem>();
		double expectedPrice = 32.0;
		long userId = 1;
		PaymentMethod howWillPay = PaymentMethod.PAY_BY_CASH;
		boolean pickUp = true;
		long timestamp = new Date().getTime();
		String phoneId = "079-694-2356";
		long orderId = 0;
		OrderPlacedByClient order = new OrderPlacedByClient(chosenItems, expectedPrice, userId, howWillPay, pickUp, timestamp, phoneId, orderId, "flaurent");

		try {
			client.placeOrder(order);
		} catch (TException e) {
			e.printStackTrace();
		}
	}

	@Override
	public PluginModel getModel() {
		return mModel;
	}
}
