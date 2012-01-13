package org.pocketcampus.plugin.map.android.req;

import java.util.List;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.map.android.MapMainController;
import org.pocketcampus.plugin.map.android.MapModel;
import org.pocketcampus.plugin.map.shared.MapItem;
import org.pocketcampus.plugin.map.shared.MapService.Iface;

/**
 * Request to get items of a layer, given it's id.
 * 
 * @author Florian <florian.laurent@epfl.ch>
 *
 */
public class LayerItemsRequest extends Request<MapMainController, Iface, Integer, List<MapItem>> {

	@Override
	protected List<MapItem> runInBackground(Iface client, Integer id) throws Exception {
		return client.getLayerItems(id);
	}

	@Override
	protected void onError(MapMainController controller, Exception e) {
		System.out.println("onError");
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}

	@Override
	protected void onResult(MapMainController controller, List<MapItem> result) {
		((MapModel) controller.getModel()).addLayerItems(result);
	}
	
	
}