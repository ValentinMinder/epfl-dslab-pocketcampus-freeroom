package org.pocketcampus.plugin.map;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.tileprovider.tilesource.*;
import org.osmdroid.ResourceProxy;
import org.pocketcampus.R;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;

import android.os.Bundle;
import android.view.Window;

public class MapPlugin extends PluginBase {

	private MapView mapView_;
	private MapController mapController_;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.map_main);
		
		/*ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.setTitle(getResources().getString(R.string.app_name));
		actionBar.addAction(new ActionBar.IntentAction(this, MainscreenPlugin.createIntent(this), R.drawable.mini_home));*/
		
		mapView_ = (MapView) findViewById(R.id.mapview);
        
		mapController_ = mapView_.getController();
		
		
		ITileSource epflTile = new EpflTileSource("Epfl1", ResourceProxy.string.osmarender, 16, 19, 256, ".png", "http://plan-epfl-tile2.epfl.ch/batiments1/");
		
		mapView_.setTileSource(epflTile);
		mapView_.setMultiTouchControls(true);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		GeoPoint epflPoint = new GeoPoint(46519732, 6566734);
		mapController_.setCenter(epflPoint);
		mapController_.setZoom(16);
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
