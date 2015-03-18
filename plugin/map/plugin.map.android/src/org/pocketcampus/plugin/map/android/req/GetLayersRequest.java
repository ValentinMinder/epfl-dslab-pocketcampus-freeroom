package org.pocketcampus.plugin.map.android.req;

import java.util.HashMap;
import java.util.Map;

import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.map.android.MapController;
import org.pocketcampus.plugin.map.android.MapModel;
import org.pocketcampus.plugin.map.shared.MapLayer;
import org.pocketcampus.plugin.map.shared.MapLayersResponse;
import org.pocketcampus.plugin.map.shared.MapService.Iface;

/**
 * Search request.
 * 
 * @author Amer Chamseddine <amer@pocketcampus.org>
 *
 */
public class GetLayersRequest extends Request<MapController, Iface, Void, MapLayersResponse> {

	@Override
	protected MapLayersResponse runInBackground(Iface client, Void param) throws Exception {
		return client.getLayers();
	}

	@Override
	protected void onResult(MapController controller, MapLayersResponse result) {
		Map<String, String> map = new HashMap<String, String>();
		Map<Long, MapLayer> a = result.getLayers();
		if(a != null) {
			for(MapLayer ml : a.values()) {
				map.put(ml.getNameForQuery(), ml.getName());
			}
		}
		if(map.size() > 0) {
			((MapModel) controller.getModel()).setLayerNames(map);
		}
	}
	
	@Override
	protected void onError(MapController controller, Exception e) {
		e.printStackTrace();
		controller.getModel().notifyNetworkError();
	}

}