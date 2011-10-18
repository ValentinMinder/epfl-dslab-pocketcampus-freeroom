package org.pocketcampus.plugin.camipro.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.camipro.android.CamiproController;
import org.pocketcampus.plugin.camipro.android.CamiproModel;
import org.pocketcampus.plugin.camipro.shared.CamiproService.Iface;

public class BalanceRequest extends Request<CamiproController, Iface, Object, Double> {

	@Override
	protected Double runInBackground(Iface client, Object param) throws Exception {
		System.out.println("Getting Balance");
		return client.getBalance();
	}

	@Override
	protected void onResult(CamiproController controller, Double result) {
		((CamiproModel) controller.getModel()).setBalance(result);
	}

	@Override
	protected void onError(CamiproController controller, Exception e) {
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}
	
}
