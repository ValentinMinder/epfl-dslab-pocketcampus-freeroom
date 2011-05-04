package org.pocketcampus.plugin.food;

import org.pocketcampus.R;
import org.pocketcampus.shared.plugin.food.Meal;
import org.pocketcampus.shared.plugin.food.Rating;
import org.pocketcampus.shared.plugin.food.Restaurant;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

public class MenuView extends LinearLayout {
	TextView titleLine_;
	TextView menuLine_;
	RatingBar ratingLine_;
	TextView votesLine_;
	LinearLayout menuInfoLine_;
	View convertView;
	Meal currentMeal_;
	FoodPlugin ctx_;
	LayoutInflater mInflater_;

	MenuView(FoodPlugin context, Meal currentMeal) {
		super(context);
		convertView = LayoutInflater.from(context.getApplicationContext())
				.inflate(R.layout.food_menuentry, null);

		// Creates a ViewHolder and store references to the two children
		// views we want to bind data to.
		this.menuInfoLine_ = (LinearLayout) convertView
				.findViewById(R.id.food_menuentry_list);
		this.titleLine_ = (TextView) convertView
				.findViewById(R.id.food_menuentry_title);
		this.menuLine_ = (TextView) convertView
				.findViewById(R.id.food_menuentry_content);
		this.ratingLine_ = (RatingBar) convertView
				.findViewById(R.id.food_menuentry_ratingIndicator);
		this.votesLine_ = (TextView) convertView
				.findViewById(R.id.food_menuentry_numberOfVotes);
		this.currentMeal_ = currentMeal;
		this.ctx_ = context;

		initializeView();
	}

	public void initializeView() {
		titleLine_.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				menuDialog();
				Log.d("Click menu", "Click on the titleline");
			}
		});

		menuLine_.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				menuDialog();
				Log.d("Click menu", "Click on the menuline");
			}
		});

		menuInfoLine_.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// menuDialog(position);
			}
		});

		// When you click on the rating stars, you can rate the meal.
		ratingLine_.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					ratingDialog();
				}
				return true;
			}
		});

		// Bind the data efficiently with the holder.
		titleLine_.setText(currentMeal_.getName_());
		menuLine_.setText(currentMeal_.getDescription_());

		setRating(currentMeal_.getRating());
		addView(convertView);
	}

	private void ratingDialog() {
		RatingsDialog r = new RatingsDialog(ctx_, currentMeal_);
		r.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				ctx_.notifyDataSetChanged();
			}
		});
		r.show();
	}

	private void menuDialog() {
		MenuDialog r = new MenuDialog(currentMeal_, ctx_, true);
		r.show();
	}

	private void setRating(Rating currentRating) {
		ratingLine_.setRating((float) Restaurant
				.starRatingToDouble(currentRating.getValue()));

		int numbVotes = currentRating.getNumberOfVotes();
		if (numbVotes != 1) {
			votesLine_.setText(numbVotes
					+ " "
					+ ctx_.getResources().getString(
							R.string.food_menulist_votesPlural));
		} else {
			votesLine_.setText(numbVotes
					+ " "
					+ ctx_.getResources().getString(
							R.string.food_menulist_votesSingular));
		}
	}
}
