package org.pocketcampus.plugin.map.android.req;

import java.util.List;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.map.android.MapMainController;
import org.pocketcampus.plugin.map.android.MapModel;
import org.pocketcampus.plugin.map.shared.MapItem;
import org.pocketcampus.plugin.map.shared.MapService.Iface;

/**
 * Search request.
 * 
 * @author Florian <florian.laurent@epfl.ch>
 *
 */
public class SearchRequest extends Request<MapMainController, Iface, String, List<MapItem>> {

	@Override
	protected void onError(MapMainController controller, Exception e) {
		System.out.println("onError");
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}

	@Override
	protected List<MapItem> runInBackground(Iface client, String param) throws Exception {
		return client.search(param);
	}

	@Override
	protected void onResult(MapMainController controller, List<MapItem> result) {
		((MapModel) controller.getModel()).setSearchResult(result);
	}
}