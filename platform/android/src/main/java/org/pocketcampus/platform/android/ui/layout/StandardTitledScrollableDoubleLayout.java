package org.pocketcampus.platform.android.ui.layout;

import org.pocketcampus.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * A <code>RelativeLayout</code> with a centered message and a title at the top.
 * You can add two inner layouts in the outer <code>RelativeLayout</code>. The
 * whole layout is scrollable.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 */
public class StandardTitledScrollableDoubleLayout extends RelativeLayout {
	/** The main layout containing the title and text. */
	private RelativeLayout mLayout;
	/** The first inner layout, below the title. */
	private RelativeLayout mFillerLayoutOne;
	/** The second inner layout, below the first one. */
	private RelativeLayout mFillerLayoutTwo;
	/** The layout's title. */
	private TextView mTitleTextView;
	/** The <code>TextView</code> displayed at the center of the layout. */
	private TextView mMessageTextView;

	/**
	 * Class constructor initializing the context and the attributes.
	 * 
	 * @param context
	 *            The application context.
	 * @param attrs
	 *            The attributes to set for this layout.
	 */
	public StandardTitledScrollableDoubleLayout(Context context,
			AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}

	/**
	 * Class constructor initializing the context.
	 * 
	 * @param context
	 *            The application context.
	 */
	public StandardTitledScrollableDoubleLayout(Context context) {
		super(context);
		initialize(context);
	}

	/**
	 * Initialize the layout.
	 * 
	 * @param context
	 *            The application context.
	 */
	private void initialize(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		mLayout = (RelativeLayout) inflater.inflate(
				R.layout.sdk_standard_titled_scrollable_double_layout, null);
		super.addView(mLayout);

		mTitleTextView = (TextView) findViewById(R.id.sdk_standard_scrollable_titled_double_layout_title);
		mMessageTextView = (TextView) findViewById(R.id.sdk_standard_scrollable_titled_double_layout_msg);
		mFillerLayoutOne = (RelativeLayout) findViewById(R.id.sdk_standard_scrollable_titled_double_layout_filler1);
		mFillerLayoutTwo = (RelativeLayout) findViewById(R.id.sdk_standard_scrollable_titled_double_layout_filler2);
	}

	/**
	 * Displays a centered message.
	 * 
	 * @param text
	 *            The text to display.
	 */
	public void setText(String text) {
		mMessageTextView.setText(text);
		mMessageTextView.setVisibility(View.VISIBLE);
	}

	/**
	 * Displays a message in the title.
	 * 
	 * @param text
	 *            The text of the title to display.
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

	/**
	 * Adds a view to the layout.
	 * 
	 * @param child
	 *            The view to add to the layout.
	 */
	@Override
	public void addView(View child) {
		mLayout.addView(child);
	}

	/**
	 * Adds a view to the first inner layout, below the title.
	 * 
	 * @param child
	 *            The view to add to the first inner layout.
	 */
	public void addFirstLayoutFillerView(View child) {
		mFillerLayoutOne.addView(child);
	}

	/**
	 * Removes all views from the first inner layout, below the title.
	 */
	public void removeFirstLayoutFillerView() {
		mFillerLayoutOne.removeAllViews();
	}

	/**
	 * Adds a view to the second inner layout, below the first inner layout.
	 * 
	 * @param child
	 *            The view to add to the second inner layout.
	 */
	public void addSecondLayoutFillerView(View child) {
		mFillerLayoutTwo.addView(child);
	}

	/**
	 * Removes all views from the second inner layout, below the first inner
	 * layout.
	 */
	public void removeSecondLayoutFillerView() {
		mFillerLayoutTwo.removeAllViews();
	}
}
