package org.pocketcampus.platform.android.ui.layout;

import org.pocketcampus.platform.android.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * A <code>RelativeLayout</code> with a centered message and a title at the top.
 * You can add two inner layouts in the outer <code>RelativeLayout</code>.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 */
public class StandardTitledDoubleSeparatedLayout extends RelativeLayout {
	/** The main layout containing both titles and the centered text. */
	private RelativeLayout mLayout;
	/** The first inner layout, below the title. */
	private RelativeLayout mFillerLayout1;
	/** The second inner layout, below the first one. */
	private RelativeLayout mFillerLayout2;
	/** The first title of the Layout. */
	private TextView mTitle1TextView;
	/** The second title of the Layout. */
	private TextView mTitle2TextView;
	/** The <code>TextView</code> displayed at the center of the main layout. */
	private TextView mMessageTextView;

	/**
	 * Class constructor initializing the context and the attributes.
	 * 
	 * @param context
	 *            The application context.
	 * @param attrs
	 *            The attributes to set for this layout.
	 */
	public StandardTitledDoubleSeparatedLayout(Context context,
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
	public StandardTitledDoubleSeparatedLayout(Context context) {
		super(context);
		initialize(context);
	}

	/**
	 * Initializes the layout.
	 * 
	 * @param context
	 *            The application context.
	 */
	private void initialize(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		mLayout = (RelativeLayout) inflater.inflate(
				R.layout.sdk_standard_titled_double_separated_layout, null);
		super.addView(mLayout);

		mTitle1TextView = (TextView) findViewById(R.id.sdk_standard_titled_double_separated_layout_title1);
		mTitle2TextView = (TextView) findViewById(R.id.sdk_standard_titled_double_separated_layout_title2);
		mMessageTextView = (TextView) findViewById(R.id.sdk_standard_titled_double_separated_layout_msg);
		mFillerLayout1 = (RelativeLayout) findViewById(R.id.sdk_standard_titled_double_separated_layout_filler1);
		mFillerLayout2 = (RelativeLayout) findViewById(R.id.sdk_standard_titled_double_separated_layout_filler2);
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
	 * Displays a message in the first title.
	 * 
	 * @param text
	 *            The text of the title to display
	 */
	public void setFirstTitle(String text) {
		mTitle1TextView.setText(text);
		mTitle1TextView.setVisibility(View.VISIBLE);
	}

	/**
	 * Displays a message in the second title.
	 * 
	 * @param text
	 *            The text of the title to display
	 */
	public void setSecondTitle(String text) {
		mTitle2TextView.setText(text);
		mTitle2TextView.setVisibility(View.VISIBLE);
	}

	/**
	 * Hides the centered message.
	 */
	public void hideText() {
		mMessageTextView.setVisibility(View.GONE);
	}

	/**
	 * Hides the first title.
	 */
	public void hideFirstTitle() {
		mTitle1TextView.setVisibility(View.GONE);
	}

	/**
	 * Hides the second title.
	 */
	public void hideSecondTitle() {
		mTitle2TextView.setVisibility(View.GONE);
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
		mFillerLayout1.addView(child);
	}

	/**
	 * Removes all views from the first inner layout, below the title.
	 */
	public void removeFirstLayoutFillerView() {
		mFillerLayout1.removeAllViews();
	}

	/**
	 * Adds a view to the second inner layout, below the first inner layout.
	 * 
	 * @param child
	 *            The view to add to the second inner layout.
	 */
	public void addSecondLayoutFillerView(View child) {
		mFillerLayout2.addView(child);
	}

	/**
	 * Removes all views from the second inner layout, below the first inner
	 * layout.
	 */
	public void removeSecondLayoutFillerView() {
		mFillerLayout2.removeAllViews();
	}
}
