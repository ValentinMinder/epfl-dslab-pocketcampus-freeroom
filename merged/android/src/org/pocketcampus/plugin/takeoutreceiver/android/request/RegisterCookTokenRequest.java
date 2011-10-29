package org.pocketcampus.plugin.takeoutreceiver.android.request;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.takeoutreceiver.android.TakeoutReceiverController;
import org.pocketcampus.plugin.takeoutreceiver.shared.TakeoutReceiverService.Iface;

public class RegisterCookTokenRequest extends Request<TakeoutReceiverController, Iface, String, Integer> {
	@Override
	protected Integer runInBackground(Iface client, String param) throws Exception {
		System.out.println("Sending C2DM token to server.");
		return (int) client.registerCookAndroid("silviu", "foo", param);
	}

	@Override
	protected void onResult(TakeoutReceiverController controller, Integer result) {
		// nothing to do
	}

	@Override
	protected void onError(TakeoutReceiverController controller, Exception e) {
		e.printStackTrace();
		controller.getModel().notifyNetworkError();
	}

}
