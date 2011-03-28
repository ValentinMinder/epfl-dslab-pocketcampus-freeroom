package org.pocketcampus.plugin.map;

import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.pocketcampus.R;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;

import android.os.Bundle;

public class MapPlugin extends PluginBase {

	private MapView mapView_;
	private MapController mapController_;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.map_main);

		setupActionBar(true);

		setupMapView();
	}
	
	private void setupMapView() {

		mapView_ = (MapView) findViewById(R.id.mapview);
        
		mapController_ = mapView_.getController();
		
		ITileSource epflTile = new EpflTileSource();
		
		mapView_.setTileSource(epflTile);
		mapView_.setMultiTouchControls(true);
		mapView_.setBuiltInZoomControls(true);
	}

	@Override
	protected void onStart() {
		super.onStart();
		//center the view at epfl
		//important to set the zoom before the position (bug of osmdroid)
		mapController_.setZoom(16);
		GeoPoint epflPoint = new GeoPoint(46519732, 6566734);
		mapController_.setCenter(epflPoint);
		
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
