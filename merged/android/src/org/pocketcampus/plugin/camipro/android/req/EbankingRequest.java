package org.pocketcampus.plugin.camipro.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.camipro.android.CamiproController;
import org.pocketcampus.plugin.camipro.android.CamiproModel;
import org.pocketcampus.plugin.camipro.shared.CamiproService.Iface;
import org.pocketcampus.plugin.camipro.shared.EbankingBean;

public class EbankingRequest extends Request<CamiproController, Iface, Object, EbankingBean> {

	@Override
	protected EbankingBean runInBackground(Iface client, Object param) throws Exception {
		System.out.println("Getting EbankingBean");
		return client.getEbankingBean();
	}

	@Override
	protected void onResult(CamiproController controller, EbankingBean result) {
		((CamiproModel) controller.getModel()).setEbanking(result);
	}

	@Override
	protected void onError(CamiproController controller, Exception e) {
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}

}
