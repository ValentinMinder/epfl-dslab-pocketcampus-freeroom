package org.pocketcampus.plugin.satellite.android.ui;

import org.pocketcampus.plugin.satellite.R;
import org.pocketcampus.platform.android.ui.labeler.IFeedViewLabeler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A view to display a picture along with a title and a description.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 */
public class AffluenceImageView extends LinearLayout {
	/** The <code>ConvertView</code> */
	private View mConvertView;
	/** The labeler to get the object's attributes */
	@SuppressWarnings("rawtypes")
	private IFeedViewLabeler mLabeler;
	/** The object to display */
	private Object mCurrentObject;
	/** The image resource */
	private int mImageResource;
	/** The title : "Affluence" */
	private TextView mTitle;
	/** The object's image */
	private ImageView mImage;
	/** The description of the current affluence's image */
	private TextView mDescription;

	/**
	 * Class constructor.
	 * 
	 * @param object
	 *            The object to be displayed.
	 * @param imageResource
	 *            The resource value of the image.
	 * @param context
	 *            The application context.
	 * @param labeler
	 *            The labeler that says how to display the object.
	 */
	public AffluenceImageView(Object object, int imageResource,
			Context context, IFeedViewLabeler<? extends Object> labeler) {
		super(context);
		this.mConvertView = LayoutInflater
				.from(context.getApplicationContext()).inflate(
						R.layout.satellite_affluence_image_view, null);

		mLabeler = labeler;
		mCurrentObject = object;
		mImageResource = imageResource;

		mTitle = (TextView) mConvertView
				.findViewById(R.id.satellite_affluence_image_view_title);
		mImage = (ImageView) mConvertView
				.findViewById(R.id.satellite_affluence_image_view_image);
		mDescription = (TextView) mConvertView
				.findViewById(R.id.satellite_affluence_image_view_description);

		initializeView();
	}

	/**
	 * Initializes the view.
	 */
	@SuppressWarnings("unchecked")
	public void initializeView() {

		// Title
		if (mLabeler.getTitle(mCurrentObject) != null) {
			mTitle.setText(mLabeler.getTitle(mCurrentObject));
		}

		// Description
		if (mLabeler.getDescription(mCurrentObject) != null) {
			mDescription.setText(mLabeler.getDescription(mCurrentObject));
		}

		// Image
		mImage.setImageDrawable(getResources().getDrawable(mImageResource));

		addView(mConvertView);
	}

}
