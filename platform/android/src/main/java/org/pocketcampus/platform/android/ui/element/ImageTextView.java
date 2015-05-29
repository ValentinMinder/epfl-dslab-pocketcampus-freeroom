package org.pocketcampus.platform.android.ui.element;

import org.pocketcampus.platform.android.R;
import org.pocketcampus.platform.android.ui.labeler.ISubtitledFeedViewLabeler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A view to display an object consisting of a title, an image and a
 * description.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 */
public class ImageTextView extends LinearLayout {
	/** The <code>ConvertView</code>. */
	private View mConvertView;
	/** The object to be displayed in the view. */
	private Object mCurrentObject;
	/** The labeler from the application to get the object's attributes. */
	@SuppressWarnings("rawtypes")
	private ISubtitledFeedViewLabeler mLabeler;
	/** The object's title. */
	private TextView mTitleLine;
	/** The object's subtitle. */
	private TextView mSubtitleLine;
	/** The object's description. */
	private TextView mDescriptionLine;
	/** The object's image. */
	private LinearLayout mImage;

	/**
	 * Class constructor.
	 * 
	 * @param currentObject
	 *            The object to be displayed in the line.
	 * @param context
	 *            The application context.
	 * @param labeler
	 *            The object's labeler.
	 * @throws IllegalArgumentException
	 *             Thrown if the object is null.
	 * @throws IllegalArgumentException
	 *             Thrown if the labeler is null.
	 */
	public ImageTextView(Object currentObject, Context context,
			ISubtitledFeedViewLabeler<? extends Object> labeler) {
		super(context);
		mConvertView = LayoutInflater.from(context.getApplicationContext())
				.inflate(R.layout.sdk_image_text_layout, null);

		if (currentObject == null) {
			new IllegalArgumentException("Object cannot be null!");
		}
		if (labeler == null) {
			new IllegalArgumentException("Labeler cannot be null!");
		}

		mCurrentObject = currentObject;
		mLabeler = labeler;

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
	 * Initializes the view.
	 */
	@SuppressWarnings("unchecked")
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
