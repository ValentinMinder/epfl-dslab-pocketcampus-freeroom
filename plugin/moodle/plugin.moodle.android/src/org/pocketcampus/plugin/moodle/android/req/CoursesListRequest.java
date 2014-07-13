package org.pocketcampus.plugin.moodle.android.req;

import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.moodle.android.MoodleController;
import org.pocketcampus.plugin.moodle.android.MoodleModel;
import org.pocketcampus.plugin.moodle.android.iface.IMoodleView;
import org.pocketcampus.plugin.moodle.shared.CoursesListReply;
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
public class CoursesListRequest extends Request<MoodleController, Iface, Object, CoursesListReply> {

	private IMoodleView caller;
	
	public CoursesListRequest(IMoodleView caller) {
		this.caller = caller;
	}
	
	@Override
	protected CoursesListReply runInBackground(Iface client, Object param) throws Exception {
		return client.getCoursesListAPI("dummy");
	}

	@Override
	protected void onResult(MoodleController controller, CoursesListReply result) {
		if(result.getIStatus() == 200) {
			((MoodleModel) controller.getModel()).setCourses(result.getICourses());
			
			keepInCache();
			
		} else if(result.getIStatus() == 407) {
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
