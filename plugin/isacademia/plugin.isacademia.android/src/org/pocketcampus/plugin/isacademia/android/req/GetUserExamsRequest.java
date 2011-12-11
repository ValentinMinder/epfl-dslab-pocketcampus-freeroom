package org.pocketcampus.plugin.isacademia.android.req;

import java.util.List;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.authentication.shared.SessionId;
import org.pocketcampus.plugin.isacademia.android.IsacademiaController;
import org.pocketcampus.plugin.isacademia.android.IsacademiaModel;
import org.pocketcampus.plugin.isacademia.shared.Exam;
import org.pocketcampus.plugin.isacademia.shared.IsacademiaService.Iface;

public class GetUserExamsRequest extends Request<IsacademiaController, Iface, SessionId, List<Exam>> {

	@Override
	protected List<Exam> runInBackground(Iface client, SessionId param) throws Exception {
		return client.getUserExams(param);
	}

	@Override
	protected void onResult(IsacademiaController controller, List<Exam> result) {
		((IsacademiaModel) controller.getModel()).setExams(result);
	}

	@Override
	protected void onError(IsacademiaController controller, Exception e) {
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}
}
