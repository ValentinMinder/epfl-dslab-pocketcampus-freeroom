package org.pocketcampus.plugin.camipro.android.req;

import java.util.List;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.camipro.android.CamiproController;
import org.pocketcampus.plugin.camipro.android.CamiproModel;
import org.pocketcampus.plugin.camipro.shared.CamiproService.Iface;
import org.pocketcampus.plugin.camipro.shared.Transaction;

public class TransactionsRequest extends Request<CamiproController, Iface, Object, List<Transaction>> {

	@Override
	protected List<Transaction> runInBackground(Iface client, Object param) throws Exception {
		System.out.println("Getting Transactions");
		return client.getTransactions();
	}

	@Override
	protected void onResult(CamiproController controller, List<Transaction> result) {
		((CamiproModel) controller.getModel()).setTransactions(result);
	}

	@Override
	protected void onError(CamiproController controller, Exception e) {
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}
	
}
