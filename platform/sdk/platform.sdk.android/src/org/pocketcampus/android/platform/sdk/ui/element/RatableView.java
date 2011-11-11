package org.pocketcampus.android.platform.sdk.ui.element;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.ui.dialog.RatingDialog;
import org.pocketcampus.android.platform.sdk.ui.labeler.IRatableLabeler;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
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
	private OnItemClickListener mOnRatingClickListener;
	private int mPosition;

	public RatableView(Object currentMeal, Context context, IRatableLabeler<? extends Object> labeler, OnItemClickListener l, int position) {
		super(context);
		mLabeler = labeler;
		mConvertView = LayoutInflater.from(context.getApplicationContext())
				.inflate(R.layout.sdk_list_entry_ratable_view, null);
		mOnRatingClickListener = l;
		mPosition = position;

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
		
		mTitleLine.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
//				menuDialog();
				Log.d("MENUDIALOG", "Click on the titleline");
			}
		});

		mDescriptionLine.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
//				menuDialog();
				Log.d("MENUDIALOG", "Click on the menuline");
			}
		});

		// When you click on the rating stars, you can rate the meal.
		mRatingLine.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					ratingDialog();
				}
				return true;
			}
		});

		// Bind the data efficiently with the holder.
		mTitleLine.setText(mLabeler.getTitle(mCurrentObject));
		mDescriptionLine.setText(mLabeler.getDescription(mCurrentObject));

		setRating(mLabeler.getRating(mCurrentObject), mLabeler.getNbVotes(mCurrentObject));
		addView(mConvertView);
	}

	private void ratingDialog() {
		RatingDialog r = new RatingDialog(mCurrentObject, mContext, mOnRatingClickListener, mPosition);
		r.show();
	}


//	private void menuDialog() {
//		MenuDialog r = new MenuDialog(currentMeal_, ctx_);
//		r.show();
//	}

	private void setRating(float currentRating, int numbVotes) {
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
