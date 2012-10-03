package org.pocketcampus.plugin.moodle.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.moodle.android.MoodleController;
import org.pocketcampus.plugin.moodle.android.MoodleModel;
import org.pocketcampus.plugin.moodle.shared.MoodleRequest;
import org.pocketcampus.plugin.moodle.shared.MoodleService.Iface;
import org.pocketcampus.plugin.moodle.shared.SectionsListReply;

/**
 * CoursesListRequest
 * 
 * This class sends an HttpRequest using Thrift to the PocketCampus server
 * in order to get the Moodle Sections of a given course
 * of the logged in user.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class SectionsListRequest extends Request<MoodleController, Iface, MoodleRequest, SectionsListReply> {

	@Override
	protected SectionsListReply runInBackground(Iface client, MoodleRequest param) throws Exception {
		return client.getCourseSections(param);
	}

	@Override
	protected void onResult(MoodleController controller, SectionsListReply result) {
		if(result.getIStatus() == 404) {
			((MoodleModel) controller.getModel()).getListenersToNotify().moodleServersDown();
		} else if(result.getIStatus() == 407) {
			controller.notLoggedIn();
		} else if(result.getIStatus() == 405) { // course id was not given as an argument
			// TODO display appropriate error
			((MoodleModel) controller.getModel()).getListenersToNotify().moodleServersDown();
		} else if(result.getIStatus() == 200) {
			((MoodleModel) controller.getModel()).setSections(result.getISections());
			if(!wasServicedFromCache())
				keepInCache();
			/*else
				refreshAsWell();*/
		}
	}

	@Override
	protected void onError(MoodleController controller, Exception e) {
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}
	
}
