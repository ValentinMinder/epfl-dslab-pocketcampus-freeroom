package org.pocketcampus.plugin.qaforum.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.qaforum.android.QAforumController;
import org.pocketcampus.plugin.qaforum.android.QAforumModel;
import org.pocketcampus.plugin.qaforum.shared.QATequilaToken;
import org.pocketcampus.plugin.qaforum.shared.QAforumService.Iface;

public class GetTequilaTokenRequest extends Request<QAforumController, Iface, Object, QATequilaToken> {

	@Override
	protected QATequilaToken runInBackground(Iface client, Object param) throws Exception {
		return client.getTequilaTokenForQAforum();
	}

	@Override
	protected void onResult(QAforumController controller, QATequilaToken result) {
		((QAforumModel) controller.getModel()).setTequilaToken(result);
		controller.gotTequilaToken();
	}

	@Override
	protected void onError(QAforumController controller, Exception e) {
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}
}
