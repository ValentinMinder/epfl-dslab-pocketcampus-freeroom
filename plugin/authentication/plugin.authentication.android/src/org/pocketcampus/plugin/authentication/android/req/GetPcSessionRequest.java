package org.pocketcampus.plugin.authentication.android.req;

import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.authentication.android.AuthenticationController;
import org.pocketcampus.plugin.authentication.shared.AuthSessionRequest;
import org.pocketcampus.plugin.authentication.shared.AuthSessionResponse;
import org.pocketcampus.plugin.authentication.shared.AuthStatusCode;
import org.pocketcampus.plugin.authentication.shared.AuthenticationService.Iface;

/**
 * GetPcSessionRequest
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class GetPcSessionRequest extends Request<AuthenticationController, Iface, AuthSessionRequest, AuthSessionResponse> {

	@Override
	protected AuthSessionResponse runInBackground(Iface client, AuthSessionRequest param) throws Exception {
		return client.getAuthSession(param);
	}

	@Override
	protected void onResult(AuthenticationController controller, AuthSessionResponse result) {
		if(result.getStatusCode() == AuthStatusCode.OK) {
			controller.pcAuthenticationFinished(result.getSessionId());
		} else if(result.getStatusCode() == AuthStatusCode.INVALID_SESSION) {
			controller.notifyInvalidToken();
		} else {
			controller.notifyNetworkError();
		}
	}

	@Override
	protected void onError(AuthenticationController controller, Exception e) {
		e.printStackTrace();
		controller.notifyNetworkError();
	}
	
}
