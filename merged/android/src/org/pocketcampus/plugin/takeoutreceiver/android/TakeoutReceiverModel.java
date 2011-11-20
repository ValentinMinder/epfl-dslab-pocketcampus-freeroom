package org.pocketcampus.plugin.takeoutreceiver.android;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.platform.sdk.shared.restaurant.PendingOrders;
import org.pocketcampus.plugin.takeoutreceiver.android.iface.ITakeoutReceiverMainView;

public class TakeoutReceiverModel extends PluginModel {
	private ITakeoutReceiverMainView mListeners;
	
	private PendingOrders mPendingOrders;
	
	public TakeoutReceiverModel() {
		mListeners = (ITakeoutReceiverMainView) getListeners();
		
		mPendingOrders = new PendingOrders();
	}
	
	public PendingOrders getPendingOrders() {
		return mPendingOrders;
	}

	public void setPendingOrders(PendingOrders pendingOrders) {
		mPendingOrders = pendingOrders;
		
		System.out.println(getNbListeners() + " listeners");
		
		mListeners.ordersUpdated();
	}

	@Override
	protected Class<? extends IView> getViewInterface() {
		return ITakeoutReceiverMainView.class;
	}
	
}
