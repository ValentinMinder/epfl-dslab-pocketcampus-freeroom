package org.pocketcampus.platform.android.ui.element;

import org.pocketcampus.platform.android.R;
import org.pocketcampus.platform.android.ui.labeler.IRatableViewLabeler;

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
	/** The application context. */
	private Context mContext;
	/** The <code>ConvertView</code>. */
	private View mConvertView;
	/** The object to be displayed in the view. */
	private Object mCurrentObject;
	/** The labeler from the application to get the object's attributes. */
	private IRatableViewLabeler mLabeler;
	/** The object's position in the <code>ListView</code>. */
	private int mPosition;
	/** The object's title. */
	private TextView mTitleLine;
	/** The object's description. */
	private TextView mDescriptionLine;
	/** The <code>RatingBar</code>. */
	private RatingBar mRatingLine;
	/** The number of votes. */
	private TextView mVotesLine;
	/** The click listener on the object's title and description. */
	private OnItemClickListener mOnElementClickLIstener;
	/** The listener on the <code>RatingBar</code>. */
	private OnItemClickListener mOnRatingClickListener;

	/**
	 * Class constructor.
	 * 
	 * @param currentObject
	 *            The object to be displayed in the line.
	 * @param context
	 *            The application context.
	 * @param labeler
	 *            The object's labeler.
	 * @param elementListener
	 *            The listener for the title and description lines.
	 * @param ratingListener
	 *            The listener on the <code>RatingBar</code>.
	 * @param position
	 *            The object's position in the List.
	 * @throws IllegalArgumentException
	 *             Thrown if the object is null.
	 * @throws IllegalArgumentException
	 *             Thrown if the labeler is null.
	 * @throws IllegalArgumentException
	 *             Thrown if the rating listener is null.
	 * @throws IllegalArgumentException
	 *             Thrown if the element listener is null.
	 */
	public RatableView(Object currentObject, Context context,
			IRatableViewLabeler<? extends Object> labeler,
			OnItemClickListener elementListener,
			OnItemClickListener ratingListener, int position) {
		super(context);
		mContext = context;
		mConvertView = LayoutInflater.from(context.getApplicationContext())
				.inflate(R.layout.sdk_list_entry_ratable_view, null);

		if (currentObject == null) {
			new IllegalArgumentException("Object cannot be null!");
		}
		if (labeler == null) {
			new IllegalArgumentException("Labeler cannot be null!");
		}
		if (ratingListener == null) {
			new IllegalArgumentException(
					"Listener on the rating cannot be null!");
		}
		if (elementListener == null) {
			new IllegalArgumentException(
					"Listener on the element cannot be null!");
		}

		mCurrentObject = currentObject;
		mLabeler = labeler;
		mPosition = position;

		// Creates a ViewHolder and store references to the two children views
		// we want to bind data to.
		this.mTitleLine = (TextView) mConvertView
				.findViewById(R.id.food_menuentry_title);
		this.mDescriptionLine = (TextView) mConvertView
				.findViewById(R.id.food_menuentry_content);
		this.mRatingLine = (RatingBar) mConvertView
				.findViewById(R.id.food_menuentry_ratingIndicator);
		this.mVotesLine = (TextView) mConvertView
				.findViewById(R.id.food_menuentry_numberOfVotes);

		// Listeners
		mOnElementClickLIstener = elementListener;
		mOnRatingClickListener = ratingListener;

		initializeView();
	}

	/**
	 * Initializes the view.
	 */
	public void initializeView() {

		// Title line click listener
		mTitleLine.setOnClickListener(new OnClickListener() {

			/**
			 * Defines what has to be performed when the title is clicked.
			 */
			public void onClick(View v) {
				if (mOnElementClickLIstener != null) {
					v.setTag(mLabeler.getPlaceName(mCurrentObject));
					mOnElementClickLIstener.onItemClick(null, v, mPosition, 0);
				}
			}
		});

		// Description line click listener
		mDescriptionLine.setOnClickListener(new OnClickListener() {

			/**
			 * Defines what has to be performed when the description is clicked.
			 */
			public void onClick(View v) {
				if (mOnElementClickLIstener != null) {
					v.setTag(mLabeler.getPlaceName(mCurrentObject));
					mOnElementClickLIstener.onItemClick(null, v, mPosition, 0);
				}
			}
		});

		// Rating bar click listener
		mRatingLine.setOnTouchListener(new OnTouchListener() {

			/**
			 * Defines what has to be performed when the rating is touched.
			 */
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

		// Bind the data efficiently with the holder.
		// Title
		mTitleLine.setText(mLabeler.getLabel(mCurrentObject));
		// Description
		mDescriptionLine.setText(mLabeler.getDescription(mCurrentObject));

		// Rating
		setRating(mLabeler.getRating(mCurrentObject),
				mLabeler.getNumberOfVotes(mCurrentObject));

		addView(mConvertView);
	}

	/**
	 * Sets the rating in the <code>RatingBar</code> and the number of votes
	 * text.
	 * 
	 * @param currentRating
	 *            The object's current rating.
	 * @param numbVotes
	 *            The object's current number of votes.
	 */
	private void setRating(float currentRating, int numbVotes) {
		mRatingLine.setRating(currentRating);

		// Checks if the number of votes is singular or plural and sets the text
		// accordingly.
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
