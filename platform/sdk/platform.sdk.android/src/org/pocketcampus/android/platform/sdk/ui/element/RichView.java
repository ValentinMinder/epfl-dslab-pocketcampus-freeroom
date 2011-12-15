package org.pocketcampus.android.platform.sdk.ui.element;

import java.util.Date;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.ui.labeler.IRichLabeler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
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
public class RichView extends LinearLayout {
	/** The Application Context */
	private Context mContext;
	/** The convert view */
	private View mConvertView;
	/** The layout */
	private LinearLayout mLayout;

	/** The Object to be displayed in the View */
	private Object mCurrentObject;
	/** The Labeler from the Application to get the Obejct's attributes */
	private IRichLabeler mLabeler;
	/** The position of the Object in the ListView */
	private int mPosition;
	/** The Object's title */
	private TextView mTitleLine;
	/** The Object's description */
	private TextView mDescriptionLine;
	/** The Object's number of votes */
	private TextView mValueLine;
	/** The Object's date */
	private TextView mDateLine;

	/** The click listener on the Object's line */
	private OnItemClickListener mOnElementClickLIstener;

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
	public RichView(Object currentObject, Context context,
			IRichLabeler<? extends Object> labeler,
			OnItemClickListener elementListener, int position) {
		super(context);
		mContext = context;
		mConvertView = LayoutInflater.from(context.getApplicationContext())
				.inflate(R.layout.sdk_list_entry_rich_view, null);

		mCurrentObject = currentObject;
		mLabeler = labeler;
		mPosition = position;

		// Creates a ViewHolder and store references to the two children views
		// we want to bind data to.
		mLayout = (LinearLayout) mConvertView
				.findViewById(R.id.sdk_list_entry_rich_view_layout);
		mTitleLine = (TextView) mConvertView
				.findViewById(R.id.sdk_list_entry_rich_view_title);
		mDescriptionLine = (TextView) mConvertView
				.findViewById(R.id.sdk_list_entry_rich_view_description);
		mValueLine = (TextView) mConvertView
				.findViewById(R.id.sdk_list_entry_rich_view_value);
		mDateLine = (TextView) mConvertView
				.findViewById(R.id.sdk_list_entry_rich_view_date);

		// Listener
		mOnElementClickLIstener = elementListener;

		initializeView();
	}

	/**
	 * Initializes the View
	 */
	public void initializeView() {

		// Sets the click listener on the layout (on the line)
		mLayout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (mOnElementClickLIstener != null) {
					v.setTag(mLabeler.getTitle(mCurrentObject));
					mOnElementClickLIstener.onItemClick(null, v, mPosition, 0);
				}
			}
		});

		// Binds the data efficiently with the holder.

		// Title
		if (mLabeler.getTitle(mCurrentObject) != null) {
			mTitleLine.setText(mLabeler.getTitle(mCurrentObject));
		}

		// Description
		if (mLabeler.getDescription(mCurrentObject) != null) {
			mDescriptionLine.setText(mLabeler.getDescription(mCurrentObject));
		}

		// Price
		if (mLabeler.getValue(mCurrentObject) != -1) {
			mValueLine.setText("" + mLabeler.getValue(mCurrentObject));
		}

		// Date
		if (mLabeler.getDate(mCurrentObject) != null) {
			Date date = mLabeler.getDate(mCurrentObject);
			mDateLine.setText("" + day(date) + " " + (date.getMonth() + 1)
					+ ", " + date.getHours() + ":" + date.getMinutes());
		}

		addView(mConvertView);
	}

	/**
	 * Returns the name of the day for a date.
	 * 
	 * @param date
	 * @return
	 */
	private String day(Date date) {
		String day = "";

		switch (date.getDay()) {
		case 0:
			day = mContext.getResources().getString(R.string.sdk_day_sunday);
			break;
		case 1:
			day = mContext.getResources().getString(R.string.sdk_day_monday);
			break;
		case 2:
			day = mContext.getResources().getString(R.string.sdk_day_tuesday);
			break;
		case 3:
			day = mContext.getResources().getString(R.string.sdk_day_wednesday);
			break;
		case 4:
			day = mContext.getResources().getString(R.string.sdk_day_thursday);
			break;
		case 5:
			day = mContext.getResources().getString(R.string.sdk_day_friday);
			break;
		case 6:
			day = mContext.getResources().getString(R.string.sdk_day_saturday);
		default:
			break;
		}

		return day;
	}
}
