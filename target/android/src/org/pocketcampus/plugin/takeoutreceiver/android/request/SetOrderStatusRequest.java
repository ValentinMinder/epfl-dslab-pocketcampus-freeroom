package org.pocketcampus.plugin.takeoutreceiver.android.request;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.takeoutreceiver.android.TakeoutReceiverController;
import org.pocketcampus.plugin.takeoutreceiver.shared.TakeoutReceiverService.Iface;

public class SetOrderStatusRequest extends Request<TakeoutReceiverController, Iface, Long, Boolean> {
	@Override
	protected Boolean runInBackground(Iface client, Long param) throws Exception {
		return client.setOrderReady(param);
	}

	@Override
	protected void onResult(TakeoutReceiverController controller, Boolean result) {
		controller.loadPendingOrders();
	}

	@Override
	protected void onError(TakeoutReceiverController controller, Exception e) {
		e.printStackTrace();
		controller.getModel().notifyNetworkError();
	}
}
