package org.pocketcampus.plugin.transport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.core.communication.DataRequest;
import org.pocketcampus.core.communication.RequestHandler;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.shared.plugin.transport.Location;

import android.content.Context;
import android.database.DataSetObserver;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
	 * 
	 * @param context
	 * @param textViewResourceId
	 * @param inputView the input view
	 * @param requestHandler a Transport request handler
	 */
	public LocationAdapter(Context context, int textViewResourceId, final AutoCompleteTextView inputView, RequestHandler requestHandler) {
		super(context, textViewResourceId);
		locations_ = new ArrayList<Location>();
		requestHandler_ = requestHandler;
		
		/**
		 * Registers the Observer for this Adapter.
		 */
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
	 * @author Florian
	 */
	class AutocompleteRequest extends DataRequest {
		@Override
		protected int expirationDelay() {
			// Not likely to change.
			return 6 * 60 * 60;
		}
		
		@Override
		protected void doInUiThread(String result) {
			Gson gson = new Gson();
			Type AutocompleteType = new TypeToken<List<Location>>(){}.getType();
			locations_ = gson.fromJson(result, AutocompleteType);
			System.out.println(locations_);
			// updates the Adapter display
			notifyDataSetChanged();
		}
		
		@Override
		protected void onCancelled() {
			// TODO display toast?
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
