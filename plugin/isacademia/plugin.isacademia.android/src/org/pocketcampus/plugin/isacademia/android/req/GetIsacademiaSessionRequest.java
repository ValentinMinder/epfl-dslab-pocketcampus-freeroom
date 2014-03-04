package org.pocketcampus.plugin.isacademia.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.isacademia.shared.TequilaToken;
import org.pocketcampus.plugin.isacademia.android.IsAcademiaController;
import org.pocketcampus.plugin.isacademia.android.IsAcademiaModel;
import org.pocketcampus.plugin.isacademia.shared.IsacademiaService.Iface;
import org.pocketcampus.plugin.isacademia.shared.IsaSession;

/**
 * GetMoodleSessionRequest
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class GetIsacademiaSessionRequest extends Request<IsAcademiaController, Iface, TequilaToken, IsaSession> {

	@Override
	protected IsaSession runInBackground(Iface client, TequilaToken param) throws Exception {
		return client.getIsaSession(param);
	}

	@Override
	protected void onResult(IsAcademiaController controller, IsaSession result) {
		((IsAcademiaModel) controller.getModel()).setIsacademiaCookie(result.getIsaCookie());
		((IsAcademiaModel) controller.getModel()).getListenersToNotify().gotIsaCookie();
	}

	@Override
	protected void onError(IsAcademiaController controller, Exception e) {
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}
	
}
