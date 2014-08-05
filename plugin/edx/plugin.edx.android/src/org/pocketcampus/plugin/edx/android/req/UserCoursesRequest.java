package org.pocketcampus.plugin.edx.android.req;

import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.edx.android.EdXController;
import org.pocketcampus.plugin.edx.android.EdXModel;
import org.pocketcampus.plugin.edx.android.iface.IEdXView;
import org.pocketcampus.plugin.edx.shared.EdXService.Iface;
import org.pocketcampus.plugin.edx.shared.EdxReq;
import org.pocketcampus.plugin.edx.shared.EdxResp;

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
public class UserCoursesRequest extends Request<EdXController, Iface, EdxReq, EdxResp> {

	private IEdXView caller;
	
	public UserCoursesRequest(IEdXView caller) {
		this.caller = caller;
	}
	
	@Override
	protected EdxResp runInBackground(Iface client, EdxReq param) throws Exception {
		return client.getUserCourses(param);
	}

	@Override
	protected void onResult(EdXController controller, EdxResp result) {
		if(result.getStatus() == 200) {
			((EdXModel) controller.getModel()).setUserCourses(result.getUserCourses());
			
			keepInCache();
			
		} else if(result.getStatus() == 407) {
			caller.sessionTimedOut();
			
		} else if(result.getStatus() == 500) {
			caller.serverFailure();
			
		} else { // 400?
			caller.upstreamServerFailure();
			
		}
	}

	@Override
	protected void onError(EdXController controller, Exception e) {
		// TODO differentiate server crash from network error
		//      caller.serverFailure();
		if(foundInCache())
			caller.networkErrorCacheExists();
		else
			caller.networkErrorHappened();
		e.printStackTrace();
	}
	
}
