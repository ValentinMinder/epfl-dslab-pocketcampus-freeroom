package org.pocketcampus.platform.android.ui.element;

import org.pocketcampus.platform.android.R;
import org.pocketcampus.platform.android.ui.labeler.IFeedViewLabeler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A view to display an Object that represents a Feed entry. It represents a
 * line of a ListView and contains the object's title and description along with
 * a picture. It's designed to be used with the FeedListView or an equivalent,
 * and can be created directly in the Application View.
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 */
public class FeedWithImageView extends LinearLayout {
	/** The convert view */
	private View mConvertView;
	/** The Object to be displayed in the View */
	private Object mCurrentObject;
	/** The Labeler from the Application to get the Obejct's attributes */
	private IFeedViewLabeler mLabeler;
	/** The position of the Object in the ListView */
	private int mPosition;
	/** The Object's title */
	private TextView mTitleLine;

	/** The Object's image */
	private LinearLayout mImage;
	/** The click listener on the Object's title and description */
	private OnItemClickListener mOnElementClickListener;

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
	 * @param position
	 *            the position of the Object in the List
	 */
	public FeedWithImageView(Object currentObject, Context context,
			IFeedViewLabeler<Object> labeler,
			OnItemClickListener elementListener, int position) {
		super(context);
		this.mConvertView = LayoutInflater
				.from(context.getApplicationContext()).inflate(
						R.layout.sdk_list_entry_feed_with_image
						, null);

		this.mCurrentObject = currentObject;
		this.mLabeler = labeler;
		this.mPosition = position;

		this.mTitleLine = (TextView) mConvertView
				.findViewById(R.id.sdk_list_entry_feed_with_image_title);

		this.mImage = (LinearLayout) mConvertView
				.findViewById(R.id.sdk_list_entry_feed_with_image_image);

		/** Listener */
		this.mOnElementClickListener = elementListener;

		initializeView();
	}

	/**
	 * Initializes the View
	 */
	public void initializeView() {

		/** Title */
		mTitleLine.setText(mLabeler.getTitle(mCurrentObject));

		mImage.addView(mLabeler.getPictureLayout(mCurrentObject));

		addView(mConvertView);
	}

}
