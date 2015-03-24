package org.pocketcampus.plugin.map.android;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginView;
import org.pocketcampus.platform.android.utils.DialogUtils;
import org.pocketcampus.platform.android.utils.DialogUtils.MultiChoiceHandler;
import org.pocketcampus.platform.android.utils.DialogUtils.SingleChoiceHandler;
import org.pocketcampus.platform.shared.utils.StringUtils;
import org.pocketcampus.plugin.map.R;
import org.pocketcampus.plugin.map.android.iface.IMapView;
import org.pocketcampus.plugin.map.shared.MapItem;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;

/**
 * Main class for the map plugin.
 * 
 * @author Amer Chamseddine <amer@pocketcampus.org>
 *
 */
public class MapMainView extends PluginView implements IMapView {
	
		
	private static final String OSM_TILE_URL = "http://plan-osm-tile{lb}.epfl.ch/{z}/{x}/{y}.png";
	//private static final String EPFL_TILE_URL = "http://plan-epfl-tile{lb}.epfl.ch/batiments{floor}-merc/{fz}/{sx}/{sry}.png";
	private static final String EPFL_TILE_URL = "http://plan-epfl-wmts{lb}.epfl.ch/1.0.0/batiments{floor}-merc/default/20141107/grid-merc/{z}/{y}/{x}.png";
	// http://plan-epfl-wmts4.epfl.ch/1.0.0/batiments0-ch/default/20141107/grid-ch/19/1932/616.png
	// http://plan-epfl-wmts0.epfl.ch/1.0.0/batimentsall-merc/default/20141107/grid-merc/15/11589/16981.png
	
	private static String replacePlaceholders(String url, int lb, String floor, int z, int x, int y) {
        int rx = (1 << z) - x - 1;
        int ry = (1 << z) - y - 1;
    	DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
		DecimalFormatSymbols customSymbol = new DecimalFormatSymbols();
		customSymbol.setGroupingSeparator('/');
		formatter.setDecimalFormatSymbols(customSymbol);
		String sx = formatter.format(x + 1000000000).substring(2);
		String sy = formatter.format(y + 1000000000).substring(2);
		String srx = formatter.format(rx + 1000000000).substring(2);
		String sry = formatter.format(ry + 1000000000).substring(2);
    	String fz = String.format(Locale.US, "%02d", z);
		Map<String, String> replacement = new HashMap<String, String>();
		replacement.put("{lb}", "" + lb); // load balancer
		replacement.put("{floor}", floor);
		replacement.put("{z}", "" + z);
		replacement.put("{x}", "" + x);
		replacement.put("{y}", "" + y);
		replacement.put("{rx}", "" + rx); // reversed x
		replacement.put("{ry}", "" + ry); // reversed y
		replacement.put("{sx}", sx); // separated x
		replacement.put("{sy}", sy); // separated y
		replacement.put("{srx}", srx); // separated reversed x
		replacement.put("{sry}", sry); // separated reversed y
		replacement.put("{fz}", fz); // formatted zoom
		for(Map.Entry<String, String> e : replacement.entrySet()) {
			url = url.replace(e.getKey(), e.getValue());
		}
		return url;
	}
	
	private static final String WMS_THEMES_URL = "http://plan.epfl.ch/wms_themes?FORMAT=image%2Fpng&LAYERS={layers}&TRANSPARENT=TRUE&LOCALID=-1&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&STYLES=&SRS=EPSG%3A900913&BBOX={cl},{cb},{cr},{ct}&WIDTH={sw}&HEIGHT={sh}";
	
	private static String replacePlaceholders(String url, List<String> layers, String floor, double l, double b, double r, double t, double scale) {
		List<String> tt = new LinkedList<String>();
		for(String ll : layers) {
			tt.add(ll.replace("{floor}", floor));
		}
		layers = tt;
		double cl = convertLng(l);
		double cb = convertLat(b);
		double cr = convertLng(r);
		double ct = convertLat(t);
		double factor = Math.sqrt(Math.abs((cl - cr) / (cb - ct)));
		double sw = scale * factor;
		double sh = scale / factor;
		double aperture = Math.abs(cl - cr) + Math.abs(cb - ct);
		Map<String, String> replacement = new HashMap<String, String>();
		replacement.put("{layers}", TextUtils.join(",", layers));
		replacement.put("{l}", "" + l); // left
		replacement.put("{b}", "" + b); // bottom
		replacement.put("{r}", "" + r); // right
		replacement.put("{t}", "" + t); // top
		replacement.put("{cl}", "" + cl); // converted left
		replacement.put("{cb}", "" + cb); // converted bottom
		replacement.put("{cr}", "" + cr); // converted right
		replacement.put("{ct}", "" + ct); // converted top
		replacement.put("{sh}", "" + (int) sh); // scaled height
		replacement.put("{sw}", "" + (int) sw); // scaled width
		replacement.put("{aperture}", "" + aperture);
		for(Map.Entry<String, String> e : replacement.entrySet()) {
			url = url.replace(e.getKey(), e.getValue());
		}
		return url;
	} 

	private static final String QUERY_MAP_URL = "http://plan.epfl.ch/search/xyz/?lang={hl}&tolerance={precision}&lon={cx}&lat={cy}&layers__eq={ulayers}&queryable=layers,floor&floor__eq={ifloor}";
	
	private static String replacePlaceholders(String url, String hl, List<String> layers, String floor, double precision, double x, double y) {
		List<String> tt = new LinkedList<String>();
		List<String> ulayers = new LinkedList<String>();
		for(String ll : layers) {
			tt.add(ll.replace("{floor}", floor));
			ulayers.add(ll.replace("{floor}", ""));
		}
		layers = tt;
		double cx = convertLng(x);
		double cy = convertLat(y);
		int ifloor;
		try {
			ifloor = Integer.parseInt(floor);
		} catch (NumberFormatException e) {
			ifloor = 10; // "all" is translated to "10"
		}
		Map<String, String> replacement = new HashMap<String, String>();
		replacement.put("{layers}", TextUtils.join(",", layers));
		replacement.put("{ulayers}", TextUtils.join(",", ulayers)); // unsigned layers (i.e., without the floor number)
		replacement.put("{hl}", hl); // human language
		replacement.put("{precision}", "" + precision);
		replacement.put("{x}", "" + x);
		replacement.put("{y}", "" + y);
		replacement.put("{cx}", "" + cx); // converted x
		replacement.put("{cy}", "" + cy); // converted y
		replacement.put("{floor}", floor);
		replacement.put("{ifloor}", "" + ifloor); // integer floor
		for(Map.Entry<String, String> e : replacement.entrySet()) {
			url = url.replace(e.getKey(), e.getValue());
		}
		return url;
	} 

	private static final List<String> FIXED_LAYERS = Arrays.asList("locaux_h{floor}", "locaux_labels_en{floor}", "batiments_routes_labels");
	
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return MapController.class;
	}
	

	private MapController mController;
	private MapModel mModel;

	private ProgressDialog loading;
	

	private GoogleMap mMap;
    private TileOverlay mOsmOverlay;
    private TileOverlay mEpflOverlay;
    private GroundOverlay mGroundOverlay;
    private Map<Marker, MapItem> mMarkers = new HashMap<Marker, MapItem>();
    
	private LatLngBounds visibleRegion;
	private AsyncTask<Void, Void, BitmapDescriptor> mDownloader;

	private String floor = "all";
	private boolean epflLabels = true;
	private CameraUpdate epflView = null;
	private Set<String> layers = new HashSet<String>();
	
	private boolean searchMode = false;
	
	
	

	
	
	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {

//		layers.clear();
//		layers.addAll(Arrays.asList(
//				"parkings_publics{floor}", 
//				"arrets_metro{floor}", 
//				"transports_publics{floor}", 
//				"information{floor}"));
		
		mController = (MapController) controller;
		mModel = (MapModel) controller.getModel();

		setActionBarTitle(getString(R.string.map_plugin_title));
		setContentView(R.layout.map_main_view);


        Spinner spinner = (Spinner) findViewById(R.id.map_layers_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.map_layers_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				onGoogleLayerSelected(arg0, arg1, arg2, arg3);
			}
			public void onNothingSelected(AdapterView<?> arg0) {}
		});

        
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_main_fragment);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
			public void onMapReady(GoogleMap arg0) {
				onMapObjectReady(arg0);
			}
		});
        
        EditText searchField = (EditText) findViewById(R.id.map_search_edittext);
        searchField.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				onSearch(v.getText().toString());
		        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		        imm.hideSoftInputFromWindow(v.getWindowToken(), 0); 
				return true;
			}
		});
        searchField.setVisibility(View.GONE);
        
        ScrollView extraSettings = (ScrollView) findViewById(R.id.map_extra_settings);
        extraSettings.setVisibility(View.GONE);
        
        mController.getLayers();
        
//        updateActionBar();
	}

	
	@Override
	public void onBackPressed() {
		if(searchMode) {
			setSearchMode(false);
		} else {
			super.onBackPressed();
		}
	}
	
	private void setSearchMode(boolean state) {
        EditText searchField = (EditText) findViewById(R.id.map_search_edittext);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        
		if(state) {
	        searchField.setVisibility(View.VISIBLE);
	        searchField.setText("");
	        searchField.requestFocus();
	        mMap.setMyLocationEnabled(false);
	        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
			
		} else {
	        searchField.setVisibility(View.GONE);
	        mMap.setMyLocationEnabled(true);
	        imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0); 
	        showMarkers(null);
			
		}
		invalidateOptionsMenu();
		searchMode = state;
	}
	
	
/*
	private static String getAnnotationPictureUrl(LatLngBounds bnds, String floor, Set<String> layers) {
//		PointF sw = convert(bnds.southwest);
//		PointF ne = convert(bnds.northeast);
//		String bbox = "" + sw.x + "," + sw.y + "," + ne.x + "," + ne.y;
//		int w,h;
//		if(ne.x - sw.x > ne.y - sw.y) { // landscape
//			h = 480;
//			w = (int) (h * (ne.x - sw.x) / (ne.y - sw.y));
//		} else { // portrait
//			w = 480;
//			h = (int) (w * (ne.y - sw.y) / (ne.x - sw.x));
//		}
		
		
		// BBOX=731311,5863258,734565,5864324&WIDTH=1000&HEIGHT=1000
//		String layers = "" +
//				"parkings_publics" + floor + "," +
//				"arrets_metro" + floor + "," +
//				"transports_publics" + floor + "," +
//				"information" + floor + "," +
//				"locaux_h" + floor + "," +
//				"locaux_labels_en" + floor + "," +
//				"batiments_routes_labels";
		// LAYERS=parkings_publicsall,arrets_metroall,transports_publicsall,informationall,locaux_hall,locaux_labels_enall,batiments_routes_labels
		//String url = "http://plan.epfl.ch/wms_themes?FORMAT=image%2Fpng&LAYERS=" + layers + "&TRANSPARENT=TRUE&LOCALID=-1&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&STYLES=&SRS=EPSG%3A900913&BBOX=" + bbox + "&WIDTH=" + w + "&HEIGHT=" + h;
//		List<String> ll = new LinkedList<String>() { { addAll(layers); addAll(FIXED_LAYERS); } };
		//String url = replacePlaceholders(WMS_THEMES_URL, all, floor, bbox, h, w);
		String url = replacePlaceholders(WMS_THEMES_URL, getAllLayers(layers), floor, bnds.southwest.longitude, bnds.southwest.latitude, bnds.northeast.longitude, bnds.northeast.latitude, 480);
		System.out.println(url);
		return url;
	}
	*/
	
	private static List<String> getAllLayers(Set<String> layers) {
		List<String> all = new ArrayList<String>();
		all.addAll(layers);
		all.addAll(FIXED_LAYERS);
		return all;
	}
	
	
	private class Downloader extends AsyncTask<Void, Void, BitmapDescriptor> {
		LatLngBounds bnds;
		Set<String> pois;
		String epflFloor;
		@Override
		protected BitmapDescriptor doInBackground(Void... params) {
			bnds = visibleRegion;
			pois = new HashSet<String>(layers);
			epflFloor = floor;
			Bitmap btmp = null;
			int trial = 0;
			while(btmp == null && trial < 5) {
				String url = replacePlaceholders(WMS_THEMES_URL, getAllLayers(pois), epflFloor, bnds.southwest.longitude, bnds.southwest.latitude, bnds.northeast.longitude, bnds.northeast.latitude, 480);
				System.out.println(url);
				//btmp = getBitmapFromURL(getAnnotationPictureUrl(bnds, epflFloor, pois));
				btmp = getBitmapFromURL(url);
				trial++;
			}
			return (btmp == null ? null : BitmapDescriptorFactory.fromBitmap(btmp));
		}
		@Override
		protected void onPostExecute(BitmapDescriptor result) {
			synchronized (MapMainView.this) {
				if(!"".equals(floor) && epflLabels) {
			        showLabels(result, bnds);
				}
				mDownloader = null;
				if(!visibleRegion.equals(bnds) || !floor.equals(epflFloor) || !layers.equals(pois)) {
					onCamMove();
				}
			}
		}
	}

	
	private class Fetcher extends AsyncTask<LatLng, Void, MapItem> {
		LatLng point;
		@Override
		protected MapItem doInBackground(LatLng... params) {
			point = params[0];
			String aperture = replacePlaceholders("{aperture}", new LinkedList<String>(), "", visibleRegion.southwest.longitude, visibleRegion.southwest.latitude, visibleRegion.northeast.longitude, visibleRegion.northeast.latitude, 0);
			double precision = Double.parseDouble(aperture) / 100 + 1;
			String url = replacePlaceholders(QUERY_MAP_URL, Locale.getDefault().getLanguage(), getAllLayers(layers), floor, precision, point.longitude, point.latitude);
			System.out.println(url);
			String result = null;
			try {
				HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
				result = IOUtils.toString(connection.getInputStream(), "UTF-8");
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			System.out.println(result);
			if(result == null || "null".equals(result)) {
				return null;
			}
			try {
				JSONObject json = new JSONObject(result);
				MapItem item = new MapItem(json.getString("title"), point.latitude, point.longitude, 0, 0);
				item.setDescription(json.getString("content"));
				return item;
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		@Override
		protected void onPostExecute(MapItem item) {
			if(item != null) {
				//item.getDescription();
				xyz.clear();
				xyzL.clear();
				LinkedList<String> a = StringUtils.getAllSubstringsBetween(item.getDescription(), "<tr class=\"queryline\">", "</tr>");
				for(String s : a) {
					LinkedList<String> b = StringUtils.getAllSubstringsBetween(s, "<td class=\"querycell\">", "</td>");
					//StringBuilder sb = new StringBuilder();
					List<String> sb = new LinkedList<String>();
					String url = null;
					for(String t: b) {
						if(t.indexOf("target=\"_blank\"") != -1) {
							url = StringUtils.getSubstringBetween(t, "<a href=\"", "\"");
						} else {
							String p = t.trim();
							if(p.length() > 0) {
								sb.add(p);
							}
							//sb.append(t.trim() + "\n");
						}
					}
					xyz.add(TextUtils.join("\n", sb));
					xyzL.add(url);
				}
				item.setDescription(null);
				item.setCategory("XYZ");
				showMarkers(Arrays.asList(item));
				//mModel.setSearchResult();
			}
		}
	}
	private List<String> xyz = new LinkedList<String>();
	private List<String> xyzL = new LinkedList<String>();
	
	private void onCamMove() {
		synchronized (MapMainView.this) {
			visibleRegion = mMap.getProjection().getVisibleRegion().latLngBounds;
			if("".equals(floor) || !epflLabels)
				return;
			if(mDownloader == null) {
				mDownloader = new Downloader().execute();
			}
		}
		
	}
	
    private void onMapObjectReady(GoogleMap map1) {
    	mMap = map1;

        //map.getUiSettings().setZoomControlsEnabled(true); // show zoom buttons
        mMap.setTrafficEnabled(false);
        mMap.setMyLocationEnabled(true);
        mMap.setBuildingsEnabled(false);
        mMap.setIndoorEnabled(false);
        
        // center on EPFL
        CameraPosition epflPosition = new CameraPosition.Builder().target(new LatLng(46.518, 6.567)).zoom(14.7f).bearing(0).tilt(0).build();
        //epflView = CameraUpdateFactory.newLatLngZoom(new LatLng(46.518, 6.567), 14.7f);
        epflView = CameraUpdateFactory.newCameraPosition(epflPosition);
    	mMap.moveCamera(epflView);
        
    	mMap.setOnCameraChangeListener(new OnCameraChangeListener() {
			public void onCameraChange(CameraPosition position) {
				onCamMove();
			}
		});
    	mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			public boolean onMarkerClick(Marker marker) {
				synchronized (MapMainView.this) {
					MapItem i = mMarkers.get(marker);
					trackEvent("PinViewMoreInfo", i.getTitle());
					if(i.isSetFloor()) {
						changeEpflFloor("" + i.getFloor());
					}
				}
				return false; // don't consume the event (so that the map centers on this marker, and info window appears)
			}
		});
    	mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			public void onInfoWindowClick(Marker marker) {
				synchronized (MapMainView.this) {
					MapItem i = mMarkers.get(marker);
					if("persons".equals(i.getCategory())) {
						searchDirectory(i.getTitle());
					} else if("XYZ".equals(i.getCategory())) {
						displayXyzSearchResults();
					}
				}
			}
		});
    	mMap.setOnMapClickListener(new OnMapClickListener() {
			public void onMapClick(LatLng point) {
				if(searchMode) {
					
				} else {
					trackEvent("TapOnMap", point.latitude + "," + point.longitude);
//					PointF p = convert(point);
					showMarkers(null);
					new Fetcher().execute(point);
				}
			}
		});
    	
    	showEpfl();
    	handleSearchIntent(getIntent());
    }
    
	private void searchDirectory(String str) {
		try {
			Intent i = new Intent();
			i.setAction(Intent.ACTION_VIEW);
			Uri.Builder uri = new Uri.Builder();
			uri.scheme("pocketcampus").authority("directory.plugin.pocketcampus.org").appendPath("query").appendQueryParameter("q", str);
			//System.out.println(uri.build());
			i.setData(uri.build());
			//i.putExtra("MapElement", r.location);
			startActivity(i);
		} catch (Exception e) {
			// Should never happen
			Toast.makeText(getApplicationContext(), "The Directory plugin is not installed??", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		
		if(!searchMode) {
			MenuItem i5 = menu.add("search");
			i5.setTitle(R.string.map_search);
			i5.setIcon(R.drawable.map_search_action);
			i5.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				public boolean onMenuItemClick(MenuItem item) {
					trackEvent("SearchAction", null);
					setSearchMode(true);
					return true;
				}
			});
			i5.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}
		

		
		MenuItem i1 = menu.add("floors");
		//i1.setIcon(android.R.drawable.ic_dialog_dialer);
//		Spinner spinner1 = (Spinner) findViewById(R.id.map_epfl_floors_spinner);
//		int index = spinner.getSelectedItemPosition() + delta;
//		spinner.setSelection(index, true);
		// i1.setActionView(R.layout.test_layout);
		//i1.setActionView(spinner1);
		//String sel = spinner1.getSelectedItem().toString();
		i1.setTitle(mModel.getEpflFloors().get(floor));
		i1.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		i1.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				trackEvent("ChangeFloorAction", null);
				DialogUtils.showSingleChoiceDialog(MapMainView.this, mModel.getEpflFloors(), null, floor, new SingleChoiceHandler<String>() {
					public void saveSelection(String t) {
						trackEvent("ChangeFloor", t);
						changeEpflFloor(t);
					}
				}, mModel.getFloorKeyComparator());
				return true;
			}
		});
		   
//		   MenuItem i2 = menu.add("Helo");
//		   i2.setTitle("hola");
//		   i2.setIcon(android.R.drawable.ic_dialog_dialer);
//		   Spinner spinner2 = (Spinner) findViewById(R.id.map_layers_spinner);
//		   //i2.setActionView(R.layout.test_layout);
//		   i2.setActionView(spinner2);
//		   i2.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		   
		if(!searchMode) {
			MenuItem i3 = menu.add("center on epfl");
			//i3.setTitle(R.string.map_menu_campus_position);
			i3.setTitle("EPFL");
			//i3.setIcon(R.drawable.map_center_on_epfl2);
			i3.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				public boolean onMenuItemClick(MenuItem item) {
					trackEvent("CenterOnCampus", null);
					mMap.animateCamera(epflView);
					return true;
				}
			});
			i3.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}

		MenuItem i4 = menu.add("layers");
		i4.setTitle(R.string.map_layer_pick_text);
		i4.setIcon(R.drawable.map_select_layers);
		i4.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				trackEvent("ShowLayersList", null);
				DialogUtils.showMultiChoiceDialog(MapMainView.this, mModel.getLayerNames(), null, layers, new MultiChoiceHandler<String>() {
					public void saveSelection(String t, boolean isChecked) {
						if (isChecked) {
							trackEvent("IncludeLayer", t);
							layers.add(t);
						} else {
							trackEvent("ExcludeLayer", t);
							layers.remove(t);
						}
						onCamMove();
						
					}
				});
				return true;
			}
		});
		i4.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		
		if(searchMode) {
			MenuItem i5 = menu.add("show results as list");
			i5.setTitle("Show as list"); // TODO
			i5.setIcon(R.drawable.map_show_results_as_list);
			i5.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				public boolean onMenuItemClick(MenuItem item) {
					displaySearchResultsAsList();
					return true;
				}
			});
			i5.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}
		
		if(searchMode) {
			MenuItem i5 = menu.add("exit search mode");
			i5.setTitle(R.string.map_menu_clear_layers); // TODO
			i5.setIcon(R.drawable.map_exit_search);
			i5.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				public boolean onMenuItemClick(MenuItem item) {
					setSearchMode(false);
					return true;
				}
			});
			i5.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}
		
		
		return true;
	}
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		menu.clear();
//		//MenuInflater inflater = getMenuInflater();
//		  // inflater.inflate(R.menu.test_menu, menu);
//		   MenuItem i = menu.add("Helo");
//		   i.setTitle("hola");
//		   i.setIcon(android.R.drawable.ic_dialog_dialer);
//		   i.setActionView(R.layout.test_layout);
//		   i.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//		   //return super.onCreateOptionsMenu(menu);
//		return true;
//	}

    
//    
//	private void updateActionBar() {
//		removeAllActionsFromActionBar();
//		addActionToActionBar(new Action() {
//			@Override
//			public void performAction(View view) {
//				onSearchRequested();
//				DialogUtils.showInputDialog(MapMainView.this, getString(R.string.map_search), "", getString(R.string.map_search), new DialogUtils.TextInputHandler(){
//					public void gotText(String s) {
//						onSearch(s);
//					}});
//				trackEvent("Search", null);
//			}
//
//			@Override
//			public int getDrawable() {
//				return R.drawable.map_search_action;
//			}
//
//			@Override
//			public String getDescription() {
//				return getString(R.string.map_search);
//			}
//		});
////		addActionToActionBar(new Action() {
////			@Override
////			public void performAction(View view) {
////				mMap.animateCamera(epflView);
////			}
////
////			@Override
////			public int getDrawable() {
////				return R.drawable.map_icon;
////			}
////
////			@Override
////			public String getDescription() {
////				return getString(R.string.map_menu_campus_position);
////			}
////		});
//		
////		addActionToActionBar(new Action() {
////			@Override
////			public void performAction(View view) {
////				changeEpflSpinner(1);
////			}
////
////			@Override
////			public int getDrawable() {
////				return android.R.drawable.btn_minus;
////			}
////
////			@Override
////			public String getDescription() {
////				return "-"; 
////			}
////		});
////		addActionToActionBar(new Action() {
////			@Override
////			public void performAction(View view) {
////				changeEpflSpinner(-1);
////			}
////
////			@Override
////			public int getDrawable() {
////				return android.R.drawable.btn_plus;
////			}
////
////			@Override
////			public String getDescription() {
////				return "+"; 
////			}
////		});
//	}
//

	
    synchronized private void showMarkers(List<MapItem> items) {
    	for(Marker m : mMarkers.keySet()) {
    		m.remove();
    	}
    	mMarkers.clear();
    	if(items == null || items.size() == 0) {
    		return;
    	}
    	if(items.size() == 1 && items.get(0).isSetFloor()) {
    		changeEpflFloor("" + items.get(0).getFloor());
    	} else {
//    		changeEpflFloor("all");
    	}
    	for(MapItem i : items) {
    		MarkerOptions opt = new MarkerOptions();
    		opt.position(new LatLng(i.getLatitude(), i.getLongitude()));
    		opt.title(i.getTitle());
    		if(i.isSetDescription())
    			opt.snippet(i.getDescription());
    		Marker m = mMap.addMarker(opt);
        	if(items.size() == 1) {
        		m.showInfoWindow();
        	}
    		mMarkers.put(m, i);
    	}
    }
    
    synchronized private void changeEpflFloor(String flr) {
		floor = flr;
		toggleEpflFloors();
		invalidateOptionsMenu();
//		int res = getResources().getIdentifier("epfl_floor_" + flr, "string", getPackageName());
//		if(res != 0) {
//			Spinner spinner = (Spinner) findViewById(R.id.map_epfl_floors_spinner);
//			for(int i = 0; i < spinner.getAdapter().getCount(); i++) {
//				if(spinner.getAdapter().getItem(i).toString().equals(getString(res))) {
//					spinner.setSelection(i, true);
//					break;
//				}
//			}
//            //setEpflLayer(getString(res));
//		}
    }
    
//    private void changeEpflSpinner(int delta) {
//		Spinner spinner = (Spinner) findViewById(R.id.map_epfl_floors_spinner);
//		int index = spinner.getSelectedItemPosition() + delta;
//		index = Math.min(spinner.getCount() - 1, Math.max(0, index));
//		spinner.setSelection(index, true);
//    }
    
    synchronized private void adaptCamera() {
    	// http://stackoverflow.com/questions/14828217/android-map-v2-zoom-to-show-all-the-markers
    	if(mMarkers.size() == 0) {
    		return;
    	}
    	LatLngBounds.Builder builder = new LatLngBounds.Builder();
    	for (Marker marker : mMarkers.keySet()) {
    	    builder.include(marker.getPosition());
    	}
    	LatLngBounds bounds = builder.build();
    	CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 10);
    	mMap.animateCamera(cu);
    }
    
    
    private void displayXyzSearchResults() {
    	if(xyz.size() == 0) {
    		return;
    	}
    	
    	trackEvent("TapOnMapShowResults", null);

        AlertDialog sdb = new AlertDialog.Builder(this)
        .setItems(xyz.toArray(new String[xyz.size()]), new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String url = xyzL.get(which);
				if(url != null) {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
				}

			}
		})
        .create();
        sdb.setCanceledOnTouchOutside(true);
        sdb.show();

    }
    
    synchronized private  void displaySearchResultsAsList() {
    	if(mMarkers.size() == 0) {
			DialogUtils.alert(this, getString(R.string.map_plugin_title), getString(R.string.map_search_no_results));
    		return;
    	}
    	
    	trackEvent("ShowResultsList", null);
    	
    	final List<Marker> markers = new ArrayList<Marker>(mMarkers.keySet());
    	Collections.sort(markers, new Comparator<Marker>() {
			public int compare(Marker arg0, Marker arg1) {
				return arg0.getTitle().compareTo(arg1.getTitle());
			}
		});
    	String [] titles = new String[markers.size()];
    	for(int i = 0; i < titles.length; i++) {
    		titles[i] = markers.get(i).getTitle();
    	}
    	
//    	final List<String> texts = new LinkedList<String>();
//    	final List<Marker> markers = new LinkedList<Marker>();
//    	for (Map.Entry<Marker, MapItem> e : mMarkers.entrySet()) {
//    		texts.add(e.getValue().getTitle());
//    		markers.add(e.getKey());
//    	}
    	
        AlertDialog sdb = new AlertDialog.Builder(this)
        .setItems(titles, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Marker marker = markers.get(which);
				trackEvent("SelectResultFromList", marker.getTitle());
		    	LatLngBounds.Builder builder = new LatLngBounds.Builder();
	    	    builder.include(marker.getPosition());
	        	LatLngBounds bounds = builder.build();
	        	CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
	        	mMap.animateCamera(cu);
	        	marker.showInfoWindow();
				MapItem i = mMarkers.get(marker);
				if(i.isSetFloor()) {
					changeEpflFloor("" + i.getFloor());
				}

			}
		})
        .create();
        sdb.setCanceledOnTouchOutside(true);
        sdb.show();

    }
	
	private void onSearch(String query) {
		if("ok maps".equalsIgnoreCase(query)) {
	        ScrollView extraSettings = (ScrollView) findViewById(R.id.map_extra_settings);
	        extraSettings.setVisibility(View.VISIBLE);
			return;
		};
		trackEvent("Search", query);
		mController.search(query);
		loading = ProgressDialog.show(this, null, getString(R.string.map_searching), true, false);
		
	}
	
	private void handleSearchIntent(Intent intent) {
		
		Uri aData = intent.getData();
		if (aData != null && aData.getQueryParameter("q") != null) {
			String query = aData.getQueryParameter("q");
			onSearch(query);
		}
		
		Bundle extras = intent.getExtras();
		if (extras != null && extras.containsKey("MapElement")) {
			MapItem meb = (MapItem) extras.getSerializable("MapElement");
			showMarkers(Arrays.asList(meb));
			adaptCamera();
		}
	}

	@Override
	protected String screenName() {
		return "/map";
	}


	@Override
	public void networkErrorHappened() {
		Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.sdk_connection_error_happened),
				Toast.LENGTH_SHORT); 
		toast.show();
		loading.dismiss();
	}

	@Override
	public void searchResultsUpdated() {
		loading.dismiss();
		
		showMarkers(mModel.getSearchResults());
		adaptCamera();

		if(mModel.getSearchResults().size() < 1) {
			DialogUtils.alert(this, getString(R.string.map_plugin_title), getString(R.string.map_search_no_results));
		}
	}
	
	

    public void onTrafficToggled(View v) {
        mMap.setTrafficEnabled(((CheckBox) v).isChecked());
    }
    public void onMyLocationToggled(View v) {
        mMap.setMyLocationEnabled(((CheckBox) v).isChecked());
    }
    public void onBuildingsToggled(View v) {
        mMap.setBuildingsEnabled(((CheckBox) v).isChecked());
    }
    public void onIndoorToggled(View v) {
        mMap.setIndoorEnabled(((CheckBox) v).isChecked());
    }


    
    
    public void toggleOsm(View v) {
    	removeOsm();
    	if(((CheckBox) v).isChecked()) {
    		showOsm();
    	}
    }
    
    

    public void toggleEpflLabels(View v) {
    	if(((CheckBox) v).isChecked()) {
    		epflLabels = true;
    		onCamMove();
    	} else {
    		epflLabels = false;
    		removeLabels();
    	}
    }

    private void toggleEpflFloors() {
    	removeEpfl();
    	removeLabels();
    	if(!"".equals(floor)) {
    		showEpfl();
    		onCamMove();
    	}
    }

    
    public void onGoogleLayerSelected(AdapterView<?> parent, View view, int position, long id) {
        // This is also called by the Android framework in onResume(). The map may not be created at
        // this stage yet.
        if (mMap != null) {
        	setGoogleLayer((String) parent.getItemAtPosition(position));
        }
    }

//    public void onEpflFloorSelected(AdapterView<?> parent, View view, int position, long id) {
//        // This is also called by the Android framework in onResume(). The map may not be created at
//        // this stage yet.
//        if (mMap != null) {
//            setEpflFloor((String) parent.getItemAtPosition(position));
//        }
//    }

    private void setGoogleLayer(String layerName) {
        if (layerName.equals(getString(R.string.map_normal))) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        } else if (layerName.equals(getString(R.string.map_hybrid))) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        } else if (layerName.equals(getString(R.string.map_satellite))) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else if (layerName.equals(getString(R.string.map_terrain))) {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        } else if (layerName.equals(getString(R.string.map_none))) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
        } else {
            Log.i("LDA", "Error setting layer with name " + layerName);
        }
    }
    
//    private void setEpflFloor(String layerName) {
//        if (layerName.equals(getString(R.string.epfl_floor_all))) {
//            floor = "all";
//        } else if (layerName.equals(getString(R.string.epfl_floor_8))) {
//        	floor = "8";
//        } else if (layerName.equals(getString(R.string.epfl_floor_7))) {
//        	floor = "7";
//        } else if (layerName.equals(getString(R.string.epfl_floor_6))) {
//        	floor = "6";
//        } else if (layerName.equals(getString(R.string.epfl_floor_5))) {
//        	floor = "5";
//        } else if (layerName.equals(getString(R.string.epfl_floor_4))) {
//        	floor = "4";
//        } else if (layerName.equals(getString(R.string.epfl_floor_3))) {
//        	floor = "3";
//        } else if (layerName.equals(getString(R.string.epfl_floor_2))) {
//        	floor = "2";
//        } else if (layerName.equals(getString(R.string.epfl_floor_1))) {
//        	floor = "1";
//        } else if (layerName.equals(getString(R.string.epfl_floor_0))) {
//        	floor = "0";
//        } else if (layerName.equals(getString(R.string.epfl_floor_b1))) {
//        	floor = "-1";
//        } else if (layerName.equals(getString(R.string.epfl_floor_b2))) {
//        	floor = "-2";
//        } else if (layerName.equals(getString(R.string.epfl_floor_b3))) {
//        	floor = "-3";
//        } else if (layerName.equals(getString(R.string.epfl_floor_b4))) {
//        	floor = "-4";
//        } else if (layerName.equals(getString(R.string.map_none))) {
//        	floor = "";
//        } else {
//            Log.i("LDA", "Error setting layer with name " + layerName);
//        }
//        toggleEpflFloors();
//    }

	
	
	
	
	
	
	/*****************
	 * HELPERS
	 */
	
	

    synchronized private void showOsm() {
    	removeOsm();
    	final Random rand = new Random();
        TileProvider osmProvider = new UrlTileProvider(256, 256) {
            @Override
            public synchronized URL getTileUrl(int x, int y, int zoom) {
                // The moon tile coordinate system is reversed.  This is not normal.
                //int reversedY = (1 << zoom) - y - 1;
                // http://plan-osm-tile4.epfl.ch/15/16982/11590.png
                // http://plan-epfl-tile0.epfl.ch/batiments-4-merc/18/000/135/850/000/169/428.png
            	//String sx = TextUtils.join("/", String.format("%09d", x).split("(?<=\\G.{3})/u"));
            	//String sy = TextUtils.join("/", String.format("%09d", y).split("(?<=\\G.{3})/u"));
            	
            	//String sz = String.format("%02d", zoom);
                //String s = String.format(Locale.US, MOON_MAP_URL_FORMAT, zoom, x, reversedY);
            	String s = replacePlaceholders(OSM_TILE_URL, rand.nextInt(5), "", zoom, x, y);
            	//String s = "http://plan-osm-tile" + rand.nextInt(5) + ".epfl.ch/" + zoom + "/" + x + "/" + y + ".png";
                System.out.println(s);
                URL url = null;
                try {
                    url = new URL(s);
                } catch (MalformedURLException e) {
                    throw new AssertionError(e);
                }
                return url;
            }
        };
        mOsmOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(osmProvider));
	}
    
    synchronized private void removeOsm() {
    	if(mOsmOverlay != null) {
    		mOsmOverlay.remove();
    		mOsmOverlay = null;
    	}
	}




    synchronized private void showEpfl() {
    	removeEpfl();
    	final Random rand = new Random();
        TileProvider epflProvider = new UrlTileProvider(256, 256) {
            @Override
            public synchronized URL getTileUrl(int x, int y, int zoom) {
                // The moon tile coordinate system is reversed.  This is not normal.
//                int reversedY = (1 << zoom) - y - 1;
                // http://plan-osm-tile4.epfl.ch/15/16982/11590.png
                // http://plan-epfl-tile0.epfl.ch/batiments-4-merc/18/000/135/850/000/169/428.png
            	//String sx = TextUtils.join("/", String.format("%09d", x).split("(?<=\\G.{3})/u"));
            	//String sy = TextUtils.join("/", String.format("%09d", y).split("(?<=\\G.{3})/u"));
            	
//            	DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
//				DecimalFormatSymbols customSymbol = new DecimalFormatSymbols();
//				customSymbol.setGroupingSeparator('/');
//				formatter.setDecimalFormatSymbols(customSymbol);
//				String sx = formatter.format(x + 1000000000).substring(2);
//				String sy = formatter.format(reversedY + 1000000000).substring(2);
//            	
//            	String sz = String.format(Locale.US, "%02d", zoom);
                //String s = String.format(Locale.US, MOON_MAP_URL_FORMAT, zoom, x, reversedY);
            	//String s = "http://plan-epfl-tile" + rand.nextInt(5) + ".epfl.ch/batiments" + layer + "-merc/" + sz + "/" + sx + "/" + sy + ".png";
            	String s = replacePlaceholders(EPFL_TILE_URL, rand.nextInt(5), floor, zoom, x, y);
            	System.out.println(s);
                URL url = null;
                try {
                    url = new URL(s);
                } catch (MalformedURLException e) {
                    throw new AssertionError(e);
                }
                return url;
            }
        };
        mEpflOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(epflProvider));;
	}
    
    synchronized private void removeEpfl() {
    	if(mEpflOverlay != null) {
    		mEpflOverlay.remove();
    		mEpflOverlay = null;
    	}
	}

    
	synchronized private void showLabels(BitmapDescriptor bd, LatLngBounds bounds) {
		removeLabels();
		if(bd == null)
			return;
		mGroundOverlay = mMap.addGroundOverlay(new GroundOverlayOptions()
				.image(bd).positionFromBounds(bounds).zIndex(100000000));
	}
    
	synchronized private void removeLabels() {
		if (mGroundOverlay != null) {
			mGroundOverlay.remove();
			mGroundOverlay = null;
		}
	}
    

	

    /*********
     * https://gist.github.com/springmeyer/871897
     */
    public static PointF convert(LatLng epsg4326) {
    	double longitude = epsg4326.longitude * 20037508.34 / 180;
    	double latitude = Math.log(Math.tan((90 + epsg4326.latitude) * Math.PI / 360)) / (Math.PI / 180);
    	latitude = latitude * 20037508.34 / 180;
    	return new PointF((float) longitude, (float) latitude);
    }
    public static double convertLat(double lat) {
    	lat = Math.log(Math.tan((90 + lat) * Math.PI / 360)) / (Math.PI / 180);
    	lat = lat * 20037508.34 / 180;
    	return lat;
    }
    public static double convertLng(double lng) {
    	lng = lng * 20037508.34 / 180;
    	return lng;
    }
    
    /*****
     * http://stackoverflow.com/questions/8992964/android-load-from-url-to-bitmap
     */
	public static Bitmap getBitmapFromURL(String src) {
		try {
			URL url = new URL(src);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
