package org.pocketcampus.plugin.qaforum.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.qaforum.android.QAforumController;
import org.pocketcampus.plugin.qaforum.shared.QAforumService.Iface;


public class MatchingQuestionRequest extends Request<QAforumController, Iface, String, String> {

	@Override
	protected String runInBackground(Iface client, String param)
			throws Exception {
		return client.questionMatching(param);
	}
	
	@Override
	protected void onResult(QAforumController controller, String result) {
		if (result.equals("invalid")) {
			controller.notLoggedIn();
		}
		else {
			controller.showMatching(result);	
		}	
	}
	
	@Override
	protected void onError(QAforumController controller, Exception e) {
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
		
	}
}
