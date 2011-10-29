package org.pocketcampus.plugin.takeoutreceiver.android.request;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.platform.sdk.shared.restaurant.PendingOrders;
import org.pocketcampus.plugin.takeoutreceiver.android.TakeoutReceiverController;
import org.pocketcampus.plugin.takeoutreceiver.android.TakeoutReceiverModel;
import org.pocketcampus.plugin.takeoutreceiver.shared.TakeoutReceiverService.Iface;

public class LoadPendingOrdersRequest extends Request<TakeoutReceiverController, Iface, Integer, PendingOrders> {
	@Override
	protected PendingOrders runInBackground(Iface client, Integer param) throws Exception {
		return client.getPendingOrders();
	}

	@Override
	protected void onResult(TakeoutReceiverController controller, PendingOrders result) {
		((TakeoutReceiverModel) controller.getModel()).setPendingOrders(result);
		System.out.println("orders updated");
	}

	@Override
	protected void onError(TakeoutReceiverController controller, Exception e) {
		e.printStackTrace();
		controller.getModel().notifyNetworkError();
	}

}
