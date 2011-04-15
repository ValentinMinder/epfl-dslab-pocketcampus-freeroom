package org.pocketcampus.plugin.transport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.core.communication.RequestHandler;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.core.communication.ServerRequest;
import org.pocketcampus.shared.plugin.transport.Location;

import android.content.Context;
import android.database.DataSetObserver;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class LocationAdapter extends ArrayAdapter<Location> {
	private List<Location> locations_;
	private RequestHandler requestHandler_;
	
	public LocationAdapter(Context context, int textViewResourceId, final AutoCompleteTextView inputView, RequestHandler requestHandler) {
		super(context, textViewResourceId);
		
		locations_ = new ArrayList<Location>();
		requestHandler_ = requestHandler;
		
		registerDataSetObserver(new DataSetObserver() {

			@Override
			public void onInvalidated() {
				super.onInvalidated();
				
				class AutocompleteRequest extends ServerRequest {

					@Override
					protected void onPostExecute(String result) {
						Gson gson = new Gson();
						Type AutocompleteType = new TypeToken<List<Location>>(){}.getType();
						
						locations_ = gson.fromJson(result, AutocompleteType);
						
						notifyDataSetChanged();
					}
					
				}
				
				RequestParameters reqParam = new RequestParameters();
				reqParam.addParameter("constraint", inputView.getText().toString());
				requestHandler_.execute(new AutocompleteRequest(), "autocomplete", reqParam );
			}
		});
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
		
		//return locations_.size();
		return Math.min(1, locations_.size());
	}
}
