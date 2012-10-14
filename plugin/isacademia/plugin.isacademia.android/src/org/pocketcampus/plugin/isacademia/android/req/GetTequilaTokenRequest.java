package org.pocketcampus.plugin.isacademia.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.isacademia.shared.TequilaToken;
import org.pocketcampus.plugin.isacademia.android.IsacademiaController;
import org.pocketcampus.plugin.isacademia.android.IsacademiaModel;
import org.pocketcampus.plugin.isacademia.shared.IsacademiaService.Iface;

/**
 * GetTequilaTokenRequest
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class GetTequilaTokenRequest extends Request<IsacademiaController, Iface, Object, TequilaToken> {

	@Override
	protected TequilaToken runInBackground(Iface client, Object param) throws Exception {
		return client.getTequilaTokenForIsa();
	}

	@Override
	protected void onResult(IsacademiaController controller, TequilaToken result) {
		((IsacademiaModel) controller.getModel()).setTequilaToken(result);
		controller.gotTequilaToken();
	}

	@Override
	protected void onError(IsacademiaController controller, Exception e) {
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}
	
}
