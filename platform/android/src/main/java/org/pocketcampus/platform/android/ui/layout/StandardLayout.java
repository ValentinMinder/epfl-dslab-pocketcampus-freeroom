package org.pocketcampus.platform.android.ui.layout;

import org.pocketcampus.platform.android.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Standard <code>Layout</code> with a centered message.
 * 
 * @author Florian
 *
 */
public class StandardLayout extends RelativeLayout {
	private TextView mMessageTextView;
	private RelativeLayout mInnerLayout;

	public StandardLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}
	
	public StandardLayout(Context context) {
		super(context);
		initialize(context);
	}
	
	private void initialize(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		mInnerLayout = (RelativeLayout) inflater.inflate(R.layout.sdk_standard_layout, null);
		super.addView(mInnerLayout);
		
		mMessageTextView = (TextView) findViewById(R.id.standard_layout_msg);
	}

	/**
	 * Displays a centered message.
	 * @param text
	 */
	public void setText(CharSequence text) {
		mMessageTextView.setText(text);
		mMessageTextView.setVisibility(View.VISIBLE);
	}

	/**
	 * Hides the centered message.
	 */
	public void hideText() {
		mMessageTextView.setVisibility(View.GONE);
	}
	
	@Override
	public void addView(View child) {
		mInnerLayout.addView(child);
	}
}
























