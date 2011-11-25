package org.pocketcampus.android.platform.sdk.ui.layout;

import org.pocketcampus.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Standard Titled <code>Layout</code> with a centered message and a title at the top.
 * You can add two inner <code>Layout</code> in the outter <code>RelativeLayout</code>
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public class StandardTitledDoubleLayout extends RelativeLayout {
	/** The Layout containing the title and text */
	private RelativeLayout mLayout;

	/** The first inner layout, below the title */
	private RelativeLayout mFillerLayoutOne;
	
	/** The second inner layout, below the first one */
	private RelativeLayout mFillerLayoutTwo;

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
	public StandardTitledDoubleLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}

	/**
	 * Constructor
	 * 
	 * @param context
	 *            the context of the calling Activity
	 */
	public StandardTitledDoubleLayout(Context context) {
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
				R.layout.sdk_standard_titled_double_layout, null);
		super.addView(mLayout);

		mTitleTextView = (TextView) findViewById(R.id.sdk_standard_titled_double_layout_title);
		mMessageTextView = (TextView) findViewById(R.id.sdk_standard_titled_double_layout_msg);
		mFillerLayoutOne = (RelativeLayout) findViewById(R.id.sdk_standard_titled_double_layout_filler1);
		mFillerLayoutTwo = (RelativeLayout) findViewById(R.id.sdk_standard_titled_double_layout_filler2);
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
	 * Add a view to the first inner layout, below the title
	 * 
	 * @param child
	 *            the view to add to the first inner layout
	 */
	public void addFirstLayoutFillerView(View child) {
		mFillerLayoutOne.addView(child);
	}

	/**
	 * Remove views from the first inner layout, below the title
	 */
	public void removeFirstLayoutFillerView() {
		mFillerLayoutOne.removeAllViews();
	}
	
	/**
	 * Add a view to the second inner layout, below the first inner layout
	 * 
	 * @param child
	 *            the view to add to the second inner layout
	 */
	public void addSecondLayoutFillerView(View child) {
		mFillerLayoutTwo.addView(child);
	}

	/**
	 * Remove views from the second inner layout, below the first inner layout
	 */
	public void removeSecondLayoutFillerView() {
		mFillerLayoutTwo.removeAllViews();
	}
}
