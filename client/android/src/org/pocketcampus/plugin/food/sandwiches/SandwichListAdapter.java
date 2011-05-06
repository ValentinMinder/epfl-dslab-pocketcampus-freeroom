package org.pocketcampus.plugin.food.sandwiches;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.pocketcampus.R;
import org.pocketcampus.plugin.food.FoodPlugin;
import org.pocketcampus.shared.plugin.food.Sandwich;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * This class is used to make each section of a list of menus.
 * 
 */
public class SandwichListAdapter extends BaseAdapter implements Filterable {
	private LayoutInflater mInflater_;
	private Vector<String> restaurants_;
	private FoodPlugin menusActivity_;
	private boolean[] mExpanded_;
	private HashMap<String, Vector<Sandwich>> sandwiches_;

	/**
	 * Constructor
	 * 
	 * @param context
	 *            context of the application the list view is in
	 * @param resto
	 *            restaurant full menu represented in the list section.
	 */
	public SandwichListAdapter(Vector<String> resto, HashMap<String, Vector<Sandwich>> sandwiches, FoodPlugin menus) {
		// Cache the LayoutInflate to avoid asking for a new one each time.
		mInflater_ = LayoutInflater.from(menus.getApplicationContext());
		this.restaurants_ = resto;
		this.menusActivity_ = menus;
		this.sandwiches_ = sandwiches;
		this.mExpanded_ = new boolean[resto.size()];
		for (int i = 0; i < resto.size(); i++) {
			mExpanded_[i] = false;
		}
	}

	/**
	 * Make a view to hold each row.
	 * 
	 * @see android.widget.ListAdapter#getView(int, android.view.View,
	 *      android.view.ViewGroup)
	 */
	public View getView(final int position, View convertView, ViewGroup parent) {
		/*
		 * A ViewHolder keeps references to children views to avoid unnecessary
		 * calls to findViewById() on each row.
		 */
		ViewHolder holder;

		String restaurant = restaurants_.get(position);

		holder = new ViewHolder(menusActivity_, restaurant, sandwiches_
				.get(restaurant), mExpanded_[position]);

		holder.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				toggle(position);
			}
		});

		return holder;
	}

	public void toggle(int position) {
		Log.d("List", "Toggling " + position + ", old: " + mExpanded_[position]
				+ ", new: " + !mExpanded_[position]);
		mExpanded_[position] = !mExpanded_[position];
		menusActivity_.notifyDataSetChanged();
	}
	
	public void toggleAll(boolean toggle) {
		for (int i = 0; i < mExpanded_.length; i++) {
			mExpanded_[i] = toggle;
		}
		menusActivity_.notifyDataSetChanged();
	}

	public Filter getFilter() {
		return null;
	}

	public long getItemId(int position) {
		return position;
	}

	// Returns the number of meals in that section.
	public int getCount() {
		return restaurants_.size();
	}

	// Returns the meal to be represented at that position.
	public Object getItem(int position) {
		return restaurants_.get(position);
	}

	private class ViewHolder extends LinearLayout {

		View sView;
		TextView sname;
		ImageView sImage;

		private List<View> sMenus;

		public ViewHolder(Context context, String title, Vector<Sandwich> resto,
				boolean expanded) {
			super(context);

			this.setOrientation(VERTICAL);

			sView = mInflater_.inflate(R.layout.food_restaurantentry, null);

			sname = (TextView) sView
					.findViewById(R.id.food_restaurantentry_title);
			sname.setText(title);

			sImage = (ImageView) sView
					.findViewById(R.id.food_restaurantentry_arrow);
			if(expanded){
				sImage.setImageDrawable(menusActivity_.getResources().getDrawable(R.drawable.food_restaurantlist_south));
			} else {
				sImage.setImageDrawable(menusActivity_.getResources().getDrawable(R.drawable.food_restaurantlist_east));
			}
				
			addView(sView);

			sMenus = new ArrayList<View>();

			Vector<Sandwich> sandwiches = sandwiches_.get(title);
			for (Sandwich s : sandwiches) {
				sMenus.add(new SandwichView(menusActivity_, s));
			}

			for (int j = 0; j < sMenus.size(); j++) {
				addView(sMenus.get(j));
				sMenus.get(j).setVisibility(expanded ? VISIBLE : GONE);
			}
		}
	}
}