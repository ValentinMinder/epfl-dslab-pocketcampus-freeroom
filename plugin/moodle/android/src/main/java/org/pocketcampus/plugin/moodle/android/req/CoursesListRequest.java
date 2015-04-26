package org.pocketcampus.plugin.moodle.android.req;

import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.moodle.android.MoodleController;
import org.pocketcampus.plugin.moodle.android.MoodleModel;
import org.pocketcampus.plugin.moodle.android.iface.IMoodleView;
import org.pocketcampus.plugin.moodle.shared.MoodleCoursesRequest2;
import org.pocketcampus.plugin.moodle.shared.MoodleCoursesResponse2;
import org.pocketcampus.plugin.moodle.shared.MoodleService.Iface;
import org.pocketcampus.plugin.moodle.shared.MoodleStatusCode2;

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
public class CoursesListRequest extends Request<MoodleController, Iface, MoodleCoursesRequest2, MoodleCoursesResponse2> {

	private IMoodleView caller;
	
	public CoursesListRequest(IMoodleView caller) {
		this.caller = caller;
	}
	
	@Override
	protected MoodleCoursesResponse2 runInBackground(Iface client, MoodleCoursesRequest2 param) throws Exception {
		return client.getCourses(param);
	}

	@Override
	protected void onResult(MoodleController controller, MoodleCoursesResponse2 result) {
		if(result.getStatusCode() == MoodleStatusCode2.OK) {
			((MoodleModel) controller.getModel()).setCourses(result.getCourses());
			
			keepInCache();
			
		} else if(result.getStatusCode() == MoodleStatusCode2.AUTHENTICATION_ERROR) {
			caller.notLoggedIn();
			
		} else {
			caller.moodleServersDown();
			
		}
	}

	@Override
	protected void onError(MoodleController controller, Exception e) {
		if(foundInCache())
			caller.networkErrorCacheExists();
		else
			caller.networkErrorHappened();
		e.printStackTrace();
	}
	
}
