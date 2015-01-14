package org.pocketcampus.plugin.authentication.android.req;

import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.authentication.android.AuthenticationController;
import org.pocketcampus.plugin.authentication.android.iface.IAuthenticationView;
import org.pocketcampus.plugin.authentication.shared.AuthStatusCode;
import org.pocketcampus.plugin.authentication.shared.AuthenticationService.Iface;
import org.pocketcampus.plugin.authentication.shared.UserAttributesRequest;
import org.pocketcampus.plugin.authentication.shared.UserAttributesResponse;

/**
 * Check if current Session is still valid
 * also fetch first and last name of logged in folk
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class FetchUserAttributes extends Request<AuthenticationController, Iface, UserAttributesRequest, UserAttributesResponse> {

	IAuthenticationView caller;
	
	public FetchUserAttributes(IAuthenticationView view) {
		caller = view;
	}
	
	@Override
	protected UserAttributesResponse runInBackground(Iface client, UserAttributesRequest req) throws Exception {
		return client.getUserAttributes(req);
	}

	@Override
	protected void onResult(AuthenticationController controller, UserAttributesResponse result) {
		if(result.getStatusCode() == AuthStatusCode.OK) {
			if(caller != null)
				caller.gotUserAttributes(result.getUserAttributes());
			else
				controller.sessionIsValid();
			keepInCache();
		} else if(result.getStatusCode() == AuthStatusCode.INVALID_SESSION) {
			if(caller != null)
				caller.gotUserAttributes(null);
			else
				controller.sessionIsInvalid();
		} else {
			controller.notifyNetworkError();
		}
	}

	@Override
	protected void onError(AuthenticationController controller, Exception e) {
		e.printStackTrace();
		if(foundInCache()) {
			controller.getUserAttributes(caller, false);
		} else {
			controller.notifyNetworkError();
		}
	}
	
}
