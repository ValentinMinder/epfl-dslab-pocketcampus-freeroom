package org.pocketcampus.plugin.pushnotif.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.pushnotif.shared.PushNotifReply;
import org.pocketcampus.plugin.pushnotif.shared.PushNotifRequest;
import org.pocketcampus.plugin.pushnotif.shared.TequilaToken;
import org.pocketcampus.plugin.pushnotif.android.PushNotifController;
import org.pocketcampus.plugin.pushnotif.android.PushNotifModel;
import org.pocketcampus.plugin.pushnotif.shared.PushNotifService.Iface;

/**
 * GetPushNotifSessionRequest
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class UnregisterRequest extends Request<PushNotifController, Iface, PushNotifRequest, PushNotifReply> {

	@Override
	protected PushNotifReply runInBackground(Iface client, PushNotifRequest param) throws Exception {
		return client.unregisterPushNotif(param);
	}

	@Override
	protected void onResult(PushNotifController controller, PushNotifReply result) {
		controller.unregistrationFinished(result.getIStatus() == 200);
	}

	@Override
	protected void onError(PushNotifController controller, Exception e) {
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}
	
}
