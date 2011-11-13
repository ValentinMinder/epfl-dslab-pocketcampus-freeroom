package org.pocketcampus.android.platform.sdk.ui.element;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.ui.labeler.IRatableViewLabeler;

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
	private IRatableViewLabeler mLabeler;
	private TextView mTitleLine;
	private TextView mDescriptionLine;
	private RatingBar mRatingLine;
	private TextView mVotesLine;
	private View mConvertView;
	private Object mCurrentObject;
	Context mContext;
	LayoutInflater mInflater;
	private OnItemClickListener mOnElementClickLIstener;
	private OnItemClickListener mOnRatingClickListener;
	private int mPosition;

	public RatableView(Object currentObject, Context context,
			IRatableViewLabeler<? extends Object> labeler,
			OnItemClickListener elementListener,
			OnItemClickListener ratingListener, int position) {
		super(context);
		mLabeler = labeler;
		mConvertView = LayoutInflater.from(context.getApplicationContext())
				.inflate(R.layout.sdk_list_entry_ratable_view, null);
		mOnElementClickLIstener = elementListener;
		mOnRatingClickListener = ratingListener;
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
		this.mCurrentObject = currentObject;
		this.mContext = context;

		initializeView();
	}

	public void initializeView() {

		mTitleLine.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (mOnElementClickLIstener != null) {
					v.setTag(mLabeler.getRestaurantName(mCurrentObject));
					mOnElementClickLIstener.onItemClick(null, v, mPosition, 0);
				}
			}
		});

		mDescriptionLine.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (mOnElementClickLIstener != null) {
					v.setTag(mLabeler.getRestaurantName(mCurrentObject));
					mOnElementClickLIstener.onItemClick(null, v, mPosition, 0);
				}
			}
		});

		// When you click on the rating stars, you can rate the meal.
		mRatingLine.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP
						&& mOnRatingClickListener != null) {
					v.setTag(mLabeler.getRestaurantName(mCurrentObject));
					mOnRatingClickListener.onItemClick(null, v, mPosition,
							(long) mLabeler.getRating(mCurrentObject));
				}
				return true;
			}
		});

		// Bind the data efficiently with the holder.
		mTitleLine.setText(mLabeler.getTitle(mCurrentObject));
		mDescriptionLine.setText(mLabeler.getDescription(mCurrentObject));

		setRating(mLabeler.getRating(mCurrentObject),
				mLabeler.getNbVotes(mCurrentObject));
		addView(mConvertView);
	}

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
