package org.pocketcampus.plugin.qaforum.android.req;

import org.json.JSONException;
import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.qaforum.android.QAforumController;
import org.pocketcampus.plugin.qaforum.shared.QAforumService.Iface;
import org.pocketcampus.plugin.qaforum.shared.s_relation;


public class RelationRequest extends Request<QAforumController, Iface, s_relation, String> {

	
	@Override
	protected String runInBackground(Iface client, s_relation param)
			throws Exception {
		return client.relationship(param);
	}
	
	@Override
	protected void onResult(QAforumController controller, String result) {
		if (result.equals("invalid")) {
			controller.notLoggedIn();
		}
		else {
			try {
				controller.callactivityRelation(result);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}	

	@Override
	protected void onError(QAforumController controller, Exception e) {
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}
}
