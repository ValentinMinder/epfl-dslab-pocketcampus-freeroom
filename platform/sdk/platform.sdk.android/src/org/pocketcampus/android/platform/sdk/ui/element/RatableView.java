package org.pocketcampus.android.platform.sdk.ui.element;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.ui.labeler.IRatableLabeler;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

public class RatableView extends LinearLayout {
	private LinearLayout mLayout;
	private IRatableLabeler mLabeler;
	private TextView mTitleLine;
	private TextView mDescriptionLine;
	private RatingBar mRatingLine;
	private TextView mVotesLine;
	private View mConvertView;
	private Object mCurrentObject;
	Context mContext;
	LayoutInflater mInflater;

	public RatableView(Object currentMeal, Context context, IRatableLabeler<? extends Object> labeler) {
		super(context);
		mLabeler = labeler;
		mConvertView = LayoutInflater.from(context.getApplicationContext())
				.inflate(R.layout.sdk_list_entry_ratable_view, null);

		// Creates a ViewHolder and store references to the two children
		// views we want to bind data to.
		this.mLayout = (LinearLayout) mConvertView
				.findViewById(R.id.food_menuentry_list);
		this.mTitleLine = (TextView) mConvertView
				.findViewById(R.id.food_menuentry_title);
		this.mDescriptionLine = (TextView) mConvertView
				.findViewById(R.id.food_menuentry_content);
		this.mRatingLine = (RatingBar) mConvertView
				.findViewById(R.id.food_menuentry_ratingIndicator);
		this.mVotesLine = (TextView) mConvertView
				.findViewById(R.id.food_menuentry_numberOfVotes);
		this.mCurrentObject = currentMeal;
		this.mContext = context;

		initializeView();
	}

	public void initializeView() {
		mLayout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// menuDialog(position);
			}
		});
		
		mTitleLine.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
//				menuDialog();
				Log.d("Click menu", "Click on the titleline");
			}
		});

		mDescriptionLine.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
//				menuDialog();
				Log.d("Click menu", "Click on the menuline");
			}
		});

		// When you click on the rating stars, you can rate the meal.
		mRatingLine.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
//					ratingDialog();
				}
				return true;
			}
		});

		// Bind the data efficiently with the holder.
		mTitleLine.setText(mLabeler.getTitle(mCurrentObject));
		mDescriptionLine.setText(mLabeler.getDescription(mCurrentObject));

		Log.d("MEAL", "Current Rating : " + mLabeler.getRating(mCurrentObject));
		
		setRating(mLabeler.getRating(mCurrentObject), mLabeler.getNbVotes(mCurrentObject));
		addView(mConvertView);
	}

//	private void ratingDialog() {
//		RatingsDialog r = new RatingsDialog(currentMeal_, ctx_);
//		r.setOnDismissListener(new OnDismissListener() {
//
//			@Override
//			public void onDismiss(DialogInterface dialog) {
//				ctx_.notifyDataSetChanged();
//			}
//		});
//		r.show();
//	}

//	private void menuDialog() {
//		MenuDialog r = new MenuDialog(currentMeal_, ctx_);
//		r.show();
//	}

	private void setRating(float currentRating, int numbVotes) {
//		Log.d("MEAL", "Nb Votes : " + numbVotes);
		Log.d("MEAL", "Current rating : " + currentRating);
		mRatingLine.setRating(currentRating);
		
		if (numbVotes != 1) {
			mVotesLine.setText(numbVotes
					+ " "
					+ mContext.getResources().getString(
							R.string.nb_votes_plural));
		} else {
			mVotesLine.setText(numbVotes
					+ " "
					+ mContext.getResources().getString(
							R.string.nb_votes_singular));
		}
	}
}
