package org.pocketcampus.plugin.qaforum.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.qaforum.android.QAforumController;
import org.pocketcampus.plugin.qaforum.android.QAforumModel;
import org.pocketcampus.plugin.qaforum.shared.QAforumService.Iface;
import org.pocketcampus.plugin.qaforum.shared.s_feedback;


public class FeedbackRequest extends Request<QAforumController, Iface, s_feedback, Integer> {

	@Override
	protected Integer runInBackground(Iface client, s_feedback param)
			throws Exception {
		return client.feedbackQuestion(param);
	}
	
	@Override
	protected void onResult(QAforumController controller, Integer result) {
		if (result==1) {
			((QAforumModel) controller.getModel()).getListenersToNotify().gotRequestReturn();	
		}
		else {
			controller.notLoggedIn();
		}
	}	

	@Override
	protected void onError(QAforumController controller, Exception e) {
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
		
	}
}
