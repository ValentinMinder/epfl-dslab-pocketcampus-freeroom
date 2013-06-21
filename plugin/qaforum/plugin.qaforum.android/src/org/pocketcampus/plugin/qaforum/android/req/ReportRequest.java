package org.pocketcampus.plugin.qaforum.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.qaforum.android.QAforumController;
import org.pocketcampus.plugin.qaforum.android.QAforumModel;
import org.pocketcampus.plugin.qaforum.shared.QAforumService.Iface;
import org.pocketcampus.plugin.qaforum.shared.s_report;


public class ReportRequest extends Request<QAforumController, Iface, s_report, Integer> {

	
	@Override
	protected Integer runInBackground(Iface client, s_report param)
			throws Exception {
		return client.reportQuestion(param);
	}
	
	@Override
	protected void onResult(QAforumController controller, Integer result) {
		if (result==1) {
			((QAforumModel) controller.getModel()).getListenersToNotify().loadingFinished();
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
