package org.pocketcampus.android.platform.sdk.ui.layout;

import org.pocketcampus.android.platform.sdk.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Simple list <code>Layout</code> with items.
 * 
 * @author Elodie
 * 
 */
public class SimpleListLayout extends RelativeLayout {
	private TextView mMessageTextView;
	private RelativeLayout mInnerLayout;

	public SimpleListLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}

	public SimpleListLayout(Context context) {
		super(context);
		initialize(context);
	}

	private void initialize(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		mInnerLayout = (RelativeLayout) inflater.inflate(
				R.layout.sdk_simple_list_layout, null);
		addView(mInnerLayout);

		mMessageTextView = (TextView) findViewById(R.id.simple_list_empty_msg);
	}

	/**
	 * Displays a centered message.
	 * 
	 * @param text
	 */
	public void setText(String text) {
		mMessageTextView.setText(text);
		mMessageTextView.setVisibility(View.VISIBLE);
	}

	/**
	 * Hides the centered message.
	 */
	public void hideText() {
		mMessageTextView.setVisibility(View.GONE);
	}
}
