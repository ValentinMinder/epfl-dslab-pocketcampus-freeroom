package org.pocketcampus.platform.android.ui.layout;

import org.pocketcampus.platform.android.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Standard Layout with a title, a picture and a description
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * 
 */
public class EventInformationLayout extends RelativeLayout {
	/** The layout containing the page */
	private RelativeLayout mLayout;
	/** The title of the layout */
	private TextView mTitle;
	/** The corresponding description */
	private TextView mTextContent;
	/** The feed it's from */
	private TextView mFeedTitle;
	/** The organizer */
	private TextView mOrganizer;

	/**
	 * 
	 * Constructor
	 * 
	 * @param context
	 *            the context of the calling activity
	 * @param attrs
	 */
	public EventInformationLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}

	/**
	 * Constructor
	 * 
	 * @param context
	 *            the context of the calling activity
	 */
	public EventInformationLayout(Context context) {
		super(context);
		initialize(context);
	}

	/**
	 * Initializes the layout and the contained elements
	 * 
	 * @param context
	 *            the context of the calling activity
	 */
	private void initialize(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		mLayout = (RelativeLayout) inflater.inflate(
				R.layout.sdk_feed_information_layout, null);
		super.addView(mLayout);

		mTitle = (TextView) findViewById(R.id.sdk_list_entry_feed_view_title);
		mFeedTitle = (TextView) findViewById(R.id.sdk_list_entry_feed_view_feed);
		mTextContent = (TextView) findViewById(R.id.sdk_list_entry_feed_view_description);
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
	 * Displays a content message.
	 * 
	 * @param text
	 *            the content to be displayed
	 */
	public void setDescription(String text) {
		mTextContent.setText(text);
		mTextContent.setVisibility(View.VISIBLE);
	}

	/**
	 * Displays a feed title.
	 * 
	 * @param text
	 *            the content to be displayed
	 */
	public void setFeedTitle(String text) {
		mFeedTitle.setText(text);
		mFeedTitle.setVisibility(View.VISIBLE);
	}

	/**
	 * Adds a child view to the layout
	 */
	@Override
	public void addView(View child) {
		mLayout.addView(child);
	}
}
