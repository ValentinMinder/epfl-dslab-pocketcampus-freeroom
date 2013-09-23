package org.pocketcampus.plugin.qaforum.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.qaforum.android.QAforumController;
import org.pocketcampus.plugin.qaforum.android.QAforumModel;
import org.pocketcampus.plugin.qaforum.shared.QATequilaToken;
import org.pocketcampus.plugin.qaforum.shared.QAforumService.Iface;
import org.pocketcampus.plugin.qaforum.shared.s_session;

public class GetSessionIdRequest extends Request<QAforumController, Iface, QATequilaToken, s_session> {

	@Override
	protected s_session runInBackground(Iface client, QATequilaToken param) throws Exception {
		return client.getSessionid(param);
	}

	@Override
	protected void onResult(QAforumController controller, s_session result) {
		if (result==null) {
			((QAforumModel) controller.getModel()).getListenersToNotify().authenticationFailed();
			return;
		}
		((QAforumModel) controller.getModel()).getListenersToNotify().gotRequestReturn();
		((QAforumModel) controller.getModel()).setQAforumCookie(result);
		((QAforumController) controller).pushnotification();
		if (result.intro==0) {
			((QAforumController) controller).callactivityHelp();
		}
	}

	@Override
	protected void onError(QAforumController controller, Exception e) {
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}
	
}
