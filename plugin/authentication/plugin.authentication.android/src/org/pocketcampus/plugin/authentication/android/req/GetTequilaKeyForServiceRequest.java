package org.pocketcampus.plugin.authentication.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.authentication.android.AuthenticationController;
import org.pocketcampus.plugin.authentication.android.AuthenticationModel;
import org.pocketcampus.plugin.authentication.shared.AuthenticationService.Iface;
import org.pocketcampus.plugin.authentication.shared.TequilaKey;
import org.pocketcampus.plugin.authentication.shared.TypeOfService;

public class GetTequilaKeyForServiceRequest extends Request<AuthenticationController, Iface, TypeOfService, TequilaKey> {
	@Override
	protected TequilaKey runInBackground(Iface client, TypeOfService param) throws Exception {
		return client.getTequilaKeyForService(param);
	}

	@Override
	protected void onResult(AuthenticationController controller, TequilaKey result) {
		((AuthenticationModel) controller.getModel()).setTequilaKey(result);
		((AuthenticationModel) controller.getModel()).setAuthState(2);
	}
	
	@Override
	protected void onError(AuthenticationController controller, Exception e) {
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}
}