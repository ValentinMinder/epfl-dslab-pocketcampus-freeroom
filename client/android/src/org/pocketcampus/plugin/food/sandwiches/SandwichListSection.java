/**
 * Sandwich List Adapter
 * 
 * @author Oriane
 * 
 */

package org.pocketcampus.plugin.food.sandwiches;

import java.util.Vector;

import org.pocketcampus.R;
import org.pocketcampus.plugin.food.FoodPlugin;
import org.pocketcampus.plugin.food.MenuView;
import org.pocketcampus.shared.plugin.food.Sandwich;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * This class is used to make each section of a list of sandwiches.
 * 
 */
public class SandwichListSection extends BaseAdapter implements Filterable {
	private LayoutInflater mInflater_;
	private Vector<Sandwich> sandwich_;
	private FoodPlugin menusActivity_;

	/**
	 * Constructor
	 * 
	 * @param context
	 *            context of the application the list view is in
	 * @param sandwiches
	 *            all sandwiches represented in the list section.
	 */
	public SandwichListSection(Vector<Sandwich> sandwiches, FoodPlugin menus) {
		// Cache the LayoutInflate to avoid asking for a new one each time.
		mInflater_ = LayoutInflater.from(menus.getApplicationContext());
		this.sandwich_ = sandwiches;
		this.menusActivity_ = menus;
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
		SandwichView holder;
		// When convertView is not null, we can reuse it directly, there is
		// no need to re-inflate it. We only inflate a new View when the
		// convertView supplied by ListView is null.
		if (convertView == null) {
			convertView = mInflater_.inflate(R.layout.food_sandwich_list_item, null);

			// Creates a ViewHolder and store references to the two children
			// views we want to bind data to.
			holder = new SandwichView(menusActivity_, sandwich_.get(position));
		} else {
			// Get the ViewHolder back to get fast access to the TextView
			// and the ImageView.
			holder = (SandwichView) convertView;
		}

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
		return sandwich_.size();
	}

	// Returns the meal to be represented at that position.
	public Object getItem(int position) {
		return sandwich_.get(position);
	}
}