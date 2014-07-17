package org.pocketcampus.plugin.edx.android.req;

import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.edx.android.EdXController;
import org.pocketcampus.plugin.edx.android.EdXModel;
import org.pocketcampus.plugin.edx.android.iface.IEdXView;
import org.pocketcampus.plugin.edx.shared.EdXService.Iface;
import org.pocketcampus.plugin.edx.shared.EdxLoginReq;
import org.pocketcampus.plugin.edx.shared.EdxLoginResp;

/**
 * UserCoursesRequest
 * 
 * This class sends an HttpRequest using Thrift to the PocketCampus server
 * in order to get the EdX Courses
 * of the logged in user.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class DoLoginRequest extends Request<EdXController, Iface, EdxLoginReq, EdxLoginResp> {

	private IEdXView caller;
	
	public DoLoginRequest(IEdXView caller) {
		this.caller = caller;
	}
	
	@Override
	protected EdxLoginResp runInBackground(Iface client, EdxLoginReq param) throws Exception {
		return client.doLogin(param);
	}

	@Override
	protected void onResult(EdXController controller, EdxLoginResp result) {
		if(result.getStatus() == 200) {
			((EdXModel) controller.getModel()).setSession(result.getSessionId());
			((EdXModel) controller.getModel()).setUserName(result.getUserName());
			caller.loginSucceeded();
			
		} else if(result.getStatus() == 407) {
			caller.loginFailed();
			
		} else if(result.getStatus() == 500) {
			caller.serverFailure();
			
		} else { // 400? 550?
			caller.upstreamServerFailure();
			
		}
	}

	@Override
	protected void onError(EdXController controller, Exception e) {
		// TODO differentiate server crash from network error
		//      caller.serverFailure();
		caller.networkErrorHappened();
		e.printStackTrace();
	}
	
}
