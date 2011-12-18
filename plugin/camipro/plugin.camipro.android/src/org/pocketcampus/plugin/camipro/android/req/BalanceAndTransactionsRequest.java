package org.pocketcampus.plugin.camipro.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.camipro.android.CamiproController;
import org.pocketcampus.plugin.camipro.android.CamiproModel;
import org.pocketcampus.plugin.camipro.shared.BalanceAndTransactions;
import org.pocketcampus.plugin.camipro.shared.CamiproRequest;
import org.pocketcampus.plugin.camipro.shared.CamiproService.Iface;

public class BalanceAndTransactionsRequest extends Request<CamiproController, Iface, CamiproRequest, BalanceAndTransactions> {

	@Override
	protected BalanceAndTransactions runInBackground(Iface client, CamiproRequest param) throws Exception {
		System.out.println("Getting BalanceAndTransactions");
		return client.getBalanceAndTransactions(param);
	}

	@Override
	protected void onResult(CamiproController controller, BalanceAndTransactions result) {
		if(result.getIStatus() == 404) {
			((CamiproModel) controller.getModel()).getListenersToNotify().camiproServersDown();
		} else if(result.getIStatus() == 407) {
			((CamiproModel) controller.getModel()).getListenersToNotify().notLoggedIn();
		} else if(result.getIStatus() == 200) {
			((CamiproModel) controller.getModel()).setBalance(result.getIBalance());
			((CamiproModel) controller.getModel()).setTransactions(result.getITransactions());
			((CamiproModel) controller.getModel()).setLastUpdateDate(result.getIDate());
		}
	}

	@Override
	protected void onError(CamiproController controller, Exception e) {
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}
	
}
