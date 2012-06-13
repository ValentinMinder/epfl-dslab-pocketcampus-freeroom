package org.pocketcampus.plugin.camipro.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.authentication.shared.TequilaToken;
import org.pocketcampus.plugin.camipro.android.CamiproController;
import org.pocketcampus.plugin.camipro.android.CamiproModel;
import org.pocketcampus.plugin.camipro.shared.CamiproService.Iface;

/**
 * GetTequilaTokenRequest
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class GetTequilaTokenRequest extends Request<CamiproController, Iface, Object, TequilaToken> {

	@Override
	protected TequilaToken runInBackground(Iface client, Object param) throws Exception {
		return client.getTequilaToken();
	}

	@Override
	protected void onResult(CamiproController controller, TequilaToken result) {
		((CamiproModel) controller.getModel()).setTequilaToken(result);
	}

	@Override
	protected void onError(CamiproController controller, Exception e) {
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}
	
}
