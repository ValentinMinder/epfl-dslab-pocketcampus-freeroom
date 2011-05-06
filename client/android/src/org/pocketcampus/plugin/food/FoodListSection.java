package org.pocketcampus.plugin.food;

import java.util.Vector;

import org.pocketcampus.R;
import org.pocketcampus.shared.plugin.food.Meal;

import android.view.LayoutInflater;
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
	private LayoutInflater mInflater_;
	private Vector<Meal> meal_;
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
		mInflater_ = LayoutInflater.from(menus.getApplicationContext());
		this.meal_ = resto;
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
		MenuView holder;
		// When convertView is not null, we can reuse it directly, there is
		// no need to re-inflate it. We only inflate a new View when the
		// convertView supplied by ListView is null.
		if (convertView == null) {
			convertView = mInflater_.inflate(R.layout.food_menuentry, null);

			// Creates a ViewHolder and store references to the two children
			// views we want to bind data to.
			holder = new MenuView(meal_.get(position), menusActivity_);
		} else {
			holder = (MenuView) convertView;
		}
		
		return holder;
	}

//	private class OnItemClickListener implements OnClickListener {
//		private int mPosition;
//
//		OnItemClickListener(int position) {
//			mPosition = position;
//		}
//
//		@Override
//		public void onClick(View arg0) {
//			// menuDialog(mPosition);
//		}
//	}
	
	public Filter getFilter() {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	// Returns the number of meals in that section.
	public int getCount() {
		return meal_.size();
	}

	// Returns the meal to be represented at that position.
	public Object getItem(int position) {
		return meal_.get(position);
	}
}