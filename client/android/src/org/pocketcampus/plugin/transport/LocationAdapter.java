package org.pocketcampus.plugin.transport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import de.schildbach.pte.NetworkProvider;
import de.schildbach.pte.dto.Location;

public class LocationAdapter extends ArrayAdapter<Location> {
	private List<Location> locations_ = new ArrayList<Location>();
	
	public LocationAdapter(Context context, int textViewResourceId, final AutoCompleteTextView inputView, final NetworkProvider networkProvider) {
		super(context, textViewResourceId);
		
		registerDataSetObserver(new DataSetObserver() {
			@Override
			public void onInvalidated() {
				super.onInvalidated();
				
				try {
					locations_ = networkProvider.autocompleteStations(inputView.getText().toString());
					notifyDataSetChanged();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	@Override
	public Location getItem(int position) {
		return locations_.get(position);
	}
	
	@Override
	public int getCount() {
		return locations_.size();
	}
}
