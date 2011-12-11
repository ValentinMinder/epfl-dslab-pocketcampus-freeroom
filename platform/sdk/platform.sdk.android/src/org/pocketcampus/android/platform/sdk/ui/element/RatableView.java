package org.pocketcampus.android.platform.sdk.ui.element;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.ui.labeler.IRatableViewLabeler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * A view to display an Object that the user can rate. It represents a line of a
 * ListView and contains the object's title and description, along with a rating
 * bar and the number of votes that Object got. It's designed to be used with
 * the RatableExpandableListView or an equivalent, and can be created directly
 * in the Application View.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * @author Elodie <elodienilane.triponez@epfl.ch>
 */
public class RatableView extends LinearLayout {
	/** The Application Context */
	private Context mContext;
	/** The convert view */
	private View mConvertView;
	/** The Object to be displayed in the View */
	private Object mCurrentObject;
	/** The Labeler from the Application to get the Obejct's attributes */
	private IRatableViewLabeler mLabeler;
	/** The position of the Object in the ListView */
	private int mPosition;
	/** The Object's title */
	private TextView mTitleLine;
	/** The Object's description */
	private TextView mDescriptionLine;
	/** The rating bar */
	private RatingBar mRatingLine;
	/** The number of votes */
	private TextView mVotesLine;
	/** The click listener on the Object's title and description */
	private OnItemClickListener mOnElementClickLIstener;
	/** The listener on the rating bar */
	private OnItemClickListener mOnRatingClickListener;

	/**
	 * The constructor
	 * 
	 * @param currentObject
	 *            The Object to be displayed in the line
	 * @param context
	 *            The Application context
	 * @param labeler
	 *            The Object's labeler
	 * @param elementListener
	 *            the listener for the title and description lines
	 * @param ratingListener
	 *            the listener on the rating bar
	 * @param position
	 *            the position of the Object in the List
	 */
	public RatableView(Object currentObject, Context context,
			IRatableViewLabeler<? extends Object> labeler,
			OnItemClickListener elementListener,
			OnItemClickListener ratingListener, int position) {
		super(context);
		mContext = context;
		mConvertView = LayoutInflater.from(context.getApplicationContext())
				.inflate(R.layout.sdk_list_entry_ratable_view, null);

		this.mCurrentObject = currentObject;
		mLabeler = labeler;
		mPosition = position;

		/**
		 * Creates a ViewHolder and store references to the two children views
		 * we want to bind data to.
		 */
		this.mTitleLine = (TextView) mConvertView
				.findViewById(R.id.food_menuentry_title);
		this.mDescriptionLine = (TextView) mConvertView
				.findViewById(R.id.food_menuentry_content);
		this.mRatingLine = (RatingBar) mConvertView
				.findViewById(R.id.food_menuentry_ratingIndicator);
		this.mVotesLine = (TextView) mConvertView
				.findViewById(R.id.food_menuentry_numberOfVotes);

		/** Listeners */
		mOnElementClickLIstener = elementListener;
		mOnRatingClickListener = ratingListener;

		initializeView();
	}

	/**
	 * Initializes the View
	 */
	public void initializeView() {

		/** title line click listener */
		mTitleLine.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (mOnElementClickLIstener != null) {
					v.setTag(mLabeler.getPlaceName(mCurrentObject));
					mOnElementClickLIstener.onItemClick(null, v, mPosition, 0);
				}
			}
		});

		/** description line click listener */
		mDescriptionLine.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (mOnElementClickLIstener != null) {
					v.setTag(mLabeler.getPlaceName(mCurrentObject));
					mOnElementClickLIstener.onItemClick(null, v, mPosition, 0);
				}
			}
		});

		/** rating bar click listener */
		mRatingLine.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP
						&& mOnRatingClickListener != null) {
					v.setTag(mLabeler.getPlaceName(mCurrentObject));
					mOnRatingClickListener.onItemClick(null, v, mPosition,
							(long) mLabeler.getRating(mCurrentObject));
				}
				return true;
			}
		});

		/** Bind the data efficiently with the holder. */

		/** Title */
		mTitleLine.setText(mLabeler.getLabel(mCurrentObject));
		/** Description */
		mDescriptionLine.setText(mLabeler.getDescription(mCurrentObject));

		/** Rating */
		setRating(mLabeler.getRating(mCurrentObject),
				mLabeler.getNumberOfVotes(mCurrentObject));

		addView(mConvertView);
	}

	/**
	 * Sets the rating in the rating bar and the number of votes text
	 * 
	 * @param currentRating
	 *            The current Object's rating
	 * @param numbVotes
	 *            The current Object's number of votes
	 */
	private void setRating(float currentRating, int numbVotes) {
		mRatingLine.setRating(currentRating);

		/** Checks if the number of votes is singular or plural */
		if (numbVotes != 1) {
			mVotesLine.setText(numbVotes
					+ " "
					+ mContext.getResources().getString(
							R.string.sdk_nb_votes_plural));
		} else {
			mVotesLine.setText(numbVotes
					+ " "
					+ mContext.getResources().getString(
							R.string.sdk_nb_votes_singular));
		}
	}

}
