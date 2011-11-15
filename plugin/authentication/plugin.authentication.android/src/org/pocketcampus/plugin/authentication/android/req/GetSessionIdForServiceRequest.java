package org.pocketcampus.plugin.authentication.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.authentication.android.AuthenticationController;
import org.pocketcampus.plugin.authentication.shared.AuthenticationService.Iface;
import org.pocketcampus.plugin.authentication.shared.SessionId;
import org.pocketcampus.plugin.authentication.shared.TequilaKey;

public class GetSessionIdForServiceRequest extends Request<AuthenticationController, Iface, TequilaKey, SessionId> {
	@Override
	protected SessionId runInBackground(Iface client, TequilaKey param) throws Exception {
		return client.getSessionIdForService(param);
	}

	@Override
	protected void onResult(AuthenticationController controller, SessionId result) {
		if(result != null)
			controller.gotSessionIdForService(result);
		else
			throw new RuntimeException("GetSessionIdForServiceRequest: Got a null SessionId");
	}
	
	@Override
	protected void onError(AuthenticationController controller, Exception e) {
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}
}