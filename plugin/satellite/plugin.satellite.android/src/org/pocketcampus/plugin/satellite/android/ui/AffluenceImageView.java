package org.pocketcampus.plugin.satellite.android.ui;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.ui.labeler.IFeedViewLabeler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
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
public class AffluenceImageView extends LinearLayout {
	/** The convert view */
	private View mConvertView;
	/** The Labeler for the Affluence view */
	@SuppressWarnings("rawtypes")
	private IFeedViewLabeler mLabeler;
	/** The Object to display */
	private Object mCurrentObject;
	private int mImageResource;

	/** Title : "Affluence" */
	private TextView mTitle;
	/** The Object's image */
	private ImageView mImage;
	/** Description of the current affluence's image */
	private TextView mDescription;

	/**
	 * The constructor of the view
	 * 
	 * @param object
	 *            The object to be displayed
	 * @param imageResource
	 *            The resource value of the image
	 * @param context
	 *            The application context
	 * @param labeler
	 *            The labeler that says how to display the Object
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
	 * Initializes the View
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
		if (mLabeler.getPictureLayout(mCurrentObject) != null) {
			mImage.setImageDrawable(getResources().getDrawable(mImageResource));
		}

		addView(mConvertView);
	}

}
