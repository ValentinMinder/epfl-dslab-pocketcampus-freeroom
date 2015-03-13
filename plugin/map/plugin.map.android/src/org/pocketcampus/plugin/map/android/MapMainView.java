package org.pocketcampus.plugin.map.android;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginView;
import org.pocketcampus.platform.android.utils.DialogUtils;
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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
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
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return MapMainController.class;
	}

	private MapMainController mController;
	private MapModel mModel;

	private ProgressDialog loading;
	

	private GoogleMap mMap;
    private TileOverlay mOsmOverlay;
    private TileOverlay mEpflOverlay;
    private GroundOverlay mGroundOverlay;
    private Map<Marker, MapItem> mMarkers = new HashMap<Marker, MapItem>();
    
	private LatLngBounds visibleRegion;
	private AsyncTask<LatLngBounds, Void, BitmapDescriptor> mDownloader;

	private String layer = "all";
	private boolean epflLabels = true;
	private CameraUpdate epflView = null;
	

	
	
	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {

		mController = (MapMainController) controller;
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
        
        updateActionBar();
	}

	
	

	private String getAnnotationPictureUrl(LatLngBounds bnds) {
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
		String layers = "parkings_publics" + layer + ",arrets_metro" + layer + ",transports_publics" + layer + ",information" + layer + ",locaux_h" + layer + ",locaux_labels_en" + layer + ",batiments_routes_labels";
		// LAYERS=parkings_publicsall,arrets_metroall,transports_publicsall,informationall,locaux_hall,locaux_labels_enall,batiments_routes_labels
		String url = "http://plan.epfl.ch/wms_themes?FORMAT=image%2Fpng&LAYERS=" + layers + "&TRANSPARENT=TRUE&LOCALID=-1&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&STYLES=&SRS=EPSG%3A900913&BBOX=" + bbox + "&WIDTH=" + w + "&HEIGHT=" + h;
		System.out.println(url);
		return url;
	}
	
	
	private class Downloader extends AsyncTask<LatLngBounds, Void, BitmapDescriptor> {
		LatLngBounds bnds;
		@Override
		protected BitmapDescriptor doInBackground(LatLngBounds... params) {
			bnds = params[0];
			return BitmapDescriptorFactory.fromBitmap(getBitmapFromURL(getAnnotationPictureUrl(bnds)));
		}
		@Override
		protected void onPostExecute(BitmapDescriptor result) {
			if(!"".equals(layer) && epflLabels) {
		        showLabels(result, bnds);
			}
//	        mGroundOverlay.setImage(result);
//	        mGroundOverlay.setPositionFromBounds(bnds);
			super.onPostExecute(result);
			synchronized (MapMainView.this) {
				if(visibleRegion.equals(bnds)) {
					mDownloader = null;
				} else {
					mDownloader = new Downloader().execute(visibleRegion);
				}
			}
		}
	}
	
	private void onCamMove() {
		synchronized (MapMainView.this) {
			visibleRegion = mMap.getProjection().getVisibleRegion().latLngBounds;
			if("".equals(layer) || !epflLabels)
				return;
			if(mDownloader == null) {
				mDownloader = new Downloader().execute(visibleRegion);
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
			Toast.makeText(getApplicationContext(), "The Map plugin is not installed??", Toast.LENGTH_SHORT).show();
		}
	}


    
    
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
		addActionToActionBar(new Action() {
			@Override
			public void performAction(View view) {
				mMap.animateCamera(epflView);
			}

			@Override
			public int getDrawable() {
				return R.drawable.map_icon;
			}

			@Override
			public String getDescription() {
				return getString(R.string.map_menu_campus_position);
			}
		});
		addActionToActionBar(new Action() {
			@Override
			public void performAction(View view) {
				changeEpflSpinner(1);
			}

			@Override
			public int getDrawable() {
				return android.R.drawable.btn_minus;
			}

			@Override
			public String getDescription() {
				return "-"; // TODO
			}
		});
		addActionToActionBar(new Action() {
			@Override
			public void performAction(View view) {
				changeEpflSpinner(-1);
			}

			@Override
			public int getDrawable() {
				return android.R.drawable.btn_plus;
			}

			@Override
			public String getDescription() {
				return "+"; // TODO
			}
		});
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

    private void toggleEpflLayers() {
    	removeEpfl();
    	removeLabels();
    	if(!"".equals(layer)) {
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
            layer = "all";
        } else if (layerName.equals(getString(R.string.epfl_floor_8))) {
        	layer = "8";
        } else if (layerName.equals(getString(R.string.epfl_floor_7))) {
        	layer = "7";
        } else if (layerName.equals(getString(R.string.epfl_floor_6))) {
        	layer = "6";
        } else if (layerName.equals(getString(R.string.epfl_floor_5))) {
        	layer = "5";
        } else if (layerName.equals(getString(R.string.epfl_floor_4))) {
        	layer = "4";
        } else if (layerName.equals(getString(R.string.epfl_floor_3))) {
        	layer = "3";
        } else if (layerName.equals(getString(R.string.epfl_floor_2))) {
        	layer = "2";
        } else if (layerName.equals(getString(R.string.epfl_floor_1))) {
        	layer = "1";
        } else if (layerName.equals(getString(R.string.epfl_floor_0))) {
        	layer = "0";
        } else if (layerName.equals(getString(R.string.epfl_floor_b1))) {
        	layer = "-1";
        } else if (layerName.equals(getString(R.string.epfl_floor_b2))) {
        	layer = "-2";
        } else if (layerName.equals(getString(R.string.epfl_floor_b3))) {
        	layer = "-3";
        } else if (layerName.equals(getString(R.string.epfl_floor_b4))) {
        	layer = "-4";
        } else if (layerName.equals(getString(R.string.map_none))) {
        	layer = "";
        } else {
            Log.i("LDA", "Error setting layer with name " + layerName);
        }
        toggleEpflLayers();
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
            	String s = "http://plan-osm-tile" + rand.nextInt(5) + ".epfl.ch/" + zoom + "/" + x + "/" + y + ".png";
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
                int reversedY = (1 << zoom) - y - 1;
                // http://plan-osm-tile4.epfl.ch/15/16982/11590.png
                // http://plan-epfl-tile0.epfl.ch/batiments-4-merc/18/000/135/850/000/169/428.png
            	//String sx = TextUtils.join("/", String.format("%09d", x).split("(?<=\\G.{3})/u"));
            	//String sy = TextUtils.join("/", String.format("%09d", y).split("(?<=\\G.{3})/u"));
            	
            	DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
				DecimalFormatSymbols customSymbol = new DecimalFormatSymbols();
				customSymbol.setGroupingSeparator('/');
				formatter.setDecimalFormatSymbols(customSymbol);
				String sx = formatter.format(x + 1000000000).substring(2);
				String sy = formatter.format(reversedY + 1000000000).substring(2);
            	
            	String sz = String.format(Locale.US, "%02d", zoom);
                //String s = String.format(Locale.US, MOON_MAP_URL_FORMAT, zoom, x, reversedY);
            	String s = "http://plan-epfl-tile" + rand.nextInt(5) + ".epfl.ch/batiments" + layer + "-merc/" + sz + "/" + sx + "/" + sy + ".png";
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
		GroundOverlayOptions goo = new GroundOverlayOptions().image(bd)
				.positionFromBounds(bounds).zIndex(100000000);
		mGroundOverlay = mMap.addGroundOverlay(goo);
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
