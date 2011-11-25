package org.pocketcampus.android.platform.sdk.ui.element;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.ui.labeler.IFeedViewLabeler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
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
	private IFeedViewLabeler mLabeler;
	/** The position of the Object in the ListView */
	private int mPosition;
	/** The Object's title */
	private TextView mTitleLine;
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
			IFeedViewLabeler<? extends Object> labeler, int position) {
		super(context);
		this.mConvertView = LayoutInflater
				.from(context.getApplicationContext()).inflate(
						R.layout.sdk_image_text_layout, null);

		this.mCurrentObject = currentObject;
		this.mLabeler = labeler;
		this.mPosition = position;

		this.mTitleLine = (TextView) mConvertView
				.findViewById(R.id.sdk_image_text_layout_title);

		this.mDescriptionLine = (TextView) mConvertView
				.findViewById(R.id.sdk_image_text_layout_description);

		this.mImage = (LinearLayout) mConvertView
				.findViewById(R.id.sdk_image_text_layout_image);

		initializeView();
	}

	/**
	 * Initializes the View
	 */
	public void initializeView() {

		/** Title */
		mTitleLine.setText(mLabeler.getTitle(mCurrentObject));

		/** Description */
		mDescriptionLine.setText(mLabeler.getDescription(mCurrentObject));

		/** Image */
		mImage.addView(mLabeler.getPictureLayout(mCurrentObject));

		addView(mConvertView);
	}

}
