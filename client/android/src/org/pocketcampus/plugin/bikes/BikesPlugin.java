package org.pocketcampus.plugin.bikes;

import java.util.ArrayList;
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
import org.pocketcampus.plugin.map.MapPlugin;
import org.pocketcampus.shared.plugin.bikes.BikeStation;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableRow.LayoutParams;

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
		
		bikeStations_ = new ArrayList<BikeStation>();

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
		bikeStations_ = new ArrayList<BikeStation>();
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
		
        final TableLayout t = (TableLayout) findViewById(R.id.bikes_table);
        t.removeAllViews();
        
        TableRow title = new TableRow(this);
        title.setBackgroundColor(Color.LTGRAY);
        title.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
        		LayoutParams.WRAP_CONTENT));

        LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT,
        		LayoutParams.FILL_PARENT);
        
        //Empty cell (for the arrow icon)
        title.addView(createTextView("", Gravity.CENTER));
       
        //Station
        title.addView(createTextView(this.getString(R.string.bikes_plugin_station), Gravity.LEFT));

        
        //Free bikes
        title.addView(createTextView(this.getString(R.string.bikes_plugin_free), Gravity.RIGHT));

        //Empty racks
        title.addView(createTextView(this.getString(R.string.bikes_plugin_empty), Gravity.RIGHT));

        
        t.addView(title, layoutParams);
        
		//Content
		int index = 0;
		for (BikeStation bs : bikeStations_) {
			TableRow tr = createRow(bs,index%2==0, layoutParams);
			t.addView(tr, layoutParams);
			index++;
		}

	}

	private TextView createTextView(String value, int gravity) {
        TextView text = new TextView(this);
        text.setTextColor(Color.BLACK);
        text.setGravity(gravity);
		text.setTypeface(Typeface.DEFAULT_BOLD);
        text.setPadding(2, 5, 2, 5);
        text.setText(value);
        return text;
	}
	
	private TableRow createRow(final BikeStation station, boolean isPair, LayoutParams layoutParams) {
		
		TableRow row = new TableRow(ctx_);
        row.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
        		LayoutParams.WRAP_CONTENT));
		
		if(isPair) {
			row.setBackgroundColor(Color.GRAY);
		} else {
			row.setBackgroundColor(Color.LTGRAY);
		}
		

		//Small arrow
		ImageView icon = new ImageView(ctx_);
		icon.setImageResource(R.drawable.transport_summarylist_smallarrow);
		icon.setPadding(2, 5, 2, 5);
		row.addView(icon);

		//Station
		row.addView(createTextView(station.getName(), Gravity.LEFT));

		//Free Bikes
		row.addView(createTextView(station.getFreeBikes() + "", Gravity.RIGHT));


		//Empty racks
		row.addView(createTextView(station.getEmptyRacks() + "", Gravity.RIGHT));

		row.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle b = new Bundle();
				b.putSerializable("MapLayer", "E947267780DD3BC28FE75AA56DA18DC7");
				b.putInt("MapItem", station.getId());
				Core.startPluginWithBundle(getApplicationContext(), new MapPlugin(), b);
			}
		});

		return row;
	}


}
