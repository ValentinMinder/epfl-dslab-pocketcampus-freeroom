package org.pocketcampus.plugin.transport;

import java.util.Date;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import de.schildbach.pte.SbbProvider;
import de.schildbach.pte.NetworkProvider.WalkSpeed;
import de.schildbach.pte.dto.Location;
import de.schildbach.pte.dto.QueryConnectionsResult;

/**
 * Transport tests.
 * 
 * @status WIP
 * @author Florian
 */
public class TransportPlugin extends PluginBase {
	private SbbProvider sbbProvider_;
	private AutoCompleteTextView departureTextView_;
	private AutoCompleteTextView arrivalTextView_;
	private TextView resultTextView_;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.transport_main);
		setupActionBar(true);
		
		sbbProvider_ = new SbbProvider("MJXZ841ZfsmqqmSymWhBPy5dMNoqoGsHInHbWJQ5PTUZOJ1rLTkn8vVZOZDFfSe");
		
		resultTextView_ = (TextView) findViewById(R.id.result);
		
		departureTextView_ = (AutoCompleteTextView) findViewById(R.id.location_departure);
		LocationAdapter adapterDeparture = new LocationAdapter(this, R.layout.transport_locationentry, departureTextView_, sbbProvider_);
		departureTextView_.setAdapter(adapterDeparture);
	    
	    arrivalTextView_ = (AutoCompleteTextView) findViewById(R.id.location_arrival);
		LocationAdapter adapterArrival = new LocationAdapter(this, R.layout.transport_locationentry, arrivalTextView_, sbbProvider_);
		arrivalTextView_.setAdapter(adapterArrival);
		
		Button button = (Button) findViewById(R.id.search_button);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					Location from = sbbProvider_.autocompleteStations(departureTextView_.getText()).get(0);
					Location to = sbbProvider_.autocompleteStations(arrivalTextView_.getText()).get(0);
					QueryConnectionsResult result = sbbProvider_.queryConnections(from, null, to, new Date(), true, null, WalkSpeed.NORMAL);
					resultTextView_.setText(result.connections.toString());
					
				} catch (Exception e) {
					resultTextView_.setText(e.toString());
				}
			}
		});
	}

	@Override
	public PluginInfo getPluginInfo() {
		return new TransportInfo();
	}

	@Override
	public PluginPreference getPluginPreference() {
		return null;
	}

}