package org.pocketcampus.plugin.map.android.req;

import java.util.List;

import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.map.android.MapController;
import org.pocketcampus.plugin.map.android.MapMainView;
import org.pocketcampus.plugin.map.android.MapModel;
import org.pocketcampus.plugin.map.shared.MapItem;
import org.pocketcampus.plugin.map.shared.MapService.Iface;

/**
 * Search request.
 * 
 * @author Florian <florian.laurent@epfl.ch>
 *
 */
public class SearchRequest extends Request<MapController, Iface, String, List<MapItem>> {

	
	private MapMainView caller;
	
	public SearchRequest(MapMainView caller) {
		this.caller = caller;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		caller.showLoading();
	}

	
	@Override
	protected List<MapItem> runInBackground(Iface client, String param) throws Exception {
		return client.search(param);
	}

	@Override
	protected void onResult(MapController controller, List<MapItem> result) {
		caller.hideLoading();
		((MapModel) controller.getModel()).setSearchResult(result);
	}
	
	@Override
	protected void onError(MapController controller, Exception e) {
		caller.hideLoading();
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}

}