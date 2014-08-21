package org.pocketcampus.plugin.authentication.android.req;

import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.authentication.android.AuthenticationController;
import org.pocketcampus.plugin.authentication.android.iface.IAuthenticationView;
import org.pocketcampus.plugin.authentication.shared.AuthStatusCode;
import org.pocketcampus.plugin.authentication.shared.AuthenticationService.Iface;
import org.pocketcampus.plugin.authentication.shared.LogoutRequest;
import org.pocketcampus.plugin.authentication.shared.LogoutResponse;

/**
 * Check if current Session is still valid
 * also fetch first and last name of logged in folk
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class LogoutAllSessions extends Request<AuthenticationController, Iface, LogoutRequest, LogoutResponse> {

	IAuthenticationView caller;
	
	public LogoutAllSessions(IAuthenticationView view) {
		caller = view;
	}
	
	@Override
	protected LogoutResponse runInBackground(Iface client, LogoutRequest req) throws Exception {
		return client.destroyAllUserSessions(req);
	}

	@Override
	protected void onResult(AuthenticationController controller, LogoutResponse result) {
		if(result.getStatusCode() == AuthStatusCode.OK) {
			caller.deletedSessions(result.getDeletedSessionsCount());
		} else if(result.getStatusCode() == AuthStatusCode.INVALID_SESSION) {
			caller.deletedSessions(null);
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
