package org.pocketcampus.plugin.map.android;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.MyLocationOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.TilesOverlay;
import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginInfo;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.plugin.map.android.cache.ILayersCacheCallback;
import org.pocketcampus.plugin.map.android.cache.LayersCache;
import org.pocketcampus.plugin.map.android.elements.MapElement;
import org.pocketcampus.plugin.map.android.elements.MapElementsList;
import org.pocketcampus.plugin.map.android.elements.MapPathOverlay;
import org.pocketcampus.plugin.map.android.ui.LevelBar;
import org.pocketcampus.plugin.map.android.ui.OnLevelBarChangeListener;
import org.pocketcampus.plugin.map.shared.Position;

import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;

/**
 * PluginBase class for the Map plugin 
 * 
 * @status WIP
 * 
 * @author Florian <florian.laurent@epfl.ch>
 * @author Jonas <jonas.schmid@epfl.ch>
 *
 */
public class MapPlugin extends PluginView {
	public static final String ITEM_GO_URL = "go_url:";

	// Used for the location
	private static final float MAX_ACCURACY_FROM_DIRECTIONS = 100;
	private static final long LAYERS_REFRESH_TIMEOUT = 30000;
	private static Position CAMPUS_CENTER_P;
	private static GeoPoint CAMPUS_CENTER_G;
	
	private static boolean DEBUG = false;

	// Map UI
	private MapView mapView_;
	private MapController mapController_;
	private MyLocationOverlay myLocationOverlay_;
	private MyLocationOverlay googleLocationOverlay_;
	private MapPathOverlay mapPathOverlay_;
	private ConcurrentHashMap<MapElementsList, ItemizedIconOverlay<MapElement>> cachedOverlays_;
	private ConcurrentHashMap<MapElementsList, Long> lastRefreshedOverlays_;

	private OnItemGestureListener<MapElement> overlayClickHandler_;

	// UI
	private ActionBar actionBar_;

	/**
	 * Number of parallel threads being executed.
	 * When the counter is > 0, it means that at least one thread
	 * is doing some work on background and then the progress bar
	 * is shown. When the progressCount is 0, no progress bar is displayed.
	 */
	private int progressCount_ = 0;

	/**
	 * Overlays which are unconditionally displayed,
	 * like the campus Tiles Overlay, or the user position
	 */
	private List<Overlay> constantOverlays_;
	
	/**
	 * Overlays which are temporary, like the search result.
	 */
	private List<Overlay> temporaryOverlays_;

	// List of all and displayed overlays
	private List<MapElementsList> allLayers_;
	private List<MapElementsList> selectedLayers_;
	
	// Cache the overlay icons
	private HashMap<String, Drawable> icons = new HashMap<String, Drawable>();
	
	// Used to save the layers to a file
	private LayersCache layersCache_;

	// Handler used to refresh the overlays 
	private Handler overlaysHandler_ = new Handler();
	
	// Variables used when the plugin is launched by another plugin.
	// to remember which item to show once they are loaded.
	private int intentLayerId_;
	private int intentItemId_;

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
//		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_main);
//		actionBar_ = (ActionBar) findViewById(R.id.actionbar);
//		setupActionBar(true);

//		Tracker.getInstance().trackPageView("map/home");

		initVariables();
		
		// Setup view
		setupMapView();

		// Download the available layers
//		incrementProgressCounter();
//		RequestParameters params = new RequestParameters();
//		getRequestHandler().execute(new LayersRequest(), "getLayers", params);
//		
//		handleSearchIntent(getIntent().getExtras());
	}

	private void initVariables() {
		// The layers are not know yet
		constantOverlays_ = new ArrayList<Overlay>();
		temporaryOverlays_ = new ArrayList<Overlay>();
		allLayers_ = new ArrayList<MapElementsList>();
		selectedLayers_ = new ArrayList<MapElementsList>();
		cachedOverlays_ = new ConcurrentHashMap<MapElementsList, ItemizedIconOverlay<MapElement>>();
		lastRefreshedOverlays_ = new ConcurrentHashMap<MapElementsList, Long>();

		//overlayClickHandler_ = new OverlayClickHandler(this);

		// Get the campus coordinates
		double lat = Double.parseDouble(getResources().getString(R.string.map_campus_latitude));
		double lon = Double.parseDouble(getResources().getString(R.string.map_campus_longitude));
		double alt = Double.parseDouble(getResources().getString(R.string.map_campus_altitude));
		
		CAMPUS_CENTER_P = new Position(lat, lon, alt);
		CAMPUS_CENTER_G = new GeoPoint(CAMPUS_CENTER_P.getLatitude(), CAMPUS_CENTER_P.getLongitude(), CAMPUS_CENTER_P.getAltitude());
		
		layersCache_ = new LayersCache(this);
		
		// Get extras from the intent
//		Bundle extras = getIntent().getExtras();
//		if(extras != null) {
//			intentLayerId_ = extras.getString("MapLayer");
//			intentItemId_ = extras.getInt("MapItem");
//		}
	}

//	@Override
//	protected void setupActionBar(boolean addHomeButton) {
//
//		actionBar_.addAction(new Action() {
//
//			@Override
//			public void performAction(View view) {
//				updateOverlays(true);
//
//				Tracker.getInstance().trackPageView("map/manualRefresh");
//			}
//
//			@Override
//			public int getDrawable() {
//				return R.drawable.refresh;
//			}
//		});
//
//		super.setupActionBar(addHomeButton);
//	}

	/**
	 * Change the level of the map.
	 * @param level the new level
	 */
	private void changeLevel(int level) {
		MapTileProviderBasic provider = new MapTileProviderBasic(getApplicationContext());

		ITileSource tileSource = getTileSource(level);

		provider.setTileSource(tileSource);
		
		TilesOverlay tilesOverlay = new TilesOverlay(provider, getBaseContext());
		tilesOverlay.setLoadingBackgroundColor(Color.TRANSPARENT);
		constantOverlays_.remove(0);
		constantOverlays_.add(0, tilesOverlay);
		updateOverlays(false);
		mapView_.postInvalidate();
//		Tracker.getInstance().trackPageView("map/changeLevel" + level);
	}
	
	/**
	 * Returns the tile source to be displayed
	 * @return the tile source
	 */
	private ITileSource getTileSource(int level) {
		ITileSource tileSource;
		if(getResources().getBoolean(R.bool.map_tilesource_is_epfl)) {
			tileSource = new EpflTileSource(level + "");
		} else {
			String name = getResources().getString(R.string.map_tilesource_name);
			int zoomMin = getResources().getInteger(R.integer.map_tilesource_zoom_min);
			int zoomMax = getResources().getInteger(R.integer.map_tilesource_zoom_max);	
			int tileSize = getResources().getInteger(R.integer.map_tilesource_tile_size);
			String ext = getResources().getString(R.string.map_tilesource_filename_ending);
			String[] urls = getResources().getStringArray(R.array.map_tilesource_urls);
			tileSource = new XYTileSource(name, ResourceProxy.string.mapnik, zoomMin, zoomMax, tileSize, ext, urls);
		}
		return tileSource;
	}

	/**
	 * Handle the eventual extras of the intent.
	 * For example, it can show a map element
	 * @param extras the bundle containing the extras
	 * @return Whether it handled the intent or not
	 */
	private boolean handleSearchIntent(Bundle extras) {

		if(extras == null) {
			return false;
		}
		
//		if(extras.containsKey("MapElement")) {
//			MapElementBean meb = (MapElementBean) extras.getSerializable("MapElement");
//			GeoPoint gp = new GeoPoint(meb.getLatitude(), meb.getLongitude());
//			MapElement overItem = new MapElement(meb.getTitle(), meb.getDescription(), gp);
//			List<MapElement> overItems = new ArrayList<MapElement>(1);
//			overItems.add(overItem);
//			Drawable searchMarker = this.getResources().getDrawable(R.drawable.map_marker_search);
//			ItemizedOverlay<MapElement> aOverlay = new ItemizedIconOverlay<MapElement>(overItems, searchMarker, overlayClickHandler_, new DefaultResourceProxyImpl(getApplicationContext()));
//			temporaryOverlays_.add(aOverlay);
//			centerOnPoint(gp);
//			return true;
//		}
		
		return false;
	}

	/**
	 * The background is provided by the default tile source.
	 * We add a TileOverlay above the background (for example
	 * the map of the campus).
	 */
	private void setupMapView() {

		mapView_ = (MapView) findViewById(R.id.mapview);
		mapView_.setMultiTouchControls(true);
		mapView_.setBuiltInZoomControls(true);
		/* XXX This is done to allow zoom up to 22 (for epfl) but the tiles will not load because
		 * the mapnik zoom is between 0 and 18 */
		ITileSource aTileSource =  new XYTileSource("Mapnik",
                ResourceProxy.string.mapnik, 0, 22, 256, ".png", "http://tile.openstreetmap.org/");
		mapView_.setTileSource(aTileSource);
		mapController_ = mapView_.getController();

		// Display the level bar if needed
		if(getResources().getBoolean(R.bool.map_has_levels)) {
			SeekBar seekBar = (SeekBar) findViewById(R.id.map_level_bar);
			int max = getResources().getInteger(R.integer.map_level_max);
			int min = getResources().getInteger(R.integer.map_level_min);
			final TextView levelTextView = (TextView) findViewById(R.id.map_level_textview);
			new LevelBar(seekBar, new OnLevelBarChangeListener() {
				@Override
				public void onLevelChanged(int level) {
					levelTextView.setVisibility(View.INVISIBLE);
					changeLevel(level);
					String slevel = getResources().getString(R.string.map_level);
					Toast.makeText(getApplicationContext(), slevel + " " + level, Toast.LENGTH_SHORT).show();
				}
				@Override
				public void onLevelChanging(int level) {
					levelTextView.setVisibility(View.VISIBLE);
					levelTextView.setText(level + "");
					
				}
			}, max, min, max);
		}

		// Add Campus tiles layer
		int level = getResources().getInteger(R.integer.map_level_default);
		ITileSource campusTile = getTileSource(level);
		MapTileProviderBasic provider = new MapTileProviderBasic(getApplicationContext());
		provider.setTileSource(campusTile);
		TilesOverlay tilesOverlay = new TilesOverlay(provider, this.getBaseContext());
		tilesOverlay.setLoadingBackgroundColor(Color.TRANSPARENT);
		constantOverlays_.add(0, tilesOverlay);

		// Following the user
		// myLocationOverlay_ = new MyLocationOverlay(this, mapView_);
//		myLocationOverlay_ = new HybridPositioningOverlay(this, mapView_);
//		constantOverlays_.add(myLocationOverlay_);
//		if(DEBUG) {
//			googleLocationOverlay_ = new MyLocationOverlay(this, mapView_);
//			constantOverlays_.add(googleLocationOverlay_);
//		}

		// Path overlay
		mapPathOverlay_ = new MapPathOverlay(Color.RED, 3.0f, this);
		constantOverlays_.add(mapPathOverlay_);

		// Center map
		centerOnCampus();
	}

	/**
	 * Re-enable the location service and the layers refresh
	 */
	@Override
	protected void onResume() {

//		if(myLocationOverlay_.isFollowLocationEnabled()) {
//			myLocationOverlay_.enableMyLocation();
//			if(DEBUG) {
//				googleLocationOverlay_.enableMyLocation();
//			}
//		}
//		
//		overlaysHandler_.removeCallbacks(overlaysRefreshTicker_);
//		overlaysHandler_.post(overlaysRefreshTicker_);

		super.onResume();
	}

	/**
	 * Disable the location service and the layers refresh
	 */
	@Override
	protected void onPause() {
//		myLocationOverlay_.disableMyLocation();
//		if(DEBUG) {
//			googleLocationOverlay_.disableMyLocation();
//		}
//		
//		overlaysHandler_.removeCallbacks(overlaysRefreshTicker_);
//		
		super.onPause();
	}
	
//	@Override
//	protected void onNewIntent(Intent intent) {
//		super.onNewIntent(intent);
//		handleSearchIntent(intent.getExtras());
//	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	/**
	 * Handle the menu
	 */
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuInflater inflater = getMenuInflater();
//		inflater.inflate(R.menu.map, menu);
//
//		return true;
//	}

//	@Override
//	public boolean onPrepareOptionsMenu(Menu menu) {
//		// Do we display a path?
//		menu.findItem(R.id.map_clear_path).setVisible(mapPathOverlay_.isShowingPath());
//
//		// Do we already have the available layers?
//		menu.findItem(R.id.map_menu_layers_button).setEnabled(allLayers_ != null && allLayers_.size() > 0);
//		
//		// Change the text if we follow the user or not
//		MenuItem follow = menu.findItem(R.id.map_my_position);
//		if(myLocationOverlay_.isFollowLocationEnabled()) {
//			follow.setTitle(R.string.map_menu_my_position_off);
//		} else {
//			follow.setTitle(R.string.map_menu_my_position_on);
//		}
//
//		return true;
//	}

	/**
	 * Handle the menu
	 */
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// Handle item selection
//		switch (item.getItemId()) {
//
//		// Show a layer selection
//		case R.id.map_menu_layers_button:
//			selectLayers();
//
////			Tracker.getInstance().trackPageView("map/menu/getLayers");
//			return true;
//
//			// Enable the user following
//		case R.id.map_my_position:
//			if(!myLocationOverlay_.isMyLocationEnabled()) {
//				Toast.makeText(this, getResources().getString(R.string.map_compute_position), Toast.LENGTH_LONG).show();
//			}
//			toggleCenterOnUserPosition();
//
////			Tracker.getInstance().trackPageView("map/menu/togglPosition");
//			return true;
//
//			// Enable the user following
//		case R.id.map_campus_position:
//			centerOnCampus();
//
////			Tracker.getInstance().trackPageView("map/menu/centerOnCampus");
//			return true;
//
//			// Shows the search dialog
//		case R.id.map_search:
//			onSearchRequested();
//
////			Tracker.getInstance().trackPageView("map/menu/search"); 
//			return true;
//			
//		case R.id.map_clear_path:
//			clearPath();
//
////			Tracker.getInstance().trackPageView("map/menu/clearPath");
//			return true;
//			
//		case R.id.map_menu_clear_layers_button:
//			temporaryOverlays_.clear();
//			setSelectedLayers(new ArrayList<MapElementsList>());
//			
////			Tracker.getInstance().trackPageView("map/menu/clearLayers");
//			return true;
//
//		default:
//			return super.onOptionsItemSelected(item);
//		}
//	}

	/**
	 * Show the list of available layers
	 */
	private void selectLayers() {

		// If we already have a cache of the layers
		if(allLayers_ != null && allLayers_.size() > 0) {
			layerSelector();
		}
		// else wait, it will come ;)
	}

	/**
	 * Launch the dialog that allows to select the different layers
	 */
	private void layerSelector() {
//		final LayerSelector l = new LayerSelector(this, allLayers_, selectedLayers_);
//
//		// Show the dialog, using a callback to the the selected layers back
//		l.selectLayers(new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				setSelectedLayers(l.getSelectedLayers());
//			}
//		});

	}

	/**
	 * Enable the location and center the map on the user
	 */
	private void toggleCenterOnUserPosition() {
		if(myLocationOverlay_.isFollowLocationEnabled()) {
			myLocationOverlay_.disableMyLocation();
			myLocationOverlay_.disableFollowLocation();
			if(DEBUG)
				googleLocationOverlay_.disableMyLocation();
		} else {
			myLocationOverlay_.enableMyLocation();
			myLocationOverlay_.enableFollowLocation();
			if(DEBUG)
				googleLocationOverlay_.enableMyLocation();
		}
	}

	/**
	 * Center the map on campus
	 */
	private void centerOnCampus() {
		centerOnPoint(CAMPUS_CENTER_G);
	}

	/**
	 * Center on a point on the map
	 * @param point Where to center the map
	 */
	public void centerOnPoint(GeoPoint point) {
//		myLocationOverlay_.disableFollowLocation();
//
//		mapController_.setZoom(getResources().getInteger(R.integer.map_zoom_level)); 
//		mapController_.setCenter(point);
	}

	/**
	 * Clear the displayed path
	 */
	private void clearPath() {
		mapPathOverlay_.clearPath();
	}

	/**
	 * Show the directions layer to a certain POI 
	 *
	 * @param endPos Position where to go
	 */
	public void showDirectionsFromHereToPosition(final Position endPos) {
		
//		if(!myLocationOverlay_.isFollowLocationEnabled()) {
//			toggleCenterOnUserPosition();
//		}
//
//		// Clear the path if there was an old one
//		mapPathOverlay_.clearPath();
//		mapView_.invalidate();
//
//		myLocationOverlay_.runOnFirstFix(new Runnable() {
//
//			@Override
//			public void run() {
//				Location fix = myLocationOverlay_.getLastFix();
//				showDirectionFromTo(fix, endPos);
//			}
//		});

//		Tracker.getInstance().trackPageView("map/showDirections?d=" + endPos.toString());
	}


	/**
	 * Show direction from the given fix to a position
	 * @param fix Fix from the GPS
	 * @param to where to go
	 */
//	private void showDirectionFromTo(Location fix, Position to) {
//		
//		// Check if the user is located and has a good accuracy
//		if(fix.hasAccuracy() && fix.getAccuracy() > MAX_ACCURACY_FROM_DIRECTIONS) {
//			Notification.showToast(getApplicationContext(), R.string.map_directions_not_accurate);
//			return;
//		}
//
//		// Check if the user is on campus
//		if(!PositionUtil.isLocationOnCampus(this, fix)) {
//			Notification.showToast(getApplicationContext(), R.string.map_directions_not_on_campus);
//			return;
//		}
//
//		// Parameters 
//		RequestParameters params = new RequestParameters();
//		params.addParameter("startLatitude", Double.toString(fix.getLatitude()));
//		params.addParameter("startLongitude", Double.toString(fix.getLongitude()));
//		params.addParameter("endLatitude", Double.toString(to.getLatitude()));
//		params.addParameter("endLongitude", Double.toString(to.getLongitude()));
//
//		//request of the layers
////		incrementProgressCounter();
//		getRequestHandler().execute(new DirectionsRequest(), "routing", params);
//	}

	/**
	 * Set the selected layers and update the overlays
	 * @param selectedLayers
	 */
	private void setSelectedLayers(ArrayList<MapElementsList> selectedLayers) {
		this.selectedLayers_ = selectedLayers;

		updateOverlays(false);
		
		layersCache_.saveSelectedLayersToFile(this.selectedLayers_);

		// Track
		StringBuffer selected = new StringBuffer("?layers=");
		for(MapElementsList l : selectedLayers) {
			selected.append(l.getLayerTitle());
			selected.append(',');
		}

//		Tracker.getInstance().trackPageView("map/selectedLayers" + selected);
	}

	/**
	 * Displays all selected overlay items (from layers).
	 * 
	 * @param forceRefresh Whether to check is the cache is still valid or to force refresh.
	 */
	private void updateOverlays(boolean forceRefresh) {
		// First we remove all the overlays and then add the constant ones
		mapView_.getOverlays().clear();
		for(Overlay over : constantOverlays_) {
			mapView_.getOverlays().add(over);
		}

		// Display the selected layers
		for(MapElementsList layer : selectedLayers_) {
			ItemizedIconOverlay<MapElement> aOverlay = cachedOverlays_.get(layer);

			// The overlay already exists
			if(aOverlay != null) {
				mapView_.getOverlays().add(aOverlay);
			}

			// The overlay does not exist, or is outdated 
			// If outdated, we redownload the new items, but keep the old ones on the screen while downloading
			if(aOverlay == null || isLayerOutdated(layer) || forceRefresh) {
				populateLayer(layer);
			}

		}
		
		for(Overlay over : temporaryOverlays_) {
			mapView_.getOverlays().add(over);
		}

		mapView_.invalidate();
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
		 * The data comes from the cache (local file) or from the server
		 */

//		incrementProgressCounter();
//		RequestParameters param = new RequestParameters();
//		param.addParameter("layer_id", layer.getLayerId());
//		param.addParameter("token", getAuthToken());
//		getRequestHandler().execute(new ItemsRequest(layer), "getItems", param);
	}

	private boolean isLayerOutdated(MapElementsList layer) {

		long cacheTime = layer.getCacheTimeInSeconds();
		if(cacheTime < 0) {
			return false;
		}

		// Get the last time we got the data, will be 0 if we never did
		long lastRefresh = lastRefreshedOverlays_.get(layer);

		// seconds to milliseconds
		cacheTime *= 1000;

		return lastRefresh + cacheTime < System.currentTimeMillis();
	}
	
	/**
	 * Get the AuthToken to give it with the requests
	 * @return
	 */
//	private String getAuthToken() {
//		AuthToken t = AuthenticationPlugin.getAuthToken(this);
//		return new Gson().toJson(t);
//	}

	/**
	 * Request class for the directions
	 */
//	class DirectionsRequest extends DataRequest {
//		
//		@Override
//		protected void doInUiThread(String result) {
//
//			decrementProgressCounter();
//
//			// Deserializes the response
//			Gson gson = new Gson();
//			List<Position> path = null;
//			Type t = new TypeToken<List<Position>>(){}.getType();
//
//			try {
//
//				Log.d(this.getClass().toString(), "Route :");
//				Log.d(this.getClass().toString(), result);
//				
//				path = gson.fromJson(result, t);
//				mapPathOverlay_.setList(path);
//				mapView_.invalidate();
//			} catch(Exception e) {
//				Notification.showToast(getApplicationContext(), R.string.map_directions_not_found);
//			}
//
//		}
//	}

	/**
	 * Called when the map plugin is launched from another plugin. 
	 * We have to check that the layer asked by the other plugin is selected,
	 * otherwise, we select it.
	 */
	private void checkSelectedLayersFromIntent() {
		
		// Not comming from another plugin
//		if(intentLayerId_ == null) {
//			return;
//		}
		
		// Is the layer already selected?
		for(MapElementsList mel : selectedLayers_) {
			if(mel.getLayerId() == intentLayerId_) {
				return;
			}
		}
		
		// Find the corresponding layer from all the available layers and add it to the selection
		for(MapElementsList mel : allLayers_) {
			if(mel.getLayerId() == intentLayerId_) {
				selectedLayers_.add(mel);
				return;
			}
		}
		
		// If the layer was not found... bad luck, the other plugin made something wrong
		//Notification.showToast(this, R.string.map_layer_not_found);
	}
	
	/**
	 * Used to retreive the layers from the server
	 */
//	class LayersRequest extends DataRequest {
//		
//		@Override
//		protected int expirationDelay() {
//			return 10;
//		}
//
//		@Override
//		protected void onCancelled() {
//			decrementProgressCounter();
//			Notification.showToast(getApplicationContext(), R.string.server_connection_error);
//		}
//
//		@Override
//		protected void doInBackgroundThread(String result) {
//
//			if(result == null) {
//				return;
//			}
//
//			// Deserializes the response
//			Type mapLayersType = new TypeToken<List<MapLayerBean>>(){}.getType();
//			List<MapLayerBean> layers = new ArrayList<MapLayerBean>();
//			try {
//				layers = Json.fromJson(result, mapLayersType);
//			} catch (JsonException e) {
//				Notification.showToast(getApplicationContext(), R.string.server_connection_error);
//			}
//			if(layers == null) {
//				Notification.showToast(getApplicationContext(), R.string.server_connection_error);
//				return;
//			}
//
//			allLayers_ = new ArrayList<MapElementsList>(layers.size());
//			for(MapLayerBean mlb : layers) {
//				//if(mlb.isDisplayable()) {
//					allLayers_.add(new MapElementsList(mlb));
//				//}
//			}
//			
//			layersCache_.loadSelectedLayersFromFile(allLayers_, new ILayersCacheCallback() {
//				@Override
//				public void onLayersLoadedFromFile(List<MapElementsList> selected) {
//					selectedLayers_ = selected;
//					checkSelectedLayersFromIntent();
//					updateOverlays(false);
//				}
//			});
//
//		}
//
//		@Override
//		protected void doInUiThread(String result) {
//			
//			decrementProgressCounter();
//
//			if(result == null) { //an error happened
//				Notification.showToast(getApplicationContext(), R.string.server_connection_error);
//				return;
//			}
//		}
//	}

	/**
	 * Used to retrieve the items from a layer
	 * If the layer already exists, but is outdated,
	 * we redownload the new items, but keep the old ones on the screen while downloading
	 */
//	class ItemsRequest extends DataRequest {
//
//		private final MapElementsList layer_;
//		private ItemizedIconOverlay<MapElement> aOverlay = null;
//		private ItemizedIconOverlay<MapElement> oldOverlay = null;
//
//		ItemsRequest(final MapElementsList layer) {
//			this.layer_ = layer;
//		}
//
//		@Override
//		protected void onCancelled() {
//			decrementProgressCounter();
//		}
//
//		@Override
//		protected void doInBackgroundThread(String result) {
//
//			if(result == null) {
//				return;
//			}
//
//			// Deserializes the response
//			Gson gson = new Gson();
//			Type mapElementType = new TypeToken<List<MapElementBean>>(){}.getType();
//			List<MapElementBean> items = new ArrayList<MapElementBean>();
//
//			try {
//				items = gson.fromJson(result, mapElementType);
//			} catch (Exception e) {
//				return;
//			}
//
//			if(items == null) {
//				return;
//			}
//
//			layer_.clear();
//			for(MapElementBean meb : items) {
//				layer_.add(new MapElement(meb));
//			}
//
//			// Try to get the icon for the overlay
//			try {
//				Drawable icon = getDrawableFromCacheOrUrl(layer_.getIconUrl());
//				aOverlay = new ItemizedIconOverlay<MapElement>(layer_, icon, overlayClickHandler_, new DefaultResourceProxyImpl(getApplicationContext()));
//			} catch (Exception e) {}
//			
//			// We don't have an icon
//			if(aOverlay == null) {
//				Log.d(this.getClass().toString(), "No icon for: " + layer_.getLayerTitle());
//				aOverlay = new ItemizedIconOverlay<MapElement>(layer_, overlayClickHandler_, new DefaultResourceProxyImpl(getApplicationContext()));
//			}
//
//			// Put the new overlay, get the old one if any (to be removed in the UI thread)
//			oldOverlay = cachedOverlays_.put(layer_, aOverlay);
//			lastRefreshedOverlays_.put(layer_, System.currentTimeMillis());
//		}
//
//		@Override
//		protected void doInUiThread(String result) {
//			
//			if(result == null) {
//				decrementProgressCounter();
//				Notification.showToast(getApplicationContext(), R.string.server_connection_error);
//				return;
//			}
//
//			// If we had another overlay that displayed the same data, remove it
//			if(oldOverlay != null) {
//				mapView_.getOverlays().remove(oldOverlay);
//			}
//			if(aOverlay != null) {
//				mapView_.getOverlays().add(aOverlay);
//			}
//			mapView_.invalidate();
//
//			decrementProgressCounter();
//			
//			if(intentLayerId_ != null && intentLayerId_.equals(layer_.getLayerId())) {
//				if(layer_.getItemFromId(intentItemId_) != null) {
//					centerOnPoint(layer_.getItemFromId(intentItemId_).getPoint());
//				}
//				
//				intentLayerId_ = null;
//			}
//			
//		}
//	}

	/**
	 * Handle a click on an item
	 */
//	class OverlayClickHandler implements ItemizedIconOverlay.OnItemGestureListener<MapElement> {
//
//		MapPlugin a_;
//
//		protected OverlayClickHandler(MapPlugin a) {
//			this.a_ = a;
//		}
//
//		@Override
//		public boolean onItemLongPress(int arg0, MapElement arg1) {
//			return false;
//		}
//
//		@Override
//		public boolean onItemSingleTapUp(int index, final MapElement item) {
//			final ItemDialog dialog = new ItemDialog(a_, item);
//			dialog.showDialog();
//			
//			return true;
//		}
//	}

	/**
	 * Get the Drawable object from an icon on the server.
	 * Get a cached version if available
	 * @param iconUrl URL of the icon
	 * @return the Drawable
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public Drawable getDrawableFromCacheOrUrl(String iconUrl) {
		if(iconUrl == null || iconUrl.equals("null") || iconUrl.length() <= 0 )
			return null;
		
		Drawable i = icons.get(iconUrl);
		
//		if(i == null) {
//			try {
//				i = ImageUtil.getDrawableFromUrl(RequestHandler.getServerUrl() + iconUrl);
//				icons.put(iconUrl, i);
//			} catch (IOException e) {
//				Log.e(this.getClass().toString(), "getDrawableFromCacheOrUrl -> " + e.toString());
//			}
//		}
		
		return i;
	}

	/**
	 * Runnable used to refresh the layers automatically
	 */
	private Runnable overlaysRefreshTicker_ = new Runnable() {
		public void run() {
			updateOverlays(false);	
			overlaysHandler_.postDelayed(this, LAYERS_REFRESH_TIMEOUT);
		}
	};

//	@Override
//	public PluginInfo getPluginInfo() {
//		return new MapInfo();
//	}
//
//	@Override
//	public PluginPreference getPluginPreference() {
//		return null;
//	}

}