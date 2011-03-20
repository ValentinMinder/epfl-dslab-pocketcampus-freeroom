package org.pocketcampus.plugin.map;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;

import android.os.Bundle;
import android.view.Window;

public class MapPlugin extends PluginBase {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		MapView mapView = new MapView(this, 256);
        setContentView(mapView);
        
		MapController controller = mapView.getController();
		GeoPoint point = new GeoPoint(46519732, 6566734);
		controller.setCenter(point);
		controller.setZoom(10);
	}
	
	@Override
	public PluginInfo getPluginInfo() {
		return new MapInfo();
	}

	@Override
	public PluginPreference getPluginPreference() {
		return null;
	}

}
