package org.pocketcampus.plugin.map.android;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
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
import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginView;
import org.pocketcampus.plugin.map.R;
import org.pocketcampus.plugin.map.android.elements.MapElement;
import org.pocketcampus.plugin.map.android.elements.MapElementsList;
import org.pocketcampus.plugin.map.android.elements.MapPathOverlay;
import org.pocketcampus.plugin.map.android.iface.IMapView;
import org.pocketcampus.plugin.map.android.search.MapSearchActivity;
import org.pocketcampus.plugin.map.android.ui.LevelBar;
import org.pocketcampus.plugin.map.android.ui.OnLevelBarChangeListener;
import org.pocketcampus.plugin.map.common.Position;
import org.pocketcampus.plugin.map.shared.MapItem;

import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.markupartist.android.widget.Action;

/**
 * Main class for the map plugin.
 * 
 * @author Johan <johan.leuenberger@epfl.ch>
 * @author Jonas <jonas.schmid@epfl.ch>
 * @author Florian <florian.laurent@epfl.ch>
 * @author Johan <johan.leuenberger@epfl.ch>
 *
 */
public class MapMainView extends PluginView implements IMapView {
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return MapMainController.class;
	}

	public static final String ITEM_GO_URL = "go_url:";

	// Used for the location
	private static Position CAMPUS_CENTER_P;
	private static GeoPoint CAMPUS_CENTER_G;

	private static boolean DEBUG = true;

	// Map UI
	private MapView mapView_;
	private MapController mapController_;
	private MyLocationOverlay myLocationOverlay_;
	private MyLocationOverlay googleLocationOverlay_;
	private MapPathOverlay mapPathOverlay_;
	private ConcurrentHashMap<MapElementsList, ItemizedIconOverlay<MapElement>> cachedOverlays_;

	private OnItemGestureListener<MapElement> overlayClickHandler_;

	/**
	 * Overlays which are unconditionally displayed, like the campus Tiles
	 * Overlay, or the user position
	 */
	private List<Overlay> constantOverlays_;

	/**
	 * Overlays which are temporary, like the search result.
	 */
	private List<Overlay> temporaryOverlays_;

	// List of all and displayed overlays
	private List<MapElementsList> selectedLayers_;

	private MapModel mModel;

	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {

		// mController = (MapMainController) controller;
		mModel = (MapModel) controller.getModel();

		setContentView(R.layout.map_main);

		initVariables();

		// Setup view
		setupMapView();

		// Download the available layers
		// mController.getLayers();

		// handleSearchIntent(getIntent().getExtras());
		setActionBarTitle(getString(R.string.map_plugin_title));

		updateActionBar();
	}

	private void updateActionBar() {
		removeAllActionsFromActionBar();
		addActionToActionBar(new Action() {
			@Override
			public void performAction(View view) {
				if (!myLocationOverlay_.isMyLocationEnabled()) {
					Toast.makeText(MapMainView.this,
							getString(R.string.map_compute_position),
							Toast.LENGTH_LONG).show();
				}
				toggleCenterOnUserPosition();
				updateActionBar();
			}

			@Override
			public int getDrawable() {
				if (!myLocationOverlay_.isMyLocationEnabled()) {
					return R.drawable.map_mylocation_action;
				} else {
					return R.drawable.map_mylocation_on_action;
				}
			}

			@Override
			public String getDescription() {
				return getString(R.string.map_compute_position);
			}
		});
		addActionToActionBar(new Action() {
			@Override
			public void performAction(View view) {
				onSearchRequested();
				trackEvent("Search", null);
			}

			@Override
			public int getDrawable() {
				return R.drawable.map_search_action;
			}

			@Override
			public String getDescription() {
				return getString(R.string.map_search);
			}
		});
	}

	private void initVariables() {
		// The layers are not know yet
		constantOverlays_ = new ArrayList<Overlay>();
		temporaryOverlays_ = new ArrayList<Overlay>();
		selectedLayers_ = new ArrayList<MapElementsList>();
		cachedOverlays_ = new ConcurrentHashMap<MapElementsList, ItemizedIconOverlay<MapElement>>();

		overlayClickHandler_ = new OverlayClickHandler(this);

		// Get the campus coordinates
		double lat = Double.parseDouble(getResources().getString(
				R.string.map_campus_latitude));
		double lon = Double.parseDouble(getResources().getString(
				R.string.map_campus_longitude));
		double alt = Double.parseDouble(getResources().getString(
				R.string.map_campus_altitude));

		CAMPUS_CENTER_P = new Position(lat, lon, alt);
		CAMPUS_CENTER_G = new GeoPoint(CAMPUS_CENTER_P.getLatitude(),
				CAMPUS_CENTER_P.getLongitude(), CAMPUS_CENTER_P.getAltitude());

	}

	/**
	 * Change the level of the map.
	 * 
	 * @param level
	 *            the new level
	 */
	private void changeLevel(int level) {
		MapTileProviderBasic provider = new MapTileProviderBasic(
				getApplicationContext());

		ITileSource tileSource = getTileSource(level);

		provider.setTileSource(tileSource);

		TilesOverlay tilesOverlay = new TilesOverlay(provider, getBaseContext());
		tilesOverlay.setLoadingBackgroundColor(Color.TRANSPARENT);
		constantOverlays_.remove(0);
		constantOverlays_.add(0, tilesOverlay);
		updateOverlays(false);
		mapView_.postInvalidate();

		trackEvent("ChangeLevel", "" + level);
	}

	/**
	 * Returns the tile source to be displayed
	 * 
	 * @return the tile source
	 */
	private ITileSource getTileSource(int level) {
		ITileSource tileSource;
		tileSource = new EpflTileSource(level + "");
		return tileSource;
	}

	/**
	 * Handle the eventual extras of the intent. For example, it can show a map
	 * element
	 * 
	 * @param extras
	 *            the bundle containing the extras
	 * @return Whether it handled the intent or not
	 */
	private boolean handleSearchIntent(Bundle extras) {

		if (extras == null) {
			return false;
		}

		if (extras.containsKey("MapElement")) {
			MapItem meb = (MapItem) extras.getSerializable("MapElement");
			GeoPoint gp = new GeoPoint(meb.getLatitude(), meb.getLongitude());
			MapElement overItem = new MapElement(meb.getTitle(),
					meb.getDescription(), gp);
			List<MapElement> overItems = new ArrayList<MapElement>(1);
			overItems.add(overItem);
			Drawable searchMarker = this.getResources().getDrawable(
					R.drawable.map_marker_search);
			ItemizedOverlay<MapElement> aOverlay = new ItemizedIconOverlay<MapElement>(
					overItems, searchMarker, overlayClickHandler_,
					new DefaultResourceProxyImpl(getApplicationContext()));

			temporaryOverlays_.clear();
			temporaryOverlays_.add(aOverlay);

			centerOnPoint(gp);

			updateOverlays(false);
			return true;
		}

		return false;
	}

	/**
	 * The background is provided by the default tile source. We add a
	 * TileOverlay above the background (for example the map of the campus).
	 */
	private void setupMapView() {

		mapView_ = (MapView) findViewById(R.id.mapview);
		mapView_.setMultiTouchControls(true);
		mapView_.setBuiltInZoomControls(true);
		/*
		 * XXX This is done to allow zoom up to 22 (for epfl) but the tiles will
		 * not load because the mapnik zoom is between 14 and 19
		 */
		ITileSource aTileSource = new XYTileSource("Mapnik",
				ResourceProxy.string.mapnik, 14, 19, 256, ".png",
				"http://tile.openstreetmap.org/");
		mapView_.setTileSource(aTileSource);
		mapController_ = mapView_.getController();

		// Display the level bar if needed
		if (getResources().getBoolean(R.bool.map_has_levels)) {
			SeekBar seekBar = (SeekBar) findViewById(R.id.map_level_bar);
			int max = getResources().getInteger(R.integer.map_level_max);
			int min = getResources().getInteger(R.integer.map_level_min);
			final TextView levelTextView = (TextView) findViewById(R.id.map_level_textview);
			new LevelBar(seekBar, new OnLevelBarChangeListener() {
				@Override
				public void onLevelChanged(int level) {
					levelTextView.setVisibility(View.INVISIBLE);
					changeLevel(level);
					String slevel = getResources()
							.getString(R.string.map_level);
					Toast.makeText(getApplicationContext(),
							slevel + " " + level, Toast.LENGTH_SHORT).show();
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
		MapTileProviderBasic provider = new MapTileProviderBasic(
				getApplicationContext());
		provider.setTileSource(campusTile);
		TilesOverlay tilesOverlay = new TilesOverlay(provider,
				this.getBaseContext());
		tilesOverlay.setLoadingBackgroundColor(Color.TRANSPARENT);
		constantOverlays_.add(0, tilesOverlay);

		// Following the user
		myLocationOverlay_ = new MyLocationOverlay(this, mapView_);
		// myLocationOverlay_ = new HybridPositioningOverlay(this, mapView_);
		// constantOverlays_.add(myLocationOverlay_);
		if (DEBUG) {
			googleLocationOverlay_ = new MyLocationOverlay(this, mapView_);
			constantOverlays_.add(googleLocationOverlay_);
		}

		// Path overlay
		mapPathOverlay_ = new MapPathOverlay(Color.RED, 3.0f, this);
		constantOverlays_.add(mapPathOverlay_);

		// Center map
		centerOnCampus();

		// Forces redisplay
		updateOverlays(false);
	}

	/**
	 * Re-enable the location service and the layers refresh
	 */
	@Override
	protected void onResume() {

		super.onResume();
	}

	@Override
	protected String screenName() {
		return "/map";
	}

	/**
	 * Disable the location service and the layers refresh
	 */
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void handleIntent(Intent intent) {
		Uri aData = intent.getData();
		if (aData != null && aData.getQueryParameter("q") != null) {
			String query = aData.getQueryParameter("q");
			Intent i = new Intent(this, MapSearchActivity.class);
			i.putExtra(SearchManager.QUERY, query);
			i.setAction(Intent.ACTION_SEARCH);
			startActivity(i);
		}
		handleSearchIntent(intent.getExtras());
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	/**
	 * Enable the location and center the map on the user
	 */
	private void toggleCenterOnUserPosition() {
		if (myLocationOverlay_.isFollowLocationEnabled()) {
			myLocationOverlay_.disableMyLocation();
			myLocationOverlay_.disableFollowLocation();

			trackEvent("CenterOnSelf", "false");

			if (DEBUG) {
				googleLocationOverlay_.disableMyLocation();
			}

		} else {
			myLocationOverlay_.enableMyLocation();
			myLocationOverlay_.enableFollowLocation();

			trackEvent("CenterOnSelf", "true");

			if (DEBUG) {
				googleLocationOverlay_.enableMyLocation();
			}
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
	 * 
	 * @param point
	 *            Where to center the map
	 */
	public void centerOnPoint(GeoPoint point) {
		// myLocationOverlay_.disableFollowLocation();

		mapController_.setZoom(getResources().getInteger(
				R.integer.map_zoom_level));
		mapController_.setCenter(point);
	}

	/**
	 * Show the directions layer to a certain POI
	 *
	 * @param endPos
	 *            Position where to go
	 */
	public void showDirectionsFromHereToPosition(final Position endPos) {
	}

	/**
	 * Displays all selected overlay items (from layers).
	 * 
	 * @param forceRefresh
	 *            Whether to check is the cache is still valid or to force
	 *            refresh.
	 */
	private void updateOverlays(boolean forceRefresh) {
		// First we remove all the overlays and then add the constant ones
		mapView_.getOverlays().clear();
		for (Overlay over : constantOverlays_) {
			mapView_.getOverlays().add(over);
		}

		// Display the selected layers
		for (MapElementsList layer : selectedLayers_) {
			ItemizedIconOverlay<MapElement> aOverlay = cachedOverlays_
					.get(layer);

			// The overlay already exists
			if (aOverlay != null) {
				mapView_.getOverlays().add(aOverlay);
			}

		}

		for (Overlay over : temporaryOverlays_) {
			mapView_.getOverlays().add(over);
		}

		mapView_.invalidate();
	}

	/**
	 * Used to retrieve the items from a layer If the layer already exists, but
	 * is outdated, we redownload the new items, but keep the old ones on the
	 * screen while downloading
	 */

	/**
	 * Handle a click on an item
	 */
	class OverlayClickHandler implements
			ItemizedIconOverlay.OnItemGestureListener<MapElement> {

		MapMainView a_;

		protected OverlayClickHandler(MapMainView a) {
			this.a_ = a;
		}

		@Override
		public boolean onItemLongPress(int arg0, MapElement arg1) {
			return false;
		}

		@Override
		public boolean onItemSingleTapUp(int index, final MapElement item) {
			// final ItemDialog dialog = new ItemDialog(a_, item);
			// dialog.showDialog();

			return true;
		}
	}

	/**
	 * Get the Drawable object from an icon on the server. Get a cached version
	 * if available
	 * 
	 * @param iconUrl
	 *            URL of the icon
	 * @return the Drawable
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public Drawable getDrawableFromCacheOrUrl(String iconUrl) {
		if (iconUrl == null || iconUrl.equals("null") || iconUrl.length() <= 0)
			return null;

		// Drawable i = icons.get(iconUrl);
		Drawable i = getResources().getDrawable(R.drawable.map_marker_search);

		// if(i == null) {
		// try {
		// i = ImageUtil.getDrawableFromUrl(RequestHandler.getServerUrl() +
		// iconUrl);
		// icons.put(iconUrl, i);
		// } catch (IOException e) {
		// Log.e(this.getClass().toString(), "getDrawableFromCacheOrUrl -> " +
		// e.toString());
		// }
		// }

		return i;
	}

	@Override
	public void networkErrorHappened() {
		// Toast toast = Toast.makeText(getApplicationContext(),
		// "Network error!", Toast.LENGTH_SHORT);
		// toast.show();
	}

	@Override
	public void layersUpdated() {
	}

	@Override
	public void layerItemsUpdated() {
		List<MapItem> items = mModel.getLayerItems();

		if (items == null || items.size() < 1) {
			return;
		}

		System.out.println("Layer id: " + items.get(0).getLayerId());

		MapElementsList layer = null;
		for (MapElementsList l : selectedLayers_) {
			if (l.getLayerId() == items.get(0).getLayerId()) {
				layer = l;
			}
		}

		ItemizedIconOverlay<MapElement> aOverlay;

		// Try to get the icon for the overlay
		aOverlay = new ItemizedIconOverlay<MapElement>(layer,
				overlayClickHandler_, new DefaultResourceProxyImpl(
						getApplicationContext()));

		ItemizedIconOverlay<MapElement> oldOverlay = cachedOverlays_.put(layer,
				aOverlay);

		if (oldOverlay != null) {
			mapView_.getOverlays().remove(oldOverlay);
		}
		if (aOverlay != null) {
			mapView_.getOverlays().add(aOverlay);
		}
		mapView_.invalidate();
	}

	@Override
	public void searchResultsUpdated() {
		// TODO Auto-generated method stub

	}
}
