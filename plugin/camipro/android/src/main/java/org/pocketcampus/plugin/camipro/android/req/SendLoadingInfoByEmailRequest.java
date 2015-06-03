package org.pocketcampus.plugin.camipro.android.req;

import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.camipro.android.CamiproCardRechargeView;
import org.pocketcampus.plugin.camipro.android.CamiproController;
import org.pocketcampus.plugin.camipro.android.CamiproModel;
import org.pocketcampus.plugin.camipro.shared.CamiproService.Iface;
import org.pocketcampus.plugin.camipro.shared.CamiproRequest;
import org.pocketcampus.plugin.camipro.shared.SendMailResult;

/**
 * SendLoadingInfoByEmailRequest
 * 
 * This class sends an HttpRequest using Thrift to the PocketCampus server
 * in order to send the e-banking card charging information to the user
 * by email.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class SendLoadingInfoByEmailRequest extends Request<CamiproController, Iface, CamiproRequest, SendMailResult> {

	CamiproCardRechargeView caller;
	
	public SendLoadingInfoByEmailRequest(CamiproCardRechargeView caller) {
		this.caller = caller;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		caller.showLoading();
	}
	
	@Override
	protected SendMailResult runInBackground(Iface client, CamiproRequest param) throws Exception {
		System.out.println("Requesting to SendLoadingInfoByEmail");
		return client.sendLoadingInfoByEmail(param);
	}

	@Override
	protected void onResult(CamiproController controller, SendMailResult result) {
		caller.hideLoading();
		if(result.getIStatus() == 404) {
			((CamiproModel) controller.getModel()).getListenersToNotify().camiproServersDown();
		} else if(result.getIStatus() == 407) {
			controller.notLoggedIn();
		} else if(result.getIStatus() == 200) {
			((CamiproModel) controller.getModel()).getListenersToNotify().emailSent(result.getIResultText());
		}
	}

	@Override
	protected void onError(CamiproController controller, Exception e) {
		caller.hideLoading();
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}
	
}
