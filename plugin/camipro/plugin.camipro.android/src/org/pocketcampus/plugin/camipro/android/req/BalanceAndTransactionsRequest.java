package org.pocketcampus.plugin.camipro.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.authentication.shared.SessionId;
import org.pocketcampus.plugin.camipro.android.CamiproController;
import org.pocketcampus.plugin.camipro.android.CamiproModel;
import org.pocketcampus.plugin.camipro.shared.BalanceAndTransactions;
import org.pocketcampus.plugin.camipro.shared.CamiproService.Iface;

public class BalanceAndTransactionsRequest extends Request<CamiproController, Iface, SessionId, BalanceAndTransactions> {

	@Override
	protected BalanceAndTransactions runInBackground(Iface client, SessionId param) throws Exception {
		System.out.println("Getting BalanceAndTransactions");
		return client.getBalanceAndTransactions(param);
	}

	@Override
	protected void onResult(CamiproController controller, BalanceAndTransactions result) {
		((CamiproModel) controller.getModel()).setBalance(result.getIBalance());
		((CamiproModel) controller.getModel()).setTransactions(result.getITransactions());
	}

	@Override
	protected void onError(CamiproController controller, Exception e) {
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}
	
}
