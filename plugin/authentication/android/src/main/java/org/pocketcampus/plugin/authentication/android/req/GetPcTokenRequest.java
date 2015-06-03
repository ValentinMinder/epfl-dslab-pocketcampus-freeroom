package org.pocketcampus.plugin.authentication.android.req;

import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.authentication.android.AuthenticationController;
import org.pocketcampus.plugin.authentication.android.AuthenticationModel;
import org.pocketcampus.plugin.authentication.shared.AuthStatusCode;
import org.pocketcampus.plugin.authentication.shared.AuthTokenResponse;
import org.pocketcampus.plugin.authentication.shared.AuthenticationService.Iface;

/**
 * GetPcTokenRequest
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class GetPcTokenRequest extends Request<AuthenticationController, Iface, Object, AuthTokenResponse> {

	@Override
	protected AuthTokenResponse runInBackground(Iface client, Object param) throws Exception {
		return client.getAuthTequilaToken();
	}

	@Override
	protected void onResult(AuthenticationController controller, AuthTokenResponse result) {
		if(result.getStatusCode() == AuthStatusCode.OK) {
			((AuthenticationModel) controller.getModel()).setTequilaToken(result.getTequilaToken());
			controller.startPreLogin();
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
