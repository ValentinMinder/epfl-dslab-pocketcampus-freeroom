package org.pocketcampus.plugin.bikes;

import java.io.IOException;
import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.core.ui.ActionBar;
import org.pocketcampus.core.ui.ActionBar.Action;
import org.pocketcampus.plugin.logging.Tracker;
import org.pocketcampus.shared.plugin.bikes.BikeStation;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class BikesPlugin extends PluginBase {

	private ActionBar actionBar_;
	
	@Override
	protected void onCreate(Bundle  savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bikes_list);


		setupActionBar(true);

		setLayout();
		
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
					//bikes_.forceRefresh();
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

			//bikes_.refreshIfNeeded();
		}

		private void setLayout() {
			final TableLayout table = (TableLayout) findViewById(R.id.bikes_table);
			
			List<BikeStation> bikeStations = null;
			try {
				bikeStations = BikeStationParser.getBikeStations();
			} catch (IOException e) {
				Toast error = Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG);
				error.show();
				e.printStackTrace();
			}
			
			
			
			int index = 0;
			
			
			Toast b = Toast.makeText(this, "" + (bikeStations == null), Toast.LENGTH_LONG);
			b.show();
	        for (BikeStation station: bikeStations) {
	        	
	            // Create a TableRow and give it an ID
	            TableRow tr = new TableRow(this);
	            tr.setId(index);
	            tr.setLayoutParams(new LayoutParams(
	                    LayoutParams.FILL_PARENT,
	                    LayoutParams.WRAP_CONTENT));   

	            // Create a TextView to house the name of the stations
	            TextView stationName = new TextView(this);
	            stationName.setId(100+index);
	            stationName.setText(station.getName_());
	            stationName.setTextColor(Color.BLACK);
	            stationName.setLayoutParams(new LayoutParams(
	                    LayoutParams.FILL_PARENT,
	                    LayoutParams.WRAP_CONTENT));
	            tr.addView(stationName);

	            // Create a TextView for the available bikes
	            TextView available = new TextView(this);
	            available.setId(200+index);
	            available.setText(station.getBikes_());
	            available.setTextColor(Color.GREEN);
	            available.setLayoutParams(new LayoutParams(
	                    LayoutParams.FILL_PARENT,
	                    LayoutParams.WRAP_CONTENT));
	            tr.addView(available);

	            TextView empty = new TextView(this);
	            empty.setId(300+index);
	            empty.setText(station.getEmpty_());
	            empty.setTextColor(Color.RED);
	            empty.setLayoutParams(new LayoutParams(
	                    LayoutParams.FILL_PARENT,
	                    LayoutParams.WRAP_CONTENT));
	            tr.addView(empty);
	            
	            // Add the TableRow to the TableLayout
	            table.addView(tr, new TableLayout.LayoutParams(
	                    LayoutParams.FILL_PARENT,
	                    LayoutParams.WRAP_CONTENT));
	            
	            index++;
	        }
			
		}
//
//		@Override
//		public void bikesRefreshing() {
//			actionBar_.setProgressBarVisibility(View.VISIBLE);
//		}
//
//		@Override
//		public void bikesRefreshed() {
//			actionBar_.setProgressBarVisibility(View.GONE);
//		}

}
