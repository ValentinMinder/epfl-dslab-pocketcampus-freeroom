package org.pocketcampus.plugin.isacademia.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.isacademia.android.IsacademiaController;
import org.pocketcampus.plugin.isacademia.android.IsacademiaModel;
import org.pocketcampus.plugin.isacademia.shared.IsaCoursesListReply;
import org.pocketcampus.plugin.isacademia.shared.IsaRequest;
import org.pocketcampus.plugin.isacademia.shared.IsacademiaService.Iface;

/**
 * GetUserCoursesRequest
 * 
 * This class sends an HttpRequest using Thrift to the PocketCampus server
 * in order to get the ISA registered courses of the logged in user.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class GetUserCoursesRequest extends Request<IsacademiaController, Iface, IsaRequest, IsaCoursesListReply> {

	@Override
	protected IsaCoursesListReply runInBackground(Iface client, IsaRequest param) throws Exception {
		return client.getUserCourses(param);
	}

	@Override
	protected void onResult(IsacademiaController controller, IsaCoursesListReply result) {
		if(result.getIStatus() == 404) {
			((IsacademiaModel) controller.getModel()).getListenersToNotify().isaServersDown();
		} else if(result.getIStatus() == 407) {
			controller.notLoggedIn();
		} else if(result.getIStatus() == 200) {
			((IsacademiaModel) controller.getModel()).setCourses(result.getICourses());
		}
	}

	@Override
	protected void onError(IsacademiaController controller, Exception e) {
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}
}
