package org.pocketcampus.plugin.moodle.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.moodle.shared.TequilaToken;
import org.pocketcampus.plugin.moodle.android.MoodleController;
import org.pocketcampus.plugin.moodle.android.MoodleModel;
import org.pocketcampus.plugin.moodle.shared.MoodleService.Iface;
import org.pocketcampus.plugin.moodle.shared.MoodleSession;

/**
 * GetMoodleSessionRequest
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class GetMoodleSessionRequest extends Request<MoodleController, Iface, TequilaToken, MoodleSession> {

	@Override
	protected MoodleSession runInBackground(Iface client, TequilaToken param) throws Exception {
		return client.getMoodleSession(param);
	}

	@Override
	protected void onResult(MoodleController controller, MoodleSession result) {
		((MoodleModel) controller.getModel()).setMoodleCookie(result.getMoodleCookie());
		((MoodleModel) controller.getModel()).getListenersToNotify().gotMoodleCookie();
	}

	@Override
	protected void onError(MoodleController controller, Exception e) {
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}
	
}
