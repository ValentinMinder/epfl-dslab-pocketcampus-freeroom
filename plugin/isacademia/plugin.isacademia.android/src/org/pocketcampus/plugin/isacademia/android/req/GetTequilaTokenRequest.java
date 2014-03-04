package org.pocketcampus.plugin.isacademia.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.isacademia.android.IsAcademiaController;
import org.pocketcampus.plugin.isacademia.android.IsAcademiaModel;
import org.pocketcampus.plugin.isacademia.shared.IsAcademiaService.Iface;
import org.pocketcampus.plugin.isacademia.shared.IsaTokenResponse;

/**
 * GetTequilaTokenRequest
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class GetTequilaTokenRequest extends Request<IsAcademiaController, Iface, Object, IsaTokenResponse> {

	@Override
	protected IsaTokenResponse runInBackground(Iface client, Object param) throws Exception {
		return client.getIsaTequilaToken();
	}

	@Override
	protected void onResult(IsAcademiaController controller, IsaTokenResponse result) {
		((IsAcademiaModel) controller.getModel()).setTequilaToken(null);
		controller.gotTequilaToken();
	}

	@Override
	protected void onError(IsAcademiaController controller, Exception e) {
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}
	
}
