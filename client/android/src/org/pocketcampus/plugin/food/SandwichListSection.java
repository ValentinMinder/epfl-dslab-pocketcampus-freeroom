package org.pocketcampus.plugin.food;

import java.util.Vector;

import org.pocketcampus.R;
import org.pocketcampus.plugin.food.menu.Meal;
import org.pocketcampus.plugin.food.menu.Sandwich;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * This class is used to make each section of a list of menus.
 * 
 */
public class SandwichListSection extends BaseAdapter implements Filterable {
	private LayoutInflater mInflater_;
	private Vector<Sandwich> sandwich_;
	private Activity menusActivity_;

	/**
	 * Constructor
	 * 
	 * @param context
	 *            context of the application the list view is in
	 * @param sandwiches
	 *            all sandwiches represented in the list section.
	 */
	public SandwichListSection(Vector<Sandwich> sandwiches, Activity menus) {
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
		ViewHolder holder;
		// When convertView is not null, we can reuse it directly, there is
		// no need to re-inflate it. We only inflate a new View when the
		// convertView supplied by ListView is null.
		if (convertView == null) {
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
		}

		holder.sandwichInfoLine.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
//				checkBoxDialog(position);
			}
		});

		holder.sandwichName.setText(sandwich_.get(position).getName());
		
		holder.sandwichLeft.setChecked(sandwichLeft(sandwich_));

		/* when you click with the dpad center on the menu description */
		/*
		 * Not working yet
		 */
		convertView.setOnClickListener(new OnItemClickListener(position));

		return convertView;
	}
	
	/* return true if there are at least on Sandwich */
	private boolean sandwichLeft(Vector<Sandwich> store){
		for(Sandwich i: store){
			if(i.isAvailable()){
				/* we have at least one sandwich available */
				return true;
			}
			else { /* this sandwich is not available */ }
		}
		return false;
	}

	private class OnItemClickListener implements OnClickListener {
		private int mPosition;

		OnItemClickListener(int position) {
			mPosition = position;
		}

		@Override
		public void onClick(View arg0) {
			// menuDialog(mPosition);
		}
	}

	// private void menuDialog(int pos){
	// boolean isDailyMenu = false;
	// if(context_.getClass().getName().equals(DailyMenus.class.getName())){
	// isDailyMenu = true;
	// }
	//		
	// MenuDialog r = new MenuDialog(context_, meal_.get(pos),
	// menusActivity_, isDailyMenu);
	// r.setOnDismissListener(new OnDismissMenuDialogListener());
	// r.show();
	// }

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

	// private class OnDismissMenuDialogListener implements
	// MenuDialog.OnDismissListener {
	//
	// @Override
	// public void onDismiss(DialogInterface dialogInt) {
	// notifyDataSetChanged();
	// }
	// }

	// protected void dataSetChanged() {
	// this.sortNews();
	// this.notifyDataSetChanged();
	// }
}