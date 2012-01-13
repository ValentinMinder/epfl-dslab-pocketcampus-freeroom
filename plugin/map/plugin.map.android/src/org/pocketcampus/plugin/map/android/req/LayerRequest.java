package org.pocketcampus.plugin.map.android.req;

import java.util.List;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.map.android.MapMainController;
import org.pocketcampus.plugin.map.android.MapModel;
import org.pocketcampus.plugin.map.shared.MapLayer;
import org.pocketcampus.plugin.map.shared.MapService.Iface;

/**
 * Request to get the list of all map layers.
 * 
 * @author Florian <florian.laurent@epfl.ch>
 *
 */
public class LayerRequest extends Request<MapMainController, Iface, Object, List<MapLayer>> {

	@Override
	protected List<MapLayer> runInBackground(Iface client, Object param) throws Exception {
		return client.getLayerList();
	}

	@Override
	protected void onResult(MapMainController controller, List<MapLayer> result) {
		((MapModel) controller.getModel()).setLayers(result);
	}

	@Override
	protected void onError(MapMainController controller, Exception e) {
		System.out.println("onError");
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}
}