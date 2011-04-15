package org.pocketcampus.plugin.food;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.pocketcampus.R;
import org.pocketcampus.shared.plugin.food.Meal;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * This class is used to make each section of a list of menus.
 * 
 */
public class RestaurantListAdapter extends BaseAdapter implements Filterable {
	private LayoutInflater mInflater_;
	private Vector<String> restaurants_;
	private FoodPlugin menusActivity_;
	private boolean[] mExpanded_;
	private HashMap<String, Vector<Meal>> mealHashMap_;

	/**
	 * Constructor
	 * 
	 * @param context
	 *            context of the application the list view is in
	 * @param resto
	 *            restaurant full menu represented in the list section.
	 */
	public RestaurantListAdapter(Vector<String> resto,
			HashMap<String, Vector<Meal>> mealHashMap, FoodPlugin menus) {
		// Cache the LayoutInflate to avoid asking for a new one each time.
		mInflater_ = LayoutInflater.from(menus.getApplicationContext());
		this.restaurants_ = resto;
		this.menusActivity_ = menus;
		this.mealHashMap_ = mealHashMap;
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
//		if (convertView == null) {
//			// Creates a ViewHolder and store references to the two children
//			// views we want to bind data to.
//			holder = new ViewHolder(menusActivity_, restaurant, mealHashMap_
//					.get(restaurant), mExpanded_[position]);
//		} else {
//			holder = (ViewHolder) convertView;
//			holder.setTitle(restaurant);
//			holder.setMenus(restaurant);
//			
//			holder.setExpanded(mExpanded_[position]);
//		}
		
		holder = new ViewHolder(menusActivity_, restaurant, mealHashMap_
				.get(restaurant), mExpanded_[position]);


		holder.mTitle.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				toggle(position);
			}
		});

		return holder;
	}

	public void toggle(int position) {
		Log.d("List", "Toggling " + position + ", old: "+mExpanded_[position]+", new: "+!mExpanded_[position]);
		mExpanded_[position] = !mExpanded_[position];
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

		View mView;
		TextView mTitle;
		private List<View> mMenus;

		public ViewHolder(Context context, String title, Vector<Meal> resto,
				boolean expanded) {
			super(context);

			this.setOrientation(VERTICAL);

			mView = mInflater_.inflate(R.layout.food_restaurantentry, null);

			mTitle = (TextView) mView
					.findViewById(R.id.food_restaurantentry_title);
			mTitle.setText(title);

			addView(mView);

			mMenus = new ArrayList<View>();

			Vector<Meal> meals = mealHashMap_.get(title);
			for (Meal m : meals) {
				mMenus.add(new MenuView(menusActivity_, m));
			}

			for (int j = 0; j < mMenus.size(); j++) {
				addView(mMenus.get(j));
				mMenus.get(j).setVisibility(expanded ? VISIBLE : GONE);
			}
		}

		/**
		 * Convenience method to set the title of a ViewHolder
		 */
		public void setTitle(String title) {
			mTitle.setText(title);
		}

		/**
		 * Convenience method to set the dialogue of a SpeechView
		 */
		public void setMenus(String resto) {
			Vector<Meal> meals = mealHashMap_.get(resto);
			mMenus.clear();
			for (Meal m : meals) {
				mMenus.add(new MenuView(menusActivity_, m));
			}
		}

		// private class OnItemClickListener implements OnClickListener {
		// private int mPosition;
		//
		// OnItemClickListener(int position) {
		// mPosition = position;
		// }
		//
		// @Override
		// public void onClick(View arg0) {
		// // menuDialog(mPosition);
		// }
		// }

		/**
		 * Convenience method to expand or hide the menus
		 */
		public void setExpanded(boolean expanded) {
			for (View v : mMenus) {
				v.setVisibility(expanded ? VISIBLE : GONE);
			}
		}
	}

	public void listRefreshed() {
		this.notifyDataSetChanged();
	}
}