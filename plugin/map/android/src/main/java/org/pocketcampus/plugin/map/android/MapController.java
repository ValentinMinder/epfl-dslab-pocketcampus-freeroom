package org.pocketcampus.plugin.map.android;

import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginModel;
import org.pocketcampus.plugin.map.android.req.GetLayersRequest;
import org.pocketcampus.plugin.map.android.req.SearchRequest;
import org.pocketcampus.plugin.map.shared.MapService.Client;
import org.pocketcampus.plugin.map.shared.MapService.Iface;

import java.util.*;

/**
 * Map's main controller.
 * @author Amer Chamseddine <amer@pocketcampus.org>
 *
 */
public class MapController extends PluginController {
	private MapModel mModel;
	private String mPluginName = "map";
	private Iface client;

	private Map<String, String> epflFloors = new HashMap<String, String>();
	private Comparator<String> floorKeyComparator;

	@Override
	public void onCreate() {
		mModel = new MapModel();
		client = (Iface) getClient(new Client.Factory(), mPluginName);

		final List<String> floorKeyOrder = Arrays.asList("all", "8", "7", "6", "5", "4", "3", "2", "1", "0", "-1", "-2", "-3", "-4", "");
		floorKeyComparator = new Comparator<String>() {

			public int compare(String arg0, String arg1) {
				Integer i0 = floorKeyOrder.indexOf(arg0);
				Integer i1 = floorKeyOrder.indexOf(arg1);
				if(i0 == -1 || i1 == -1) {
					return arg0.compareTo(arg1);
				}
				return i0.compareTo(i1);
			}
		};

		epflFloors.clear();
		for(String s : floorKeyOrder) {
			int res = getResources().getIdentifier("epfl_floor_" + s.replace("-", "m"), "string", getPackageName());
			epflFloors.put(s, getString(res));
		}
	}
	
	@Override
	public PluginModel getModel() {
		return mModel;
	}
	
	public void getLayers() {
		new GetLayersRequest().start(this, client, null);
	}

	public void search(MapMainView context, String query) {
		new SearchRequest(context).start(this, client, query);
	}



	public Map<String, String> getEpflFloors() {
		return epflFloors;
	}

	public Comparator<String> getFloorKeyComparator() {
		return floorKeyComparator;
	}
}













