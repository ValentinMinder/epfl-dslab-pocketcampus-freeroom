package org.pocketcampus.platform.android.ui.layout;

import org.pocketcampus.platform.android.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Standard <code>Layout</code> with a centered message and a title at the top.
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * 
 */
public class StandardTitledLayout extends RelativeLayout {
	/** The Layout containing the title and text */
	private RelativeLayout mLayout;

	/** The rest of the layout, below the title */
	private RelativeLayout mFillerLayout;

	/** The title of the Layout */
	private TextView mTitleTextView;

	/** The TextView displayed at the center of the Layout */
	private TextView mMessageTextView;

	/**
	 * Constructor
	 * 
	 * @param context
	 *            the context of the calling Activity
	 * @param attrs
	 *            the attributes to set for this layout
	 */
	public StandardTitledLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}

	/**
	 * Constructor
	 * 
	 * @param context
	 *            the context of the calling Activity
	 */
	public StandardTitledLayout(Context context) {
		super(context);
		initialize(context);
	}

	/**
	 * Initialize the layout
	 * 
	 * @param context
	 *            the context of the calling Activity
	 */
	private void initialize(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		mLayout = (RelativeLayout) inflater.inflate(
				R.layout.sdk_standard_titled_layout, null);
		super.addView(mLayout);

		mTitleTextView = (TextView) findViewById(R.id.sdk_standard_titled_layout_title);
		mMessageTextView = (TextView) findViewById(R.id.sdk_standard_titled_layout_msg);
		mFillerLayout = (RelativeLayout) findViewById(R.id.sdk_standard_titled_layout_filler);
	}

	/**
	 * Displays a centered message.
	 * 
	 * @param text
	 *            the text to display
	 */
	public void setText(String text) {
		mMessageTextView.setText(text);
		mMessageTextView.setVisibility(View.VISIBLE);
	}

	/**
	 * Displays a title message.
	 * 
	 * @param text
	 *            the title to display
	 */
	public void setTitle(String text) {
		mTitleTextView.setText(text);
		mTitleTextView.setVisibility(View.VISIBLE);
	}

	/**
	 * Hides the centered message.
	 */
	public void hideText() {
		mMessageTextView.setVisibility(View.GONE);
	}

	/**
	 * Hides the title.
	 */
	public void hideTitle() {
		mTitleTextView.setVisibility(View.GONE);
	}

	@Override
	public void addView(View child) {
		mLayout.addView(child);
	}

	/**
	 * Add a view to the inner layout, below the title
	 * 
	 * @param child
	 *            the view to add to the inner layout
	 */
	public void addFillerView(View child) {
		mFillerLayout.addView(child);
	}

	/**
	 * Remove views from the inner layout, below the title
	 */
	public void removeFillerView() {
		mFillerLayout.removeAllViews();
	}
}
