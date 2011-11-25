package org.pocketcampus.plugin.satellite.android.display;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.ui.labeler.IFeedViewLabeler;
import org.pocketcampus.plugin.satellite.shared.Affluence;

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
	/** The Affluence to be displayed in the View */
	private Affluence mAffluence;
	/** The Labeler for the Affluence view */
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
	 * The constructor
	 * 
	 * @param affluence
	 * @param context
	 * @param labeler
	 */
	public AffluenceImageView(Object object, int imageResource, Context context,
			IFeedViewLabeler<? extends Object> labeler) {
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
	public void initializeView() {

		/** Title */
		mTitle.setText(mLabeler.getTitle(mCurrentObject));

		/** Description */
		mDescription.setText(mLabeler.getDescription(mCurrentObject));

		/** Image */
		mImage.setImageDrawable(getResources().getDrawable(
				mImageResource));


		addView(mConvertView);
	}

}
