package org.pocketcampus.plugin.qaforum.android.req;

import org.json.JSONException;
import org.json.JSONObject;
import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.qaforum.android.QAforumController;
import org.pocketcampus.plugin.qaforum.shared.QAforumService.Iface;
import org.pocketcampus.plugin.qaforum.shared.s_accept;

public class AcceptRequest extends Request<QAforumController, Iface, s_accept, String> {

	@Override
	protected String runInBackground(Iface client, s_accept param)
			throws Exception {
		return client.acceptNotif(param);
	}

	@Override
	protected void onResult(QAforumController controller, String result) {
		if(result.equals("declined"))
			return;
		try {
			JSONObject messageJsonObject=new JSONObject(result);
			String typeString=messageJsonObject.getString("type");
			if(typeString.equals("forwardquestion")){//(a) question(s) received	
				controller.callactivityQuestion(messageJsonObject);
			}
			else if (typeString.equals("forwardanswer")) {//(a) answer(s) received		
				controller.callactivityAnswer(messageJsonObject);				
			}
			else if(typeString.equals("transfer")){//(a) feedback(s) received	
				controller.callactivityFeedback(messageJsonObject);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	protected void onError(QAforumController controller, Exception e) {
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}
	
}
