package org.pocketcampus.android.platform.sdk.ui.layout;

import org.pocketcampus.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Standard Layout with a title, a picture and a description
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * 
 */
public class FeedInformationLayout extends RelativeLayout {
	private RelativeLayout mLayout;
	private TextView mTitle;
	private ImageView mImage;
	private TextView mDescription;

	public FeedInformationLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}

	public FeedInformationLayout(Context context) {
		super(context);
		initialize(context);
	}

	private void initialize(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		mLayout = (RelativeLayout) inflater.inflate(
				R.layout.sdk_list_entry_feed_view, null);
		super.addView(mLayout);

		mTitle = (TextView) findViewById(R.id.sdk_list_entry_feed_view_title);
		mImage = (ImageView) findViewById(R.id.sdk_list_entry_feed_view_image);
		mDescription = (TextView) findViewById(R.id.sdk_list_entry_feed_view_description);
	}

	/**
	 * Displays a title message.
	 * 
	 * @param text
	 */
	public void setTitle(String text) {
		mTitle.setText(text);
		mTitle.setVisibility(View.VISIBLE);
	}

	/**
	 * Displays a title message.
	 * 
	 * @param text
	 */
	public void setImage(Drawable d) {
		mImage.setImageDrawable(d);
		mImage.setVisibility(View.VISIBLE);
	}

	/**
	 * Displays a title message.
	 * 
	 * @param text
	 */
	public void setDescription(String text) {
		mDescription.setText(text);
		mDescription.setVisibility(View.VISIBLE);
	}

	@Override
	public void addView(View child) {
		mLayout.addView(child);
	}
}
