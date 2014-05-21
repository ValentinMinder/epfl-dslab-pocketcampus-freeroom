package org.pocketcampus.plugin.authentication.android.req;

import org.pocketcampus.android.platform.sdk.core.GlobalContext;
import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.authentication.android.AuthenticationController;
import org.pocketcampus.plugin.authentication.android.AuthenticationModel;
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
			GlobalContext globCntxt = ((GlobalContext) controller.getApplicationContext());
			AuthenticationModel authMod = ((AuthenticationModel) controller.getModel());
			globCntxt.setPcSessionId(result.getSessionId(), authMod.getStorePassword());
			controller.pcAuthenticationFinished();
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
