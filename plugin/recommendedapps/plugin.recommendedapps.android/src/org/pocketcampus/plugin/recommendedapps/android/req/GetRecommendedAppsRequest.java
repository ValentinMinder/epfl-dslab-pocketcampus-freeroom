package org.pocketcampus.plugin.recommendedapps.android.req;

import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.recommendedapps.android.RecommendedAppsController;
import org.pocketcampus.plugin.recommendedapps.android.iface.IRecommendedAppsView;
import org.pocketcampus.plugin.recommendedapps.shared.RecommendedAppsRequest;
import org.pocketcampus.plugin.recommendedapps.shared.RecommendedAppsResponse;
import org.pocketcampus.plugin.recommendedapps.shared.RecommendedAppsResponseStatus;
import org.pocketcampus.plugin.recommendedapps.shared.RecommendedAppsService.Iface;

public class GetRecommendedAppsRequest
		extends
		Request<RecommendedAppsController, Iface, RecommendedAppsRequest, RecommendedAppsResponse> {

	private IRecommendedAppsView caller;

	public GetRecommendedAppsRequest(IRecommendedAppsView caller) {
		this.caller = caller;
	}

	@Override
	public RecommendedAppsResponse runInBackground(Iface client,
			RecommendedAppsRequest request) throws Exception {
		return client.getRecommendedApps(request);
	}

	@Override
	protected void onResult(RecommendedAppsController controller,
			RecommendedAppsResponse result) {
		if (result.getStatus() == RecommendedAppsResponseStatus.OK) {
			controller.updateModelWithRecommendedAppsResponse(result);
			// keepInCache();
		} else {
			caller.serverDown();
		}
	}

	@Override
	protected void onError(RecommendedAppsController controller, Exception e) {
		caller.networkErrorHappened();
		e.printStackTrace();

	}
}
