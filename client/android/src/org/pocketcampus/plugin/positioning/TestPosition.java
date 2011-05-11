package org.pocketcampus.plugin.positioning;



//import org.osmdroid.views.overlay.MyLocationOverlay;



import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
//import org.andnav.osm.views.MapView;
//import com.google.android.maps.MapActivity;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import org.osmdroid.views.MapController;
import org.osmdroid.views.overlay.MyLocationOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.pocketcampus.R;
import org.pocketcampus.plugin.map.elements.MapPathOverlay;
import org.pocketcampus.shared.plugin.map.Position;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
//import org.android.overlay.ElementMapOverlay;

public class TestPosition extends Activity {
  /** Called when the activity is first created. */
  private MapController mapController_;
  private MapView mapView_;
  //private OSMap osm_;
  //private Location myLocation_;
  private Context ctx_ ;
  private HybridLocation myLocation_;
  //private MyLocationOverlay myLocationOverlay_;
  private ResourceProxy resourceProxy_;
  //private ElementMapOverlay elementMapOverlay_;
  //private LocationManager location_ = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
  private Position gpsLocation_;
  private GeoPoint gsmLocation_;
  private WifiLocation wifiLocation_;
  

  private MyLocationOverlay myLocationOverlay_;
  private MapPathOverlay mapPathOverlay_;
  
	
  
  
  

  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.position_main);
      
      
		mapView_= (MapView) findViewById(R.id.mapview);
		mapView_.setMultiTouchControls(true);
		mapView_.setBuiltInZoomControls(true);
		mapController_ = mapView_.getController();
      
     
      ctx_ = getApplicationContext();
      System.out.println("Passes : ctx ");
      wifiLocation_ = new WifiLocation(ctx_);
      
      System.out.println("Wifi Position :"+ wifiLocation_.getWifiLocationPerCoefficient().toString());
      wifiLocation_.getWeakestAP();
      wifiLocation_.getStrongestAP();
      myLocation_ = new HybridLocation(ctx_, mapView_);
      
      //gpsLocation_ = myLocation_.getGpsLocation();
      try {
			gsmLocation_ = myLocation_.getGsmPosition();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		System.out.println("Gsm Position :"+gsmLocation_.toString());
		
		System.out.println(" Position :"+myLocation_.getPosition().toString());
//      Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.gsm); 
//      Bitmap bmp2 = BitmapFactory.decodeResource(getResources(), R.drawable.icon); 
//      ElementMapOverlay mapi2 = null;
//		ElementMapOverlay mapi = new ElementMapOverlay(gpsLocation_,bmp);
		//mapi2 = new ElementMapOverlay(gsmLocation_,bmp2);
		//OverlayItem mapi2 =  new OverlayItem("Gps", "gpsLocation", myLocation_.getMyPosition());
//		Canvas c1 = new Canvas();
//		List<Overlay> listOfOverlays = mapView_.getOverlays();
//      listOfOverlays.clear();
//      listOfOverlays.add(mapi); 
      //listOfOverlays.add(mapi2);

//      //mapView.invalidate();
//     if(!(myLocationOverlay_.getLocOverlay_().isLocationFollowEnabled()))
//      myLocation_.getLocOverlay_().followLocation(true);
     
  // myLocationOverlay_.getMapController().animateTo(gpsLocation_);
      
		//mapi.onDraw(c1,mapView);
      
     // GsmLocation myGsmLocation = new GsmLocation(ctx_);
//      Log.d("gsm:::::",myGsmLocation.toString());
//      Log.d("Cell Id::::","Cell Id :"+myGsmLocation.getCellID());
//      Log.d("Lac ::::::","Lac :"+myGsmLocation.getLac());
      try {
			Log.d("GsmLocation:::",myLocation_.getGsmPosition() .toDoubleString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		System.out.println("Distanec Max :"+ wifiScan.getMaxD() );
//		System.out.println("Distanec free Max :"+ wifiScan.getMaxFreeD() );
//		System.out.println("Distanec Min :"+ wifiScan.getMinD() );
//		System.out.println("Distanec free Min :"+ wifiScan.getMinFreeD() );
		
		Resources resource = getResources();
	
      System.out.println("center passes !!!!!!!");
      



  }




} 