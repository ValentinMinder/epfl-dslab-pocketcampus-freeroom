package org.pocketcampus.plugin.moodle.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.moodle.android.MoodleController;
import org.pocketcampus.plugin.moodle.android.MoodleModel;
import org.pocketcampus.plugin.moodle.shared.CoursesListReply;
import org.pocketcampus.plugin.moodle.shared.MoodleRequest;
import org.pocketcampus.plugin.moodle.shared.MoodleService.Iface;

/**
 * CoursesListRequest
 * 
 * This class sends an HttpRequest using Thrift to the PocketCampus server
 * in order to get the Moodle Courses
 * of the logged in user.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class CoursesListRequest extends Request<MoodleController, Iface, MoodleRequest, CoursesListReply> {

	@Override
	protected CoursesListReply runInBackground(Iface client, MoodleRequest param) throws Exception {
		return client.getCoursesList(param);
	}

	@Override
	protected void onResult(MoodleController controller, CoursesListReply result) {
		if(result.getIStatus() == 404) {
			((MoodleModel) controller.getModel()).getListenersToNotify().moodleServersDown();
		} else if(result.getIStatus() == 407) {
			controller.notLoggedIn();
		} else if(result.getIStatus() == 200) {
			((MoodleModel) controller.getModel()).setCourses(result.getICourses());
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
