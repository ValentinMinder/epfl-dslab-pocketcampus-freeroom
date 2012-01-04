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
 * A view to display an object labeled with a <code>IRichLabeler</code>. It
 * represents a line of a <code>ListView</code> and contains the object's title
 * and description, along with a <code>Date</code> and a value.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * @author Elodie <elodienilane.triponez@epfl.ch>
 */
public class RichView extends LinearLayout {
	/** The application context. */
	private Context mContext;
	/** The <code>ConvertView</code> */
	private View mConvertView;
	/** The main layout. */
	private LinearLayout mLayout;
	/** The object to be displayed in the view. */
	private Object mCurrentObject;
	/** The labeler from the application to get the object's attributes. */
	@SuppressWarnings("rawtypes")
	private IRichLabeler mLabeler;
	/** The object's position in the <code>ListView</code>. */
	private int mPosition;
	/** The object's title */
	private TextView mTitleLine;
	/** The object's description */
	private TextView mDescriptionLine;
	/** The object's value */
	private TextView mValueLine;
	/** The object's date */
	private TextView mDateLine;
	/** The click listener on the object's line */
	private OnItemClickListener mOnLineClickLIstener;

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
	 * @param position
	 *            The object'position in the list.
	 * @throws IllegalArgumentException
	 *             Thrown if the object is null.
	 * @throws IllegalArgumentException
	 *             Thrown if the labeler is null.
	 */
	public RichView(Object currentObject, Context context,
			IRichLabeler<? extends Object> labeler,
			OnItemClickListener elementListener, int position) {
		super(context);
		mContext = context;
		mConvertView = LayoutInflater.from(context.getApplicationContext())
				.inflate(R.layout.sdk_list_entry_rich_view, null);

		if (currentObject == null) {
			new IllegalArgumentException("Object cannot be null!");
		}
		if (labeler == null) {
			new IllegalArgumentException("Labeler cannot be null!");
		}

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
		mOnLineClickLIstener = elementListener;

		initializeView();
	}

	/**
	 * Initializes the view.
	 */
	@SuppressWarnings("unchecked")
	public void initializeView() {

		// Sets the click listener on the layout (on the line)
		mLayout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (mOnLineClickLIstener != null) {
					v.setTag(mLabeler.getTitle(mCurrentObject));
					mOnLineClickLIstener.onItemClick(null, v, mPosition, 0);
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
	 * Returns the name of the day for a given <code>Date</code>.
	 * 
	 * @param date
	 *            The object's date.
	 * @return day The day as a <code>String</code>.
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
