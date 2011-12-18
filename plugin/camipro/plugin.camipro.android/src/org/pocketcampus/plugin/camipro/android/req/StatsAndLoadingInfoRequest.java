package org.pocketcampus.plugin.camipro.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.camipro.android.CamiproController;
import org.pocketcampus.plugin.camipro.android.CamiproModel;
import org.pocketcampus.plugin.camipro.shared.CamiproService.Iface;
import org.pocketcampus.plugin.camipro.shared.CamiproRequest;
import org.pocketcampus.plugin.camipro.shared.StatsAndLoadingInfo;

public class StatsAndLoadingInfoRequest extends Request<CamiproController, Iface, CamiproRequest, StatsAndLoadingInfo> {

	@Override
	protected StatsAndLoadingInfo runInBackground(Iface client, CamiproRequest param) throws Exception {
		System.out.println("Getting StatsAndLoadingInfo");
		return client.getStatsAndLoadingInfo(param);
	}

	@Override
	protected void onResult(CamiproController controller, StatsAndLoadingInfo result) {
		if(result.getIStatus() == 404) {
			((CamiproModel) controller.getModel()).getListenersToNotify().camiproServersDown();
		} else if(result.getIStatus() == 407) {
			((CamiproModel) controller.getModel()).getListenersToNotify().notLoggedIn();
		} else if(result.getIStatus() == 200) {
			((CamiproModel) controller.getModel()).setCardStatistics(result.getICardStatistics());
			((CamiproModel) controller.getModel()).setCardLoadingWithEbankingInfo(result.getICardLoadingWithEbankingInfo());
		}
	}

	@Override
	protected void onError(CamiproController controller, Exception e) {
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}
	
}
