package org.pocketcampus.plugin.camipro.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.camipro.shared.TequilaToken;
import org.pocketcampus.plugin.camipro.android.CamiproController;
import org.pocketcampus.plugin.camipro.android.CamiproModel;
import org.pocketcampus.plugin.camipro.shared.CamiproService.Iface;
import org.pocketcampus.plugin.camipro.shared.CamiproSession;

/**
 * GetCamiproSessionRequest
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class GetCamiproSessionRequest extends Request<CamiproController, Iface, TequilaToken, CamiproSession> {

	@Override
	protected CamiproSession runInBackground(Iface client, TequilaToken param) throws Exception {
		return client.getCamiproSession(param);
	}

	@Override
	protected void onResult(CamiproController controller, CamiproSession result) {
		((CamiproModel) controller.getModel()).setCamiproCookie(result.getCamiproCookie());
		((CamiproModel) controller.getModel()).getListenersToNotify().gotCamiproCookie();
	}

	@Override
	protected void onError(CamiproController controller, Exception e) {
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}
	
}
