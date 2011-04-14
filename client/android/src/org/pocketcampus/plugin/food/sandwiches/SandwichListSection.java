/**
 * Sandwich List Adapter
 * 
 * @author Oriane
 * 
 */

package org.pocketcampus.plugin.food.sandwiches;

import java.util.Vector;

import org.pocketcampus.R;
import org.pocketcampus.shared.plugin.food.Sandwich;

import android.app.Activity;
import android.content.Context;
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
	private Activity menusActivity_;
	private Context activityContext_;
	private CheckBox box_;

	/**
	 * Constructor
	 * 
	 * @param context
	 *            context of the application the list view is in
	 * @param sandwiches
	 *            all sandwiches represented in the list section.
	 */
	public SandwichListSection(Vector<Sandwich> sandwiches, Activity menus, Context context) {
		// Cache the LayoutInflate to avoid asking for a new one each time.
		mInflater_ = LayoutInflater.from(menus.getApplicationContext());
		this.sandwich_ = sandwiches;
		this.menusActivity_ = menus;
		this.activityContext_ = context;
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
		ViewHolder holder = null;
		// When convertView is not null, we can reuse it directly, there is
		// no need to re-inflate it. We only inflate a new View when the
		// convertView supplied by ListView is null.
		if (convertView == null) {
			System.out.println("YYY");
			convertView = mInflater_.inflate(R.layout.food_sandwich_list_item, null);

			// Creates a ViewHolder and store references to the two children
			// views we want to bind data to.
			holder = new ViewHolder();
			holder.sandwichInfoLine = (LinearLayout) convertView.findViewById(R.id.food_sandwich_lign_list);
			holder.sandwichName = (TextView) convertView.findViewById(R.id.food_sandwich_place);
			holder.sandwichLeft = (CheckBox) convertView.findViewById(R.id.food_sandwich_left_checkbox);

			convertView.setTag(holder);
		} else {
			// Get the ViewHolder back to get fast access to the TextView
			// and the ImageView.
			holder = (ViewHolder) convertView.getTag();
			//System.out.println("XXX");
		}

		//		box_ = holder.sandwichLeft;
		holder.sandwichLeft.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				CheckBox c =  (CheckBox)v;
				SandwichCheckBoxDialog dialog = new SandwichCheckBoxDialog(menusActivity_, activityContext_, sandwich_.get(position), c);
				dialog.show();
			}
		});

		holder.sandwichName.setText(sandwich_.get(position).getName());

		holder.sandwichLeft.setChecked(sandwichLeft(sandwich_.get(position)));

		/* when you click with the dpad center on the sandwich description
		 * Not working yet
		 */
		convertView.setOnClickListener(new OnItemClickListener(position));

		return convertView;
	}

	/* return true if there are at least on Sandwich */
	private boolean sandwichLeft(Sandwich sandwich){
		if(sandwich.isAvailable()){
			return true;
		}else{
			return false;
		}
	}

	private class OnItemClickListener implements OnClickListener {
		private int mPosition;

		OnItemClickListener(int position) {
			mPosition = position;
		}

		@Override
		public void onClick(View arg0) {}
	}

	static class ViewHolder {
		LinearLayout sandwichInfoLine;
		TextView sandwichName;
		CheckBox sandwichLeft;
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