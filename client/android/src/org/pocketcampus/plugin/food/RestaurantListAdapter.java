package org.pocketcampus.plugin.food;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.pocketcampus.R;
import org.pocketcampus.plugin.logging.Tracker;
import org.pocketcampus.shared.plugin.food.Meal;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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

		holder = new ViewHolder(menusActivity_, restaurant,
				mealHashMap_.get(restaurant), mExpanded_[position]);

		holder.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				toggle(position);
				menusActivity_.setSelected(position);
			}
		});

		return holder;
	}

	public void toggle(int position) {
		String restaurant = restaurants_.get(position).replace(" ", "");
		Tracker.getInstance()
				.trackPageView("food/menusListToggle" + restaurant);
		Log.d("List", "Toggling " + position + ", old: " + mExpanded_[position]
				+ ", new: " + !mExpanded_[position]);
		mExpanded_[position] = !mExpanded_[position];
		menusActivity_.notifyDataSetChanged();
	}

	public void repaint(Meal m) {
		Set<String> restaurants = mealHashMap_.keySet();
		for (String restaurant : restaurants) {
			for (Meal meal : mealHashMap_.get(restaurant)) {
				if (m.hashCode() == meal.hashCode()) {
					meal.setRating(m.getRating());
				}
			}
		}
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

		View mView;
		TextView mTitle;
		ImageView mImage;

		private List<View> mMenus;

		public ViewHolder(Context context, String title, Vector<Meal> resto,
				boolean expanded) {
			super(context);

			this.setOrientation(VERTICAL);

			mView = mInflater_.inflate(R.layout.food_restaurantentry, null);

			mTitle = (TextView) mView
					.findViewById(R.id.food_restaurantentry_title);
			mTitle.setText(title);

			mImage = (ImageView) mView
					.findViewById(R.id.food_restaurantentry_arrow);
			if (expanded) {
				mImage.setImageDrawable(menusActivity_.getResources()
						.getDrawable(R.drawable.food_restaurantlist_south));
			} else {
				mImage.setImageDrawable(menusActivity_.getResources()
						.getDrawable(R.drawable.food_restaurantlist_east));
			}

			addView(mView);

			mMenus = new ArrayList<View>();

			Vector<Meal> meals = mealHashMap_.get(title);
			for (Meal m : meals) {
				mMenus.add(new MenuView(m, menusActivity_));
			}

			for (int j = 0; j < mMenus.size(); j++) {
				addView(mMenus.get(j));
				mMenus.get(j).setVisibility(expanded ? VISIBLE : GONE);
			}
		}
	}
}