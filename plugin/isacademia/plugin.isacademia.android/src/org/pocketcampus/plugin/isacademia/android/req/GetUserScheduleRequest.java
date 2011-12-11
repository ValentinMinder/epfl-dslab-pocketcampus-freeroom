package org.pocketcampus.plugin.isacademia.android.req;

import java.util.List;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.authentication.shared.SessionId;
import org.pocketcampus.plugin.isacademia.android.IsacademiaController;
import org.pocketcampus.plugin.isacademia.android.IsacademiaModel;
import org.pocketcampus.plugin.isacademia.shared.IsacademiaService.Iface;
import org.pocketcampus.plugin.isacademia.shared.Seance;

public class GetUserScheduleRequest extends Request<IsacademiaController, Iface, SessionId, List<Seance>> {

	@Override
	protected List<Seance> runInBackground(Iface client, SessionId param) throws Exception {
		return client.getUserSchedule(param);
	}

	@Override
	protected void onResult(IsacademiaController controller, List<Seance> result) {
		((IsacademiaModel) controller.getModel()).setSchedule(result);
	}

	@Override
	protected void onError(IsacademiaController controller, Exception e) {
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}
}
