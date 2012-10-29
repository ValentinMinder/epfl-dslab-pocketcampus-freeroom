package org.pocketcampus.plugin.pushnotif.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.pushnotif.shared.PushNotifReply;
import org.pocketcampus.plugin.pushnotif.shared.PushNotifRegReq;
import org.pocketcampus.plugin.pushnotif.android.PushNotifController;
import org.pocketcampus.plugin.pushnotif.shared.PushNotifService.Iface;

/**
 * GetPushNotifSessionRequest
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class RegisterRequest extends Request<PushNotifController, Iface, PushNotifRegReq, PushNotifReply> {

	@Override
	protected PushNotifReply runInBackground(Iface client, PushNotifRegReq param) throws Exception {
		return client.registerPushNotif(param);
	}

	@Override
	protected void onResult(PushNotifController controller, PushNotifReply result) {
		controller.registrationFinished(result.getIStatus() == 200);
	}

	@Override
	protected void onError(PushNotifController controller, Exception e) {
		controller.getModel().notifyNetworkError();
		controller.networkError();
		e.printStackTrace();
	}
	
}
