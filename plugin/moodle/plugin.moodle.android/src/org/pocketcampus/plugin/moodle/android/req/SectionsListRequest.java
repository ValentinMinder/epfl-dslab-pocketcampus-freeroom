package org.pocketcampus.plugin.moodle.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.moodle.android.MoodleController;
import org.pocketcampus.plugin.moodle.android.MoodleModel;
import org.pocketcampus.plugin.moodle.android.iface.IMoodleView;
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
public class SectionsListRequest extends Request<MoodleController, Iface, String, SectionsListReply> {

	private IMoodleView caller;
	
	public SectionsListRequest(IMoodleView caller) {
		this.caller = caller;
	}
	
	@Override
	protected SectionsListReply runInBackground(Iface client, String param) throws Exception {
		return client.getCourseSectionsAPI(param);
	}

	@Override
	protected void onResult(MoodleController controller, SectionsListReply result) {
		if(result.getIStatus() == 200) {
			((MoodleModel) controller.getModel()).setSections(result.getISections());
			
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
