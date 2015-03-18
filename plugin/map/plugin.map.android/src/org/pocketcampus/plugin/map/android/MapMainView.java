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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginView;
import org.pocketcampus.platform.android.utils.DialogUtils;
import org.pocketcampus.platform.android.utils.DialogUtils.MultiChoiceHandler;
import org.pocketcampus.plugin.map.R;
import org.pocketcampus.plugin.map.android.iface.IMapView;
import org.pocketcampus.plugin.map.shared.MapItem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.Spinner;
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
import com.markupartist.android.widget.Action;

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
		replacement.put("{lb}", "" + lb);
		replacement.put("{floor}", floor);
		replacement.put("{z}", "" + z);
		replacement.put("{x}", "" + x);
		replacement.put("{y}", "" + y);
		replacement.put("{rx}", "" + rx);
		replacement.put("{ry}", "" + ry);
		replacement.put("{sx}", sx);
		replacement.put("{sy}", sy);
		replacement.put("{srx}", srx);
		replacement.put("{sry}", sry);
		replacement.put("{fz}", fz);
		for(Map.Entry<String, String> e : replacement.entrySet()) {
			url = url.replace(e.getKey(), e.getValue());
		}
		return url;
	}
	
	private static final String WMS_THEMES_URL = "http://plan.epfl.ch/wms_themes?FORMAT=image%2Fpng&LAYERS={layers}&TRANSPARENT=TRUE&LOCALID=-1&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&STYLES=&SRS=EPSG%3A900913&BBOX={bbox}&WIDTH={w}&HEIGHT={h}";
	
	private static String replacePlaceholders(String url, List<String> layers, String floor, String bbox, int h, int w) {
		List<String> t = new LinkedList<String>();
		for(String l : layers) {
			t.add(l.replace("{floor}", floor));
		}
		layers = t;
		Map<String, String> replacement = new HashMap<String, String>();
		replacement.put("{layers}", TextUtils.join(",", layers));
		replacement.put("{bbox}", bbox);
		replacement.put("{h}", "" + h);
		replacement.put("{w}", "" + w);
		for(Map.Entry<String, String> e : replacement.entrySet()) {
			url = url.replace(e.getKey(), e.getValue());
		}
		return url;
	} 

	private static final String QUERY_MAP_URL = "http://plan.epfl.ch/search/xyz/?lang={hl}&tolerance=0.37322767710685734&lon={cx}&lat={cy}&layers__eq={ulayers}&queryable=layers,floor&floor__eq={ifloor}";
	
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
	
	
	

	
	
	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {

		layers.clear();
		layers.addAll(Arrays.asList(
				"parkings_publics{floor}", 
				"arrets_metro{floor}", 
				"transports_publics{floor}", 
				"information{floor}"));
		
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

        Spinner spinner1 = (Spinner) findViewById(R.id.map_epfl_layers_spinner);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(
                this, R.array.map_epfl_layers_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);
        spinner1.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				onEpflLayerSelected(arg0, arg1, arg2, arg3);
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
        
        ScrollView extraSettings = (ScrollView) findViewById(R.id.map_extra_settings);
        extraSettings.setVisibility(View.GONE);
        
        mController.getLayers();
        
        updateActionBar();
	}

	
	

	private static String getAnnotationPictureUrl(LatLngBounds bnds, String floor, Set<String> layers) {
//		System.out.println(bnds.southwest + " " + bnds.northeast);
//		System.out.println(convert(bnds.southwest) + " " + convert(bnds.northeast));
		PointF sw = convert(bnds.southwest);
		PointF ne = convert(bnds.northeast);
		String bbox = "" + sw.x + "," + sw.y + "," + ne.x + "," + ne.y;
		int w,h;
		if(ne.x - sw.x > ne.y - sw.y) { // landscape
			h = 480;
			w = (int) (h * (ne.x - sw.x) / (ne.y - sw.y));
		} else { // portrait
			w = 480;
			h = (int) (w * (ne.y - sw.y) / (ne.x - sw.x));
		}
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
		List<String> all = new ArrayList<String>();
		all.addAll(layers);
		all.addAll(FIXED_LAYERS);
		String url = replacePlaceholders(WMS_THEMES_URL, all, floor, bbox, h, w);
		System.out.println(url);
		return url;
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
				btmp = getBitmapFromURL(getAnnotationPictureUrl(bnds, epflFloor, pois));
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
        epflView = CameraUpdateFactory.newLatLngZoom(new LatLng(46.518, 6.567), (float)14.7);
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
					changeEpflFloor(i.getFloor());
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
					}
				}
			}
		});
    	mMap.setOnMapClickListener(new OnMapClickListener() {
			public void onMapClick(LatLng point) {
				PointF p = convert(point);
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
		
		MenuItem i1 = menu.add("Helo");
		i1.setTitle("hola");
		i1.setIcon(android.R.drawable.ic_dialog_dialer);
		Spinner spinner1 = (Spinner) findViewById(R.id.map_epfl_layers_spinner);
		// i1.setActionView(R.layout.test_layout);
		i1.setActionView(spinner1);
		i1.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		   
//		   MenuItem i2 = menu.add("Helo");
//		   i2.setTitle("hola");
//		   i2.setIcon(android.R.drawable.ic_dialog_dialer);
//		   Spinner spinner2 = (Spinner) findViewById(R.id.map_layers_spinner);
//		   //i2.setActionView(R.layout.test_layout);
//		   i2.setActionView(spinner2);
//		   i2.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		   
		MenuItem i3 = menu.add("Helo");
		i3.setTitle(R.string.map_menu_campus_position);
		i3.setIcon(R.drawable.map_icon);
		i3.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				mMap.animateCamera(epflView);
				return true;
			}
		});
		i3.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		MenuItem i4 = menu.add("Helo");
		i4.setTitle(R.string.map_layer_pick_text);
		i4.setIcon(R.drawable.map_icon);
		i4.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				DialogUtils.showMultiChoiceDialog(MapMainView.this, mModel.getLayerNames(), getString(R.string.map_layer_pick_text), layers, new MultiChoiceHandler<String>() {
					public void saveSelection(String t, boolean isChecked) {
						if (isChecked)
							layers.add(t);
						else
							layers.remove(t);
						onCamMove();
						
					}
				});
				return true;
			}
		});
		i4.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

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

    
    
	private void updateActionBar() {
		removeAllActionsFromActionBar();
		addActionToActionBar(new Action() {
			@Override
			public void performAction(View view) {
				onSearchRequested();
				DialogUtils.showInputDialog(MapMainView.this, "Map", "Search for (TODO replace me)", "Go", new DialogUtils.TextInputHandler(){
					public void gotText(String s) {
						onSearch(s);
					}});
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
//		addActionToActionBar(new Action() {
//			@Override
//			public void performAction(View view) {
//				mMap.animateCamera(epflView);
//			}
//
//			@Override
//			public int getDrawable() {
//				return R.drawable.map_icon;
//			}
//
//			@Override
//			public String getDescription() {
//				return getString(R.string.map_menu_campus_position);
//			}
//		});
		
//		addActionToActionBar(new Action() {
//			@Override
//			public void performAction(View view) {
//				changeEpflSpinner(1);
//			}
//
//			@Override
//			public int getDrawable() {
//				return android.R.drawable.btn_minus;
//			}
//
//			@Override
//			public String getDescription() {
//				return "-"; // TODO
//			}
//		});
//		addActionToActionBar(new Action() {
//			@Override
//			public void performAction(View view) {
//				changeEpflSpinner(-1);
//			}
//
//			@Override
//			public int getDrawable() {
//				return android.R.drawable.btn_plus;
//			}
//
//			@Override
//			public String getDescription() {
//				return "+"; // TODO
//			}
//		});
	}


	
    synchronized private void showMarkers(List<MapItem> items) {
    	for(Marker m : mMarkers.keySet()) {
    		m.remove();
    	}
    	mMarkers.clear();
    	if(items.size() == 1 && items.get(0).isSetFloor()) {
    		changeEpflFloor(items.get(0).getFloor());
    	}
    	for(MapItem i : items) {
    		MarkerOptions opt = new MarkerOptions();
    		opt.position(new LatLng(i.getLatitude(), i.getLongitude()));
    		opt.title(i.getTitle());
    		if(i.isSetDescription())
    			opt.snippet(i.getDescription());
    		mMarkers.put(mMap.addMarker(opt), i);
    	}
    }
    
    synchronized private void changeEpflFloor(int floor) {
		int res = getResources().getIdentifier("epfl_floor_" + floor, "string", getPackageName());
		if(res != 0) {
			Spinner spinner = (Spinner) findViewById(R.id.map_epfl_layers_spinner);
			for(int i = 0; i < spinner.getAdapter().getCount(); i++) {
				if(spinner.getAdapter().getItem(i).toString().equals(getString(res))) {
					spinner.setSelection(i, true);
					break;
				}
			}
            //setEpflLayer(getString(res));
		}
    }
    
    private void changeEpflSpinner(int delta) {
		Spinner spinner = (Spinner) findViewById(R.id.map_epfl_layers_spinner);
		int index = spinner.getSelectedItemPosition() + delta;
		index = Math.min(spinner.getCount() - 1, Math.max(0, index));
		spinner.setSelection(index, true);
    }
    
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
    
	
	private void onSearch(String query) {
		if("ok maps".equalsIgnoreCase(query)) {
	        ScrollView extraSettings = (ScrollView) findViewById(R.id.map_extra_settings);
	        extraSettings.setVisibility(View.VISIBLE);
			return;
		};
		mController.search(query);
		loading = ProgressDialog.show(this, null, null, true, false);
		
	}
	
	private void handleSearchIntent(Intent intent) {
		
		Uri aData = intent.getData();
		if (aData != null && aData.getQueryParameter("q") != null) {
			String query = aData.getQueryParameter("q");
			mController.search(query);
			loading = ProgressDialog.show(this, null, null, true, false);
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

    public void onEpflLayerSelected(AdapterView<?> parent, View view, int position, long id) {
        // This is also called by the Android framework in onResume(). The map may not be created at
        // this stage yet.
        if (mMap != null) {
            setEpflLayer((String) parent.getItemAtPosition(position));
        }
    }

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
    
    private void setEpflLayer(String layerName) {
        if (layerName.equals(getString(R.string.epfl_floor_all))) {
            floor = "all";
        } else if (layerName.equals(getString(R.string.epfl_floor_8))) {
        	floor = "8";
        } else if (layerName.equals(getString(R.string.epfl_floor_7))) {
        	floor = "7";
        } else if (layerName.equals(getString(R.string.epfl_floor_6))) {
        	floor = "6";
        } else if (layerName.equals(getString(R.string.epfl_floor_5))) {
        	floor = "5";
        } else if (layerName.equals(getString(R.string.epfl_floor_4))) {
        	floor = "4";
        } else if (layerName.equals(getString(R.string.epfl_floor_3))) {
        	floor = "3";
        } else if (layerName.equals(getString(R.string.epfl_floor_2))) {
        	floor = "2";
        } else if (layerName.equals(getString(R.string.epfl_floor_1))) {
        	floor = "1";
        } else if (layerName.equals(getString(R.string.epfl_floor_0))) {
        	floor = "0";
        } else if (layerName.equals(getString(R.string.epfl_floor_b1))) {
        	floor = "-1";
        } else if (layerName.equals(getString(R.string.epfl_floor_b2))) {
        	floor = "-2";
        } else if (layerName.equals(getString(R.string.epfl_floor_b3))) {
        	floor = "-3";
        } else if (layerName.equals(getString(R.string.epfl_floor_b4))) {
        	floor = "-4";
        } else if (layerName.equals(getString(R.string.map_none))) {
        	floor = "";
        } else {
            Log.i("LDA", "Error setting layer with name " + layerName);
        }
        toggleEpflFloors();
    }

	
	
	
	
	
	
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
