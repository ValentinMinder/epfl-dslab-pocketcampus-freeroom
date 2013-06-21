package org.pocketcampus.plugin.qaforum.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.qaforum.android.QAforumController;
import org.pocketcampus.plugin.qaforum.android.QAforumModel;
import org.pocketcampus.plugin.qaforum.shared.QAforumService.Iface;


public class OneAnswerRequest extends Request<QAforumController, Iface, Integer, String> {

	@Override
	protected String runInBackground(Iface client, Integer param)
			throws Exception {
		return client.oneAnswer(param);
	}
	
	@Override
	protected void onResult(QAforumController controller, String result) {
		if (result.equals("invalid")) {
			controller.notLoggedIn();
		}
		else {
			controller.callactivityOneanswer(result);
			((QAforumModel) controller.getModel()).getListenersToNotify().gotRequestReturn();
		}
	}
	
	@Override
	protected void onError(QAforumController controller, Exception e) {
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}
}
