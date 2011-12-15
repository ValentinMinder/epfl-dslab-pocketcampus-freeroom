package org.pocketcampus.android.platform.sdk.ui.element;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.ui.labeler.ISubtitledFeedViewLabeler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A view to display an Object represented as a title, an image and a
 * description.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 */
public class ImageTextView extends LinearLayout {
	/** The convert view */
	private View mConvertView;
	/** The Object to be displayed in the View */
	private Object mCurrentObject;
	/** The Labeler from the Application to get the Obejct's attributes */
	private ISubtitledFeedViewLabeler mLabeler;
	/** The position of the Object in the ListView */
	private int mPosition;
	/** The Object's title */
	private TextView mTitleLine;
	/** The Object's subtitle */
	private TextView mSubtitleLine;
	/** The Object's description */
	private TextView mDescriptionLine;

	/** The Object's image */
	private LinearLayout mImage;

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
	public ImageTextView(Object currentObject, Context context,
			ISubtitledFeedViewLabeler<? extends Object> labeler, int position) {
		super(context);
		mConvertView = LayoutInflater.from(context.getApplicationContext())
				.inflate(R.layout.sdk_image_text_layout, null);

		mCurrentObject = currentObject;
		mLabeler = labeler;
		mPosition = position;

		mTitleLine = (TextView) mConvertView
				.findViewById(R.id.sdk_image_text_layout_title);

		mSubtitleLine = (TextView) mConvertView
				.findViewById(R.id.sdk_image_text_layout_subtitle);

		mDescriptionLine = (TextView) mConvertView
				.findViewById(R.id.sdk_image_text_layout_description);

		mImage = (LinearLayout) mConvertView
				.findViewById(R.id.sdk_image_text_layout_image);

		initializeView();
	}

	/**
	 * Initializes the View
	 */
	public void initializeView() {

		// Title
		if (mLabeler.getTitle(mCurrentObject) != null) {
			mTitleLine.setText(mLabeler.getTitle(mCurrentObject));
		}

		// Subtitle
		if (mLabeler.getSubtitle(mCurrentObject) != null) {
			mSubtitleLine.setText(mLabeler.getSubtitle(mCurrentObject));
		}

		// Description
		if (mLabeler.getDescription(mCurrentObject) != null) {
			mDescriptionLine.setText(mLabeler.getDescription(mCurrentObject));
		}

		// Image
		if (mLabeler.getPictureLayout(mCurrentObject) != null) {
			mImage.addView(mLabeler.getPictureLayout(mCurrentObject));
		}

		addView(mConvertView);
	}

}
