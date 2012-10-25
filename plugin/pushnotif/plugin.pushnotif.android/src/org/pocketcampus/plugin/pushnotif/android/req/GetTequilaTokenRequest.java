package org.pocketcampus.plugin.pushnotif.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.platform.sdk.shared.authentication.TequilaToken;
import org.pocketcampus.plugin.pushnotif.android.PushNotifController;
import org.pocketcampus.plugin.pushnotif.shared.PushNotifService.Iface;

/**
 * GetTequilaTokenRequest
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class GetTequilaTokenRequest extends Request<PushNotifController, Iface, Object, TequilaToken> {

	@Override
	protected TequilaToken runInBackground(Iface client, Object param) throws Exception {
		return client.getTequilaTokenForPushNotif();
	}

	@Override
	protected void onResult(PushNotifController controller, TequilaToken result) {
		controller.gotTequilaToken(result);
	}

	@Override
	protected void onError(PushNotifController controller, Exception e) {
		controller.getModel().notifyNetworkError();
		controller.networkError();
		e.printStackTrace();
	}
	
}
