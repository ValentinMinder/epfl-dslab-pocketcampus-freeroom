package org.pocketcampus.plugin.map;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.MyLocationOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.TilesOverlay;
import org.pocketcampus.R;
import org.pocketcampus.core.communication.DataRequest;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.core.ui.ActionBar;
import org.pocketcampus.core.ui.ActionBar.Action;
import org.pocketcampus.plugin.logging.Tracker;
import org.pocketcampus.plugin.map.elements.MapElement;
import org.pocketcampus.plugin.map.elements.MapElementsList;
import org.pocketcampus.plugin.map.elements.MapPathOverlay;
import org.pocketcampus.plugin.map.ui.ItemDialog;
import org.pocketcampus.plugin.map.ui.LayerSelector;
import org.pocketcampus.plugin.map.ui.LevelBar;
import org.pocketcampus.plugin.map.ui.OnLevelBarChangeListener;
import org.pocketcampus.shared.plugin.map.MapElementBean;
import org.pocketcampus.shared.plugin.map.MapLayerBean;
import org.pocketcampus.shared.plugin.map.Position;
import org.pocketcampus.utils.ImageUtil;
import org.pocketcampus.utils.Notification;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

	// Used for the location
	private static final float MAX_ACCURACY_FROM_DIRECTIONS = 100;
	private static final long LAYERS_REFRESH_TIMEOUT = 10000;
	private static Position CAMPUS_CENTER_P;
	private static GeoPoint CAMPUS_CENTER_G;
	private static int CAMPUS_RADIUS;

	// Map UI
	private MapView mapView_;
	private MapController mapController_;
	private MyLocationOverlay myLocationOverlay_;
	private MapPathOverlay mapPathOverlay_;
	private ConcurrentHashMap<MapElementsList, ItemizedIconOverlay<OverlayItem>> cachedOverlays_;
	private ConcurrentHashMap<MapElementsList, Long> lastRefreshedOverlays_;

	private OnItemGestureListener<OverlayItem> overlayClickHandler_;

	// UI
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
	 * Overlays which are unconditionally displayed,
	 * like the campus Tiles Overlay, or the user position
	 */
	private List<Overlay> constantOverlays_;

	// List of all and displayed overlays
	private List<MapElementsList> allLayers_;
	private List<MapElementsList> displayedLayers_;
	
	private HashMap<String, Drawable> icons = new HashMap<String, Drawable>();

	// Handler used to refresh the overlays 
	private Handler overlaysHandler_ = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_main);

		Tracker.getInstance().trackPageView("map/home");

		initVariables();

		// Setup view
		setupActionBar(true);
		actionBar_ = (ActionBar) findViewById(R.id.actionbar);
		setupMapView();

		// Check if another activity wants to show something
		Bundle extras = getIntent().getExtras();
		handleIntent(extras);
	}

	private void initVariables() {
		// The layers are not know yet
		constantOverlays_ = new ArrayList<Overlay>();
		allLayers_ = new ArrayList<MapElementsList>();
		displayedLayers_ = new ArrayList<MapElementsList>();
		cachedOverlays_ = new ConcurrentHashMap<MapElementsList, ItemizedIconOverlay<OverlayItem>>();
		lastRefreshedOverlays_ = new ConcurrentHashMap<MapElementsList, Long>();

		overlayClickHandler_ = new OverlayClickHandler(this);

		// Get the campus coordinates
		double lat = Double.parseDouble(getResources().getString(R.string.map_campus_latitude));
		double lon = Double.parseDouble(getResources().getString(R.string.map_campus_longitude));
		double alt = Double.parseDouble(getResources().getString(R.string.map_campus_altitude));
		CAMPUS_CENTER_P = new Position(lat, lon, alt);
		CAMPUS_CENTER_G = new GeoPoint(CAMPUS_CENTER_P.getLatitude(), CAMPUS_CENTER_P.getLongitude(), CAMPUS_CENTER_P.getAltitude());
		CAMPUS_RADIUS = getResources().getInteger(R.integer.map_campus_radius);

		// XXX Displays the overlay for live transport
		//new TransportLiveOverlay(getApplicationContext()).requestOverlay(this);
	}

	@Override
	protected void setupActionBar(boolean addHomeButton) {

		actionBar_ = (ActionBar) findViewById(R.id.actionbar);
		actionBar_.addAction(new Action() {

			@Override
			public void performAction(View view) {
				updateOverlays(true);

				Tracker.getInstance().trackPageView("map/manualRefresh");
			}

			@Override
			public int getDrawable() {
				return R.drawable.refresh;
			}
		});

		super.setupActionBar(addHomeButton);
	}

	/**
	 * Change the level of the map.
	 * @param level the new level
	 */
	private void changeLevel(int level) {
		ITileSource epflTile = new EpflTileSource(level + "");
		MapTileProviderBasic mProvider = new MapTileProviderBasic(getApplicationContext());
		mProvider.setTileSource(epflTile);
		TilesOverlay mTilesOverlay = new TilesOverlay(mProvider, getBaseContext());
		constantOverlays_.remove(0);
		constantOverlays_.add(0, mTilesOverlay);
		updateOverlays(false);

		Tracker.getInstance().trackPageView("map/changeLevel" + level);
	}

	/**
	 * Handle the eventual extras of the intent.
	 * For example, it can show a map element
	 * @param extras the bundle containing the extras
	 */
	private void handleIntent(Bundle extras) {
		if(extras == null)
			return;

		if(extras.containsKey("MapElement")) {
			Log.d("MapPlugin", "intent with extras");
			MapElementBean meb = (MapElementBean) extras.getSerializable("MapElement");
			OverlayItem overItem = new OverlayItem(meb.getTitle(), meb.getDescription(),
					new GeoPoint(meb.getLatitude(), meb.getLongitude()));
			List<OverlayItem> overItems = new ArrayList<OverlayItem>(1);
			overItems.add(overItem);
			ItemizedOverlay<OverlayItem> aOverlay = new ItemizedIconOverlay<OverlayItem>(overItems, overlayClickHandler_, new DefaultResourceProxyImpl(getApplicationContext()));
			constantOverlays_.add(aOverlay);
		}
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
		mapController_ = mapView_.getController();

		// Display the level bar if needed
		if(getResources().getBoolean(R.bool.map_has_levels)) {
			SeekBar seekBar = (SeekBar) findViewById(R.id.map_level_bar);
			int max = getResources().getInteger(R.integer.map_level_max);
			int min = getResources().getInteger(R.integer.map_level_min);
			new LevelBar(seekBar, new OnLevelBarChangeListener() {
				@Override
				public void onLevelChanged(int level) {
					changeLevel(level);
					String slevel = getResources().getString(R.string.map_level);
					Toast.makeText(getApplicationContext(), slevel + " " + level, Toast.LENGTH_SHORT).show();
				}
			}, max, min, max);
		}

		// Add EPFL tiles layer
		ITileSource epflTile = new EpflTileSource();
		MapTileProviderBasic mProvider = new MapTileProviderBasic(getApplicationContext());
		mProvider.setTileSource(epflTile);
		TilesOverlay mTilesOverlay = new TilesOverlay(mProvider, this.getBaseContext());
		constantOverlays_.add(0, mTilesOverlay);

		// Following the user
		myLocationOverlay_ = new MyLocationOverlay(this, mapView_);
		constantOverlays_.add(myLocationOverlay_);

		// Path overlay
		mapPathOverlay_ = new MapPathOverlay(Color.RED, 3.0f, this);
		constantOverlays_.add(mapPathOverlay_);

		// Center map
		centerOnCampus();
	}

	/**
	 * Displays a not-cancelable progress dialog with the specific message.
	 * @param message
	 */
	private void showProgressDialog(int messageId) {
		progressDialog_ = new ProgressDialog(this);
		progressDialog_.setTitle(getResources().getString(R.string.please_wait));
		progressDialog_.setMessage(getResources().getString(messageId));
		progressDialog_.setCancelable(false);
		progressDialog_.show();
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

	/**
	 * Re-enable the location service and the layers refresh
	 */
	@Override
	protected void onResume() {

		if(myLocationOverlay_.isFollowLocationEnabled()) {
			myLocationOverlay_.enableMyLocation();
		}
		
		overlaysHandler_.removeCallbacks(overlaysRefreshTicker_);
		overlaysHandler_.post(overlaysRefreshTicker_);

		super.onResume();
	}

	/**
	 * Disable the location service and the layers refresh
	 */
	@Override
	protected void onPause() {
		myLocationOverlay_.disableMyLocation();
		
		overlaysHandler_.removeCallbacks(overlaysRefreshTicker_);
		
		super.onPause();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	/**
	 * Handle the menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.map, menu);

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.map_clear_path).setVisible(mapPathOverlay_.isShowingPath());

		MenuItem follow = menu.findItem(R.id.map_my_position);
		if(myLocationOverlay_.isFollowLocationEnabled()) {
			follow.setTitle(R.string.map_menu_my_position_off);
		} else {
			follow.setTitle(R.string.map_menu_my_position_on);
		}

		return true;
	}

	/**
	 * Handle the menu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {

		// Show a layer selection
		case R.id.map_menu_layers_button:
			selectLayers();

			Tracker.getInstance().trackPageView("map/menu/getLayers");
			return true;

			// Enable the user following
		case R.id.map_my_position:
			toggleCenterOnUserPosition();

			Tracker.getInstance().trackPageView("map/menu/togglPosition");
			return true;

			// Enable the user following
		case R.id.map_campus_position:
			centerOnCampus();

			Tracker.getInstance().trackPageView("map/menu/centerOnCampus");
			return true;

			// Shows the search dialog
		case R.id.map_search:
			onSearchRequested();

			Tracker.getInstance().trackPageView("map/menu/search"); 
			return true;

		case R.id.map_clear_path:
			clearPath();

			Tracker.getInstance().trackPageView("map/menu/clearPath");
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Download the list of available layers
	 */
	private void selectLayers() {

		// If we don't already have a cache of the layers
		if(allLayers_ == null || allLayers_.size() == 0) {
			showProgressDialog(R.string.map_loading_layers);

			//request of the layers
			getRequestHandler().execute(new LayersRequest(), "getLayers", (RequestParameters)null);
		} else {
			layerSelector();
		}
	}

	/**
	 * Launch the dialog that allows to select the different layers
	 */
	private void layerSelector() {
		final LayerSelector l = new LayerSelector(this, allLayers_, displayedLayers_);

		// Show the dialog, using a callback to the the selected layers back
		l.selectLayers(new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				setSelectedLayers(l.getSelectedLayers());
			}
		});

	}

	/**
	 * Enable the location and center the map on the user
	 */
	private void toggleCenterOnUserPosition() {
		if(myLocationOverlay_.isFollowLocationEnabled()) {
			myLocationOverlay_.disableMyLocation();
			myLocationOverlay_.disableFollowLocation();
		} else {
			myLocationOverlay_.enableMyLocation();
			myLocationOverlay_.enableFollowLocation();
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
		myLocationOverlay_.disableFollowLocation();

		mapController_.setZoom(getResources().getInteger(R.integer.map_zoom_level)); 
		mapController_.setCenter(point);
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

		toggleCenterOnUserPosition();

		// Clear the path if there was an old one
		mapPathOverlay_.clearPath();
		mapView_.invalidate();

		myLocationOverlay_.runOnFirstFix(new Runnable() {

			@Override
			public void run() {
				Location fix = myLocationOverlay_.getLastFix();
				showDirectionFromTo(fix, endPos);
			}
		});

		Tracker.getInstance().trackPageView("map/showDirections?d=" + endPos.toString());
	}


	/**
	 * Show direction from the given fix to a position
	 * @param fix Fix from the GPS
	 * @param to where to go
	 */
	private void showDirectionFromTo(Location fix, Position to) {

		// Check if the user is located and has a good accuracy
		if(fix.hasAccuracy() && fix.getAccuracy() > MAX_ACCURACY_FROM_DIRECTIONS) {
			Notification.showToast(getApplicationContext(), R.string.map_directions_not_accurate);
			return;
		}

		// Check if the user is on campus
		Position startPos = new Position(fix.getLatitude(), fix.getLongitude(), fix.getAltitude());
		double distanceToCenter = directDistanceBetween(startPos, CAMPUS_CENTER_P);
		if(distanceToCenter > CAMPUS_RADIUS) {
			Notification.showToast(getApplicationContext(), R.string.map_directions_not_on_campus);
			return;
		}

		// Parameters 
		RequestParameters params = new RequestParameters();
		params.addParameter("startLatitude", Double.toString(startPos.getLatitude()));
		params.addParameter("startLongitude", Double.toString(startPos.getLongitude()));
		params.addParameter("endLatitude", Double.toString(to.getLatitude()));
		params.addParameter("endLongitude", Double.toString(to.getLongitude()));

		//request of the layers
		incrementProgressCounter();
		getRequestHandler().execute(new DirectionsRequest(), "routing", params);
	}

	/**
	 * Set the selected layers and update the overlays
	 * @param selectedLayers
	 */
	private void setSelectedLayers(ArrayList<MapElementsList> selectedLayers) {
		this.displayedLayers_ = selectedLayers;

		updateOverlays(false);

		// Track
		StringBuffer selected = new StringBuffer("?layers=");
		for(MapElementsList l : selectedLayers) {
			selected.append(l.getLayerTitle());
			selected.append(',');
		}

		Tracker.getInstance().trackPageView("map/selectedLayers" + selected);
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
		for(MapElementsList layer : displayedLayers_) {
			ItemizedIconOverlay<OverlayItem> aOverlay = cachedOverlays_.get(layer);

			// The overlay already exists
			if(aOverlay != null) {
				mapView_.getOverlays().add(aOverlay);
				Log.d(this.getClass().toString(), "Overlay: " + aOverlay.toString());
			} else {
				Log.d(this.getClass().toString(), "Overlay NULL");
			}

			Log.d(this.getClass().toString(), "Cached overlays: " + cachedOverlays_.toString());

			// The overlay does not exist, or is outdated 
			// If outdated, we redownload the new items, but keep the old ones on the screen while downloading
			if(aOverlay == null || isLayerOutdated(layer) || forceRefresh) {
				Log.d(this.getClass().toString(), "Layer outdated: " + layer.toString());
				populateLayer(layer);
			}

		}

		mapView_.invalidate();
	}

	public void setRailwayOverlay(Overlay railwayOverlay) {
		updateOverlays(false);
		mapView_.getOverlays().add(railwayOverlay);
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
		 * The data comes from the cache (local file?) or from the server
		 */

		incrementProgressCounter();
		RequestParameters param = new RequestParameters();
		param.addParameter("layer_id", String.valueOf(layer.getLayerId()));
		getRequestHandler().execute(new ItemsRequest(layer), "getItems", param);
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
	 * Get the distance between two points
	 * 
	 * @param start Start position
	 * @param end End position
	 * @return distance in meters
	 */
	private static double directDistanceBetween(Position start, Position end) {
		GeoPoint s = new GeoPoint(start.getLatitude(), start.getLongitude(), start.getAltitude());
		GeoPoint e = new GeoPoint(end.getLatitude(), end.getLongitude(), end.getAltitude());
		return s.distanceTo(e);
	}

	/**
	 * Request class for the directions
	 */
	class DirectionsRequest extends DataRequest {
		@Override
		protected void doInUiThread(String result) {

			decrementProgressCounter();

			// Deserializes the response
			Gson gson = new Gson();
			List<Position> path = null;
			Type t = new TypeToken<List<Position>>(){}.getType();

			try {
				path = gson.fromJson(result, t);
				mapPathOverlay_.setList(path);
				mapView_.invalidate();
			} catch(Exception e) {
				System.out.println(e);
			}

		}
	}

	/**
	 * Used to retreive the layers from the server
	 */
	class LayersRequest extends DataRequest {

		@Override
		protected void onCancelled() {
			dismissProgressDialog();
			Notification.showToast(getApplicationContext(), R.string.server_connection_error);
		}

		@Override
		protected void doInBackgroundThread(String result) {

			if(result == null) {
				return;
			}

			// Deserializes the response
			Gson gson = new Gson();
			Type mapLayersType = new TypeToken<List<MapLayerBean>>(){}.getType();
			List<MapLayerBean> layers = new ArrayList<MapLayerBean>();
			try {
				layers = gson.fromJson(result, mapLayersType);
			} catch (JsonSyntaxException e) {
				dismissProgressDialog();
				Notification.showToast(getApplicationContext(), R.string.unexpected_response);
				return;
			}
			if(layers == null) {
				dismissProgressDialog();
				Notification.showToast(getApplicationContext(), R.string.server_connection_error);
				return;
			}

			allLayers_ = new ArrayList<MapElementsList>(layers.size());
			for(MapLayerBean mlb : layers) {
				if(mlb.isDisplayable()) {
					allLayers_.add(new MapElementsList(mlb));
				}
			}

		}

		@Override
		protected void doInUiThread(String result) {

			if(result == null) { //an error happened
				dismissProgressDialog();
				Notification.showToast(getApplicationContext(), R.string.server_connection_error);
				return;
			}

			dismissProgressDialog();
			layerSelector();
		}
	}

	/**
	 * Used to retrieve the items from a layer
	 * If the layer already exists, but is outdated,
	 * we redownload the new items, but keep the old ones on the screen while downloading
	 */
	class ItemsRequest extends DataRequest {

		private final MapElementsList layer_;
		private ItemizedIconOverlay<OverlayItem> aOverlay = null;
		private ItemizedIconOverlay<OverlayItem> oldOverlay = null;

		ItemsRequest(final MapElementsList layer) {
			this.layer_ = layer;
		}

		@Override
		protected void doInBackgroundThread(String result) {

			if(result == null) {
				return;
			}

			// Deserializes the response
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

			layer_.clear();
			for(MapElementBean meb : items) {
				layer_.add(new MapElement(meb));
			}

			Log.d(this.getClass().toString(), "got new items");

			// Try to get the icon for the overlay
			try {
				Drawable icon = getDrawableFromCacheOrUrl(layer_.getIconUrl());
				aOverlay = new ItemizedIconOverlay<OverlayItem>(layer_, icon, overlayClickHandler_, new DefaultResourceProxyImpl(getApplicationContext()));
			} catch (Exception e) {
				aOverlay = new ItemizedIconOverlay<OverlayItem>(layer_, overlayClickHandler_, new DefaultResourceProxyImpl(getApplicationContext()));
			}

			// Put the new overlay, get the old one if any (to be removed in the UI thread)
			oldOverlay = cachedOverlays_.put(layer_, aOverlay);
			lastRefreshedOverlays_.put(layer_, System.currentTimeMillis());
		}

		@Override
		protected void doInUiThread(String result) {
			if(result == null) {
				decrementProgressCounter();
				Notification.showToast(getApplicationContext(), R.string.server_connection_error);
				return;
			}

			// If we had another overlay that displayed the same data, remove it
			if(oldOverlay != null) {
				mapView_.getOverlays().remove(oldOverlay);
				Log.d(this.getClass().toString(), "Old overlay: " + oldOverlay.toString());
			} else {
				Log.d(this.getClass().toString(), "No old overlay");
			}

			mapView_.getOverlays().add(aOverlay);

			mapView_.invalidate();
			decrementProgressCounter();
		}
	}

	/**
	 * Handle a click on an item
	 */
	class OverlayClickHandler implements ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {

		MapPlugin a_;

		protected OverlayClickHandler(MapPlugin a) {
			this.a_ = a;
		}

		@Override
		public boolean onItemLongPress(int arg0, OverlayItem arg1) {
			return false;
		}

		@Override
		public boolean onItemSingleTapUp(int index, final OverlayItem item) {

			final Dialog dialog = new ItemDialog(a_, item);

			dialog.show();

			return true;
		}
	}

	/**
	 * Get the Drawable object from an icon on the server.
	 * Get a cached version if available
	 * @param iconUrl URL of the icon
	 * @return the Drawable
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public Drawable getDrawableFromCacheOrUrl(String iconUrl) throws MalformedURLException, IOException {
		
		Drawable i = icons.get(iconUrl);
		
		if(i == null) {
			i = ImageUtil.getDrawableFromUrl(iconUrl);
			icons.put(iconUrl, i);
		}
		
		return i;
	}

	/**
	 * Runnable used to refresh the layers automatically
	 */
	private Runnable overlaysRefreshTicker_ = new Runnable() {
		public void run() {
			
			Log.d(this.getClass().toString(), "ticker");

			updateOverlays(false);
			
			overlaysHandler_.postDelayed(this, LAYERS_REFRESH_TIMEOUT);
		}
	};

	@Override
	public PluginInfo getPluginInfo() {
		return new MapInfo();
	}

	@Override
	public PluginPreference getPluginPreference() {
		return null;
	}

}