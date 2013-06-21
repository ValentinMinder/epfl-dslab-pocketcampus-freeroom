package org.pocketcampus.plugin.qaforum.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.qaforum.android.QAforumController;
import org.pocketcampus.plugin.qaforum.android.QAforumModel;
import org.pocketcampus.plugin.qaforum.shared.s_ask;
import org.pocketcampus.plugin.qaforum.shared.QAforumService.Iface;


public class AskRequest extends Request<QAforumController, Iface, s_ask, Integer> {

	@Override
	protected Integer runInBackground(Iface client, s_ask param)
			throws Exception {
		return client.askQuestion(param);
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
