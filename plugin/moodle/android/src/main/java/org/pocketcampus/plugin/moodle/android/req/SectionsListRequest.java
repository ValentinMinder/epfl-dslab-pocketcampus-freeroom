package org.pocketcampus.plugin.moodle.android.req;

import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.moodle.android.MoodleController;
import org.pocketcampus.plugin.moodle.android.MoodleModel;
import org.pocketcampus.plugin.moodle.android.iface.IMoodleView;
import org.pocketcampus.plugin.moodle.shared.MoodleCourseSectionsRequest2;
import org.pocketcampus.plugin.moodle.shared.MoodleCourseSectionsResponse2;
import org.pocketcampus.plugin.moodle.shared.MoodleService.Iface;
import org.pocketcampus.plugin.moodle.shared.MoodleStatusCode2;

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
public class SectionsListRequest extends Request<MoodleController, Iface, MoodleCourseSectionsRequest2, MoodleCourseSectionsResponse2> {

	private IMoodleView caller;
	
	public SectionsListRequest(IMoodleView caller) {
		this.caller = caller;
	}
	
	@Override
	protected MoodleCourseSectionsResponse2 runInBackground(Iface client, MoodleCourseSectionsRequest2 param) throws Exception {
		return client.getSections(param);
	}

	@Override
	protected void onResult(MoodleController controller, MoodleCourseSectionsResponse2 result) {
		if(result.getStatusCode() == MoodleStatusCode2.OK) {
			((MoodleModel) controller.getModel()).setSections(result.getSections());
			
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
