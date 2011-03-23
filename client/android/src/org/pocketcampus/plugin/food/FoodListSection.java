package org.pocketcampus.plugin.food;

import java.util.Vector;

import org.pocketcampus.R;
import org.pocketcampus.plugin.food.menu.Meal;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * This class is used to make each section of a list of menus.
 * 
 */
public class FoodListSection extends BaseAdapter implements Filterable {
	private LayoutInflater mInflater_;
	private Vector<Meal> meal_;
	private Activity menusActivity_;

	/**
	 * Constructor
	 * 
	 * @param context
	 *            context of the application the list view is in
	 * @param resto
	 *            restaurant full menu represented in the list section.
	 */
	public FoodListSection(Vector<Meal> resto, Activity menus) {
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
		ViewHolder holder;
		// When convertView is not null, we can reuse it directly, there is
		// no need to re-inflate it. We only inflate a new View when the
		// convertView supplied by ListView is null.
		if (convertView == null) {
			convertView = mInflater_.inflate(R.layout.food_menuentry, null);

			// Creates a ViewHolder and store references to the two children
			// views we want to bind data to.
			holder = new ViewHolder();
			holder.menuInfoLine = (LinearLayout) convertView
					.findViewById(R.id.food_menuentry_list);
			holder.titleLine = (TextView) convertView
					.findViewById(R.id.food_menuentry_title);
			holder.menuLine = (TextView) convertView
					.findViewById(R.id.food_menuentry_content);
			holder.ratingLine = (RatingBar) convertView
					.findViewById(R.id.food_menuentry_ratingIndicator);
			holder.votesLine = (TextView) convertView
					.findViewById(R.id.food_menuentry_numberOfVotes);

			convertView.setTag(holder);
		} else {
			// Get the ViewHolder back to get fast access to the TextView
			// and the ImageView.
			holder = (ViewHolder) convertView.getTag();
		}

		holder.menuLine.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// menuDialog(position);
				Log.d("Click menu", "Click on the menuline");
			}
		});

		holder.menuInfoLine.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// menuDialog(position);
			}
		});

		/* when you click with the dpad center on the menu description */
		/*
		 * Not working yet
		 */
		convertView.setOnClickListener(new OnItemClickListener(position));

		// When you click on the rating stars, you can rate the meal.
		holder.ratingLine.setOnTouchListener(new OnTouchListener() {
			private int pos = position;

			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					rate(position);
				}
				return true;
			}
		});

		// Bind the data efficiently with the holder.
		Meal currentMeal = meal_.get(position);

		holder.menuLine.setText(currentMeal.getDescription());
		holder.titleLine.setText(currentMeal.getName());

		new RatingsDownloader(currentMeal, menusActivity_, holder);

		return convertView;
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

	private void rate(int pos) {
		RatingsDialog r = new RatingsDialog(menusActivity_,
				meal_.get(pos));
		r.show();
	}

	/* show the sandwich's list of the store */
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
		TextView titleLine;
		TextView menuLine;
		RatingBar ratingLine;
		TextView votesLine;
		LinearLayout menuInfoLine;
	}

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