package org.pocketcampus.plugin.food;

import java.util.Vector;

import org.pocketcampus.shared.plugin.food.Meal;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

/**
 * This class is used to make each section of a list of menus.
 * 
 */
public class FoodListSection extends BaseAdapter implements Filterable {
	private Vector<Meal> meals_;
	private FoodPlugin menusActivity_;

	/**
	 * Constructor
	 * 
	 * @param context
	 *            context of the application the list view is in
	 * @param resto
	 *            restaurant full menu represented in the list section.
	 */
	public FoodListSection(Vector<Meal> resto, FoodPlugin menus) {
		// Cache the LayoutInflate to avoid asking for a new one each time.
		// LayoutInflater mInflater_ =
		// LayoutInflater.from(menus.getApplicationContext());
		this.meals_ = resto;
		this.menusActivity_ = menus;
	}

	public void repaint(Meal m) {
		for (Meal meal : meals_) {
			if (m.hashCode() == meal.hashCode()) {
				meal.setRating(m.getRating());
			}
		}
		menusActivity_.notifyDataSetChanged();
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
		MenuView holder;
		holder = new MenuView(meals_.get(position), menusActivity_);

		return holder;
	}

	public Filter getFilter() {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	// Returns the number of meals in that section.
	public int getCount() {
		return meals_.size();
	}

	// Returns the meal to be represented at that position.
	public Object getItem(int position) {
		return meals_.get(position);
	}
}