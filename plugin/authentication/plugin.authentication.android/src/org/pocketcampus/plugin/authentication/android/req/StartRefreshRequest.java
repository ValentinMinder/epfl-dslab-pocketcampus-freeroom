package org.pocketcampus.plugin.authentication.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.authentication.android.AuthenticationController;
import org.pocketcampus.plugin.authentication.shared.AuthenticationService.Iface;
import org.pocketcampus.plugin.authentication.shared.TequilaSession;

/**
 * StartRefreshRequest
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class StartRefreshRequest extends Request<AuthenticationController, Iface, TequilaSession, Integer> {
	
	@Override
	protected Integer runInBackground(Iface client, TequilaSession param) throws Exception {
		return client.startRefresh(param);
	}

	@Override
	protected void onResult(AuthenticationController controller, Integer result) {
		controller.startRefreshFinished(result == 200);
	}
	
	@Override
	protected void onError(AuthenticationController controller, Exception e) {
		e.printStackTrace();
		controller.startRefreshFinished(false);
	}
	
}
