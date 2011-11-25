package org.pocketcampus.plugin.satellite.android.display;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.ui.labeler.ILabeler;
import org.pocketcampus.plugin.satellite.shared.Affluence;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

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

	/** The Object's image */
	private ImageView mImage;

	/**
	 * The constructor
	 * 
	 * @param affluence
	 * @param context
	 * @param labeler
	 */
	public AffluenceImageView(Affluence affluence, Context context,
			ILabeler<? extends Object> labeler) {
		super(context);
		this.mConvertView = LayoutInflater
				.from(context.getApplicationContext()).inflate(
						R.layout.satellite_affluence_image_view, null);

		this.mAffluence = affluence;

		this.mImage = (ImageView) mConvertView
				.findViewById(R.id.satellite_affluence_image_view_image);

		initializeView();
	}

	/**
	 * Initializes the View
	 */
	public void initializeView() {

		switch (mAffluence) {
		case EMPTY :
			mImage.setImageDrawable(getResources().getDrawable(
					R.drawable.satellite_affluence_empty));
			break;
		case CLOSED :
			mImage.setImageDrawable(getResources().getDrawable(
					R.drawable.satellite_affluence_empty));
			break;
		case FULL :
			mImage.setImageDrawable(getResources().getDrawable(
					R.drawable.satellite_affluence_empty));
			break;
		case MEDIUM :
			mImage.setImageDrawable(getResources().getDrawable(
					R.drawable.satellite_affluence_empty));
			break;
		case CROWDED :
			mImage.setImageDrawable(getResources().getDrawable(
					R.drawable.satellite_affluence_empty));
			break;
		case ERROR :
			mImage.setImageDrawable(getResources().getDrawable(
					R.drawable.satellite_affluence_empty));
			break;
		default :
			break;
			
		}

		addView(mConvertView);
	}

}
