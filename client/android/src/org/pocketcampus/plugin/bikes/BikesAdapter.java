package org.pocketcampus.plugin.bikes;



import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.shared.plugin.bikes.BikeStation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class BikesAdapter extends BaseAdapter {
	

		// Misc
		private LayoutInflater mInflater_;
		
		private List<BikeStation> stations_;
				
		private int selected_;
		private Context ctx_;
		
		/**
		 * Adapter constructor
		 * @param context Context of the application
		 * @param items Items that have to be on the list
		 */
		public BikesAdapter(Context context, int selected, List<BikeStation> bikeStations) {
			super();
					
			this.ctx_ = context;
			
			this.stations_ =  bikeStations;
						
			mInflater_ = LayoutInflater.from(context);
						
			this.selected_ = selected;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				v = mInflater_.inflate(R.layout.bikes_item, null);
			}

			// The item to display
			final BikeStation item = (BikeStation) getItem(position);

			if (item != null) {
				TextView tv;

				tv = (TextView) v.findViewById(R.id.bikes_item_station);
				tv.setText((item.getName()));
				if(selected_ == position) {
					tv.setBackgroundColor(R.color.red);
					tv.setTextColor(R.color.SnowWhite);
				}
				
				
				tv = (TextView) v.findViewById(R.id.bikes_item_bikes);
				
				tv.setText(ctx_.getText(R.string.bikes_plugin_free) + ": " + item.getFreeBikes() + ", " + ctx_.getText(R.string.bikes_plugin_empty) + ": " + item.getEmptyRacks());
				if(selected_ == position) {
					tv.setBackgroundColor(R.color.red);
					tv.setTextColor(R.color.SnowWhite);
				}
			}

			return v;

		}


		@Override
		public int getCount() {
			return stations_.size();
		}

		@Override
		public Object getItem(int position) {
			return stations_.get(position);
		}

		@Override
		public long getItemId(int position) {
			return stations_.get(position).getName().hashCode();
		}


}
