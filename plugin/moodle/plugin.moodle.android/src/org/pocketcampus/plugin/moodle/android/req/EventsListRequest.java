package org.pocketcampus.plugin.moodle.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.moodle.android.MoodleController;
import org.pocketcampus.plugin.moodle.android.MoodleModel;
import org.pocketcampus.plugin.moodle.shared.EventsListReply;
import org.pocketcampus.plugin.moodle.shared.MoodleRequest;
import org.pocketcampus.plugin.moodle.shared.MoodleService.Iface;

/**
 * EventsListRequest
 * 
 * This class sends an HttpRequest using Thrift to the PocketCampus server
 * in order to get the Moodle Events
 * of the logged in user.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class EventsListRequest extends Request<MoodleController, Iface, MoodleRequest, EventsListReply> {

	@Override
	protected EventsListReply runInBackground(Iface client, MoodleRequest param) throws Exception {
		return client.getEventsList(param);
	}

	@Override
	protected void onResult(MoodleController controller, EventsListReply result) {
		if(result.getIStatus() == 404) {
			((MoodleModel) controller.getModel()).getListenersToNotify().moodleServersDown();
		} else if(result.getIStatus() == 407) {
			controller.notLoggedIn();
		} else if(result.getIStatus() == 200) {
			((MoodleModel) controller.getModel()).setEvents(result.getIEvents());
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
