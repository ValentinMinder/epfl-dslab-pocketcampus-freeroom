package org.pocketcampus.plugin.bikes;

import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.core.communication.RequestHandler;
import org.pocketcampus.core.plugin.Core;
import org.pocketcampus.core.plugin.NoIDException;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.core.ui.ActionBar;
import org.pocketcampus.core.ui.ActionBar.Action;
import org.pocketcampus.plugin.logging.Tracker;
import org.pocketcampus.plugin.mainscreen.IAllowsID;
import org.pocketcampus.plugin.mainscreen.MainscreenNews;
import org.pocketcampus.shared.plugin.bikes.BikeStation;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class BikesPlugin extends PluginBase {

	private static ActionBar actionBar_;

	public static RequestHandler bikesRequestHandler;
	
	private BikesAdapter adapter_;
	
	private int selected_;
	private List<BikeStation> bikeStations_;
	private BikeStationList list_;
	
	
	private Context ctx_;
	
	@Override
	protected void onCreate(Bundle  savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bikes_main);

		bikesRequestHandler = getRequestHandler();
		
		list_ = new BikeStationList(this);
		setupActionBar(true);
		
		selected_ = -1;
		ctx_ = getApplicationContext();
		
		handleIntent();

		
		Tracker.getInstance().trackPageView("bikes/home");		
	}

	@Override
	public PluginInfo getPluginInfo() {
		return new BikesInfo();
	}

	@Override
	public PluginPreference getPluginPreference() {
		return null;
	}

	@Override
	protected void setupActionBar(boolean addHomeButton) {

		actionBar_ = (ActionBar) findViewById(R.id.actionbar);
		actionBar_.addAction(new Action() {

			@Override
			public void performAction(View view) {
				refresh();
			}

			@Override
			public int getDrawable() {
				return R.drawable.refresh;
			}
		});

		super.setupActionBar(addHomeButton);

	}

	@Override
	protected void onResume() {
		super.onResume();
		refresh();
	}

	private void handleIntent() {
		try {
			Log.d(this.getClass().toString(), hasIDInIntent() ? "Has ID " + getIDFromIntent() : "Does not have ID");
			if(hasIDInIntent()) {
				selected_ = getIDFromIntent();
			}
		} catch (NoIDException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void refresh() {		
		Log.d("MainscreenPlugin", "Refreshing");
		list_.loadBikes();
	}
	
	public void setBikeStationList(List<BikeStation> bikeStations) {
		this.bikeStations_ = bikeStations;
	}
	
	public static void refreshing() {
		actionBar_.setProgressBarVisibility(View.VISIBLE);
	}


	public static void refreshed() {
		actionBar_.setProgressBarVisibility(View.GONE);
	}
	
	
	
	protected void displayBikes() {
		final ListView l = (ListView) findViewById(R.id.mainscreen_news_list_list);
		adapter_ = new BikesAdapter(ctx_, selected_, bikeStations_);
		l.setAdapter(adapter_);
		l.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				MainscreenNews selected = (MainscreenNews) adapter_.getItem(position);
				if(selected.getPlugin_() instanceof IAllowsID) {
					Core.startPluginWithID(ctx_, selected.getPlugin_(), selected.getId_());
				}
			}
		});
	}
	

}
