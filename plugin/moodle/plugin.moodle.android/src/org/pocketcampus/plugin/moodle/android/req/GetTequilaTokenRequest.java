package org.pocketcampus.plugin.moodle.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.authentication.shared.TequilaToken;
import org.pocketcampus.plugin.moodle.android.MoodleController;
import org.pocketcampus.plugin.moodle.android.MoodleModel;
import org.pocketcampus.plugin.moodle.shared.MoodleService.Iface;

/**
 * GetTequilaTokenRequest
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class GetTequilaTokenRequest extends Request<MoodleController, Iface, Object, TequilaToken> {

	@Override
	protected TequilaToken runInBackground(Iface client, Object param) throws Exception {
		return client.getTequilaToken();
	}

	@Override
	protected void onResult(MoodleController controller, TequilaToken result) {
		((MoodleModel) controller.getModel()).setTequilaToken(result);
	}

	@Override
	protected void onError(MoodleController controller, Exception e) {
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}
	
}
