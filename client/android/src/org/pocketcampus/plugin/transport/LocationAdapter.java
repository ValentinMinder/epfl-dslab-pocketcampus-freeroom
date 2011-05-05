package org.pocketcampus.plugin.transport;

import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.core.communication.RequestHandler;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.shared.plugin.transport.Location;

import android.content.Context;
import android.database.DataSetObserver;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

/**
 * Adapter providing transport locations from the CFF server.
 * @author Florian
 * @status working
 */
public class LocationAdapter extends ArrayAdapter<Location> {
	private static final int MAX_DISPLAY_AUTOCOMPLETIONS = 3;

	/** Request handler instance from the Transport Plugin */
	private RequestHandler requestHandler_;
	
	/** List of locations for the last input. */
	private List<Location> locations_;
	
	/**
	 * Public constructor.
	 * @param context
	 * @param textViewResourceId
	 * @param inputView the input view
	 * @param requestHandler a Transport request handler
	 */
	public LocationAdapter(Context context, int textViewResourceId, final AutoCompleteTextView inputView, RequestHandler requestHandler) {
		super(context, textViewResourceId);
		locations_ = new ArrayList<Location>();
		requestHandler_ = requestHandler;
		
		// Registers the Observer for this Adapter.
		registerDataSetObserver(new DataSetObserver() {
			@Override
			public void onInvalidated() {
				super.onInvalidated();
				
				RequestParameters reqParam = new RequestParameters();
				reqParam.addParameter("constraint", inputView.getText().toString());
				requestHandler_.execute(new AutocompleteRequest(), "autocomplete", reqParam);
			}
		});
	}
	
	/**
	 * Inner class that loads autocompletions and put them in the <code>locations_</code> field. 
	 */
	class AutocompleteRequest extends AutoCompleteStationRequest {
		@Override
		protected void handleLocations(ArrayList<Location> locations) {
			locations_ = locations;
			notifyDataSetChanged();
		}
	}
	
	@Override
	public Location getItem(int position) {
		return locations_.get(position);
	}
	
	@Override
	public int getCount() {
		if(locations_ == null) {
			return 0;
		}
		
		// Limits the number of autocompletions displayed.
		return Math.min(MAX_DISPLAY_AUTOCOMPLETIONS, locations_.size());
	}
}
