package org.pocketcampus.plugin.map;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.MyLocationOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.TilesOverlay;
import org.pocketcampus.R;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.core.communication.ServerRequest;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.core.ui.ActionBar;
import org.pocketcampus.plugin.map.elements.MapElement;
import org.pocketcampus.plugin.map.elements.MapElementsList;
import org.pocketcampus.plugin.map.elements.MapPathOverlay;
import org.pocketcampus.plugin.map.elements.Search;
import org.pocketcampus.plugin.map.ui.LayerSelector;
import org.pocketcampus.shared.plugin.map.MapElementBean;
import org.pocketcampus.shared.plugin.map.MapLayerBean;
import org.pocketcampus.shared.plugin.map.Path;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

/**
 * PluginBase class for the Map plugin 
 * 
 * @status WIP
 * 
 * @author Jonas, Johan
 *
 */
public class MapPlugin extends PluginBase {

	private MapView mapView_;
	private MapController mapController_;
	private List<MapElementsList> layers_;
	private List<MapElementsList> selectedLayers_;
	private MyLocationOverlay myLocationOverlay_;
	private MapPathOverlay mapPathOverlay_;
	
	private ProgressDialog progressDialog_;
	private ActionBar actionBar_;
	
	
	/**
	 * Number of parallel threads being executed.
	 * When the counter is > 0, it means that at least one thread
	 * is doing some work on background and then the progress bar
	 * is shown. When the progressCount is 0, no progress bar is displayed.
	 */
	private int progressCount_ = 0;
	
	
	/**
	 * Overlays which are unconditionally displayed
	 * like the EPFL Tiles Overlay
	 */
	private List<Overlay> constantOverlays_;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.map_main);

		setupActionBar(true);
		actionBar_ = (ActionBar) findViewById(R.id.actionbar);

		setupMapView();

		layers_ = new ArrayList<MapElementsList>();
		selectedLayers_ = new ArrayList<MapElementsList>();
	}
	
	/**
	 * The background is provided by the default tile source.
	 * We add a TileOverlay above the background (for example
	 * the map of the epfl campus).
	 */
	private void setupMapView() {

		mapView_ = (MapView) findViewById(R.id.mapview);
        constantOverlays_ = new ArrayList<Overlay>();
        
		mapController_ = mapView_.getController();
		
		mapView_.setMultiTouchControls(true);
		mapView_.setBuiltInZoomControls(true);
		
		
		// Add EPFL tiles layer
		ITileSource epflTile = new EpflTileSource();
		MapTileProviderBasic mProvider = new MapTileProviderBasic(getApplicationContext());
        mProvider.setTileSource(epflTile);
        TilesOverlay mTilesOverlay = new TilesOverlay(mProvider, this.getBaseContext());
        constantOverlays_.add(mTilesOverlay);
        
        // Following the user
        myLocationOverlay_ = new MyLocationOverlay(this, mapView_);
        myLocationOverlay_.enableMyLocation();
        //myLocationOverlay_.enableFollowLocation();
        constantOverlays_.add(myLocationOverlay_);
        
        // Path overlay
        mapPathOverlay_ = new MapPathOverlay(Color.BLUE, this);
        constantOverlays_.add(mapPathOverlay_);
	}
	
	/**
	 * Displays a not-cancelable progress dialog with the specific message.
	 * @param message
	 */
	private void showProgressDialog(String message) {
		progressDialog_ = new ProgressDialog(this);
		progressDialog_.setTitle(getResources().getString(R.string.please_wait));
		progressDialog_.setMessage(message);
		progressDialog_.setCancelable(false);
		progressDialog_.show();
	}
	
	/**
	 * Displays the message (toast)
	 * @param message
	 */
	private void showToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * Dismisses the progress dialog (displayed using showProgressDialog)
	 */
	private void dismissProgressDialog() {
		if(progressDialog_ != null) {
			try {
				progressDialog_.dismiss();
			} catch (Exception e) {
				Log.e("MapPlugin", e.toString());
			}
		}
	}
	
	/**
	 * Increments the progressCounter. It displays the progress bar
	 * of the action bar. It allows several parallel threads doing background
	 * work.
	 */
	private synchronized void incrementProgressCounter() {
		progressCount_++;
		actionBar_.setProgressBarVisibility(View.VISIBLE);
	}
	
	/**
	 * Decrements the progressCounter. Called when a thread has finished
	 * doing some background work.
	 */
	private synchronized void decrementProgressCounter() {
		progressCount_--;
		if(progressCount_ < 0) { //Should never happen!
			Log.e("MapPlugin", "ERROR progresscount is negative!");
		}
		
		if(progressCount_ <= 0) {
			actionBar_.setProgressBarVisibility(View.GONE);
		}
	}

	@Override
	protected void onResume() {
		myLocationOverlay_.enableMyLocation();
		myLocationOverlay_.enableCompass();
		super.onResume();
	}

	@Override
	protected void onStart() {
		super.onStart();
		
		//Center the view at epfl
		//It is important to set the zoom before the position (bug of osmdroid)
		mapController_.setZoom(16);
		GeoPoint epflPoint = new GeoPoint(46519732, 6566734);
		mapController_.setCenter(epflPoint);
		
		updateOverlays();
	}

	/**
	 * Downloads the list of available layers
	 */
	private void loadLayersFromServer() {
		class LayersRequest extends ServerRequest {
			@Override
			protected void onPostExecute(String result) {
				
				if(result == null) { //an error happened
					dismissProgressDialog();
					showToast(getResources().getString(R.string.server_connection_error));
					return;
				}
				Log.d("SERVER", "response: " + result);
				
				//Deserializes the response
				Gson gson = new Gson();
				Type mapLayersType = new TypeToken<List<MapLayerBean>>(){}.getType();
				List<MapLayerBean> layers = new ArrayList<MapLayerBean>();
				try {
					layers = gson.fromJson(result, mapLayersType);
				} catch (JsonSyntaxException e) {
					dismissProgressDialog();
					showToast(getResources().getString(R.string.unexpected_response));
					return;
				}
				if(layers == null) {
					dismissProgressDialog();
					showToast(getResources().getString(R.string.server_connection_error));
					return;
				}
				
				layers_ = new ArrayList<MapElementsList>(layers.size());
				for(MapLayerBean mlb : layers) {
					if(mlb.isDisplayable())
						layers_.add(new MapElementsList(mlb));
				}
				dismissProgressDialog();
				layerSelector();
			}
		}
		//request of the layers
		getRequestHandler().execute(new LayersRequest(), "getLayers", (RequestParameters)null);
	}


	@Override
	protected void onPause() {
		myLocationOverlay_.disableMyLocation();
		myLocationOverlay_.disableCompass();
		super.onPause();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.map, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.map_menu_layers_button:
			showProgressDialog("Loading layers...");
			loadLayersFromServer();
			return true;
			
		case R.id.map_my_position:
			centerOnPosition();
			return true;
			
		case R.id.map_path:
			showDirections();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	/**
	 * Launch the dialog that allows to select the different layers
	 */
	private void layerSelector() {
		final LayerSelector l = new LayerSelector(this, layers_, selectedLayers_);
		
		// Show the dialog, using a callback to the the selected layers back
		l.selectLayers(new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				setSelectedLayers(l.getSelectedLayers());
			}
		});
		
	}
	
	private void centerOnPosition() {
        myLocationOverlay_.enableFollowLocation();
	}
	
	private void showDirections() {

		MapElement start = new MapElement("Depart", "", new GeoPoint(0, 0));
		MapElement end = new MapElement("Arrivee", "", new GeoPoint(1, 1));
		
		Path path = Search.searchPathBetween(start, end, false);
		
		/*
		Path path = new Path();
		ArrayList<GeoPoint> list = path.getGeoPointList();
		list.add(new GeoPoint(46.51811752656941, 6.568092385190248));
		list.add(new GeoPoint(46.52011208093279, 6.565411761843846));
		list.add(new GeoPoint(46.51854536111413, 6.563350147693381));
		*/
		
		mapPathOverlay_.setPath(path);
	}
	
	/**
	 * Set the selected layers
	 * @param selectedLayers
	 */
	private void setSelectedLayers(ArrayList<MapElementsList> selectedLayers) {
		this.selectedLayers_ = selectedLayers;
		
		updateOverlays();
	}

	@Override
	public PluginInfo getPluginInfo() {
		return new MapInfo();
	}

	@Override
	public PluginPreference getPluginPreference() {
		return null;
	}
	
	
	/**
	 * Displays all selected overlay items (from layers).
	 */
	private void updateOverlays() {
		// First we remove all the overlays and then add the constant ones
		mapView_.getOverlays().clear();
		for(Overlay over : constantOverlays_) {
			mapView_.getOverlays().add(over);
		}
		// Display the selected layers
		for(MapElementsList layer : selectedLayers_) {
			if(layer.size() == 0) {
				populateLayer(layer);
			} else { //the items have already been fetched
				ItemizedOverlay<OverlayItem> aOverlay = new ItemizedIconOverlay<OverlayItem>(layer, null, new DefaultResourceProxyImpl(getApplicationContext()));
				mapView_.getOverlays().add(aOverlay);
				mapView_.invalidate();
			}
		}
	}
	
	/**
	 * Adds corresponding MapElements into the list.
	 * @param layer the layer (= list of items) where the item will be added
	 */
	private void populateLayer(final MapElementsList layer) {
		if(layer == null) {
			return;
		}
		
		/* The idea is to add MapElements (=item) into the MapElementsList.
		 * The data comes from the cache (local file?) or from the server
		 */
		
		incrementProgressCounter();
		class ItemsRequest extends ServerRequest {
			@Override
			protected void onPostExecute(String result) {
				if(result == null) {
					decrementProgressCounter();
					showToast(getResources().getString(R.string.server_connection_error));
					return;
				}
				Log.d("SERVER", "response: " + result);
				
				//Deserializes the response
				Gson gson = new Gson();
				Type mapElementType = new TypeToken<List<MapElementBean>>(){}.getType();
				List<MapElementBean> items = new ArrayList<MapElementBean>();
						
				try {
					items = gson.fromJson(result, mapElementType);
				} catch (JsonSyntaxException e) {
					decrementProgressCounter();
					return;
				}
				if(items == null) {
					decrementProgressCounter();
					return;
				}
				
				for(MapElementBean meb : items) {
					layer.add(new MapElement(meb));
				}
				
				ItemizedOverlay<OverlayItem> aOverlay = new ItemizedIconOverlay<OverlayItem>(layer, new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
					@Override
					public boolean onItemLongPress(int arg0, OverlayItem arg1) {
						return false;
					}
					
					@Override
					public boolean onItemSingleTapUp(int arg0, OverlayItem arg1) {
						showToast(arg1.mTitle);
						return true;
					}
				}, new DefaultResourceProxyImpl(getApplicationContext()));
				
				mapView_.getOverlays().add(aOverlay);
				mapView_.invalidate();
				decrementProgressCounter();
			}
		}
		RequestParameters param = new RequestParameters();
		param.addParameter("layer_id", layer.getLayerId() + "");
		getRequestHandler().execute(new ItemsRequest(), "getItems", param);
	}
}
