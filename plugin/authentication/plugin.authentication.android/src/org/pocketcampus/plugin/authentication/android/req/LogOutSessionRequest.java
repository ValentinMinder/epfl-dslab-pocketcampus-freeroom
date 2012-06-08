package org.pocketcampus.plugin.authentication.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.authentication.android.AuthenticationController;
import org.pocketcampus.plugin.authentication.shared.AuthenticationService.Iface;
import org.pocketcampus.plugin.authentication.shared.SessionId;

/**
 * LogOutSessionRequest
 * 
 * This class sends an HttpRequest using Thrift to the PocketCampus server
 * in order to sign out the SessionId for a specific service.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class LogOutSessionRequest extends Request<AuthenticationController, Iface, SessionId, Integer> {
	
	@Override
	protected Integer runInBackground(Iface client, SessionId param) throws Exception {
		return client.logOutSession(param);
	}

	@Override
	protected void onResult(AuthenticationController controller, Integer result) {
		controller.logoutFinished();
	}
	
	@Override
	protected void onError(AuthenticationController controller, Exception e) {
		e.printStackTrace();
		controller.logoutFinished();
	}
	
}
