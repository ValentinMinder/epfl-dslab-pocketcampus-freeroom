package org.pocketcampus.plugin.scanner;

import java.lang.reflect.Type;
import java.util.ArrayList;

import org.pocketcampus.R;
import org.pocketcampus.core.communication.DataRequest;
import org.pocketcampus.core.communication.RequestHandler;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.core.parser.Json;
import org.pocketcampus.core.parser.JsonException;
import org.pocketcampus.core.plugin.Core;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.core.ui.ActionBar;
import org.pocketcampus.core.ui.ActionBar.Action;
import org.pocketcampus.plugin.labs.LabsInfo;
import org.pocketcampus.plugin.logging.Tracker;
import org.pocketcampus.plugin.mainscreen.PluginsAdapter;
import org.pocketcampus.plugin.map.MapPlugin;
import org.pocketcampus.shared.plugin.library.BookBean;
import org.pocketcampus.shared.plugin.map.MapLayerBean;

import com.google.gson.reflect.TypeToken;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ScannerPlugin extends PluginBase {
	private ActionBar actionBar_;
	private ScannerPlugin ctx_;
	private TextView centralMsg_;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scanner_main);
		
		centralMsg_ = (TextView) findViewById(R.id.scanner_central_msg);
		actionBar_ = (ActionBar) findViewById(R.id.actionbar);
		ctx_ = this;
		
		Tracker.getInstance().trackPageView("scanner/home");
		
		setupActionBar();
		setupRouteList();
	}
	
	private void setupActionBar() {
		actionBar_.addAction(new Action() {
			@Override
			public void performAction(View view) {
				setupRouteList();
			}

			@Override
			public int getDrawable() {
				return R.drawable.refresh;
			}
		});
		
		setupActionBar(true);
	}

	private void setupRouteList() {
		centralMsg_.setVisibility(TextView.VISIBLE);
		centralMsg_.setText("Loading...");
		
		class RouteListRequest extends DataRequest {
			ArrayList<MapLayerBean> routes_ = new ArrayList<MapLayerBean>();
			
			@Override
			protected void doInBackgroundThread(String result) {
				Type MapLayerBeanType = new TypeToken<ArrayList<MapLayerBean>>(){}.getType();

				try {
					routes_ = Json.fromJson(result, MapLayerBeanType);
					
					System.out.println(routes_);
					
				} catch (JsonException e) {
					onCancelled();
					e.printStackTrace();
					return;
				}
			}
			
			@Override
			protected void doInUiThread(String result) {
				displayRouteList(routes_);
			}
			
			@Override
			protected void onCancelled() {
				displayRouteList(null);
			}
		}
		
		getRequestHandler().execute(new RouteListRequest(), "getLayersList", (RequestParameters)null);
	}
	
	private void displayRouteList(ArrayList<MapLayerBean> layers) {
		if(layers == null) {
			centralMsg_.setVisibility(TextView.VISIBLE);
			centralMsg_.setText("Can't access cloud list.");
			
			return;
		}
		
		ListView routeList = (ListView) findViewById(R.id.scanner_routelist);
		
		if(layers.size() != 0) {
			routeList.setAdapter(new ArrayAdapter<MapLayerBean>(this, R.layout.transport_summaryentry, R.id.travel_summary_time, layers));
			
			routeList.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
					MapLayerBean layer = (MapLayerBean) adapter.getItemAtPosition(pos);
					
					Bundle bundle = new Bundle();
					bundle.putString("MapLayer", layer.getExternalId());

					Core.startPluginWithBundle(ctx_, new MapPlugin(), bundle);
				}
			});
			
			centralMsg_.setVisibility(TextView.GONE);
			
		} else {
			centralMsg_.setText("No cloud list available.");
			centralMsg_.setVisibility(TextView.VISIBLE);
		}
	}

	@Override
	public PluginInfo getPluginInfo() {
		return new ScannerInfo();
	}

	@Override
	public PluginPreference getPluginPreference() {
		return null;
	}
	
	public RequestHandler getScannerRequestHandler() {
		return getRequestHandler();
	}
}
