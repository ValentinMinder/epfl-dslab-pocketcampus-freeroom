package org.pocketcampus.plugin.isacademia.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.isacademia.shared.TequilaToken;
import org.pocketcampus.plugin.isacademia.android.IsacademiaController;
import org.pocketcampus.plugin.isacademia.android.IsacademiaModel;
import org.pocketcampus.plugin.isacademia.shared.IsacademiaService.Iface;
import org.pocketcampus.plugin.isacademia.shared.IsaSession;

/**
 * GetMoodleSessionRequest
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class GetIsacademiaSessionRequest extends Request<IsacademiaController, Iface, TequilaToken, IsaSession> {

	@Override
	protected IsaSession runInBackground(Iface client, TequilaToken param) throws Exception {
		return client.getIsaSession(param);
	}

	@Override
	protected void onResult(IsacademiaController controller, IsaSession result) {
		((IsacademiaModel) controller.getModel()).setIsacademiaCookie(result.getIsaCookie());
		((IsacademiaModel) controller.getModel()).getListenersToNotify().gotIsaCookie();
	}

	@Override
	protected void onError(IsacademiaController controller, Exception e) {
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}
	
}
