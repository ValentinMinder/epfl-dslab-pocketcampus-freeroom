package org.pocketcampus.android.platform.sdk.ui.layout;

import org.pocketcampus.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Standard <code>Layout</code> with a centered message and a title at the top.
 * 
 * @author Elodie
 *
 */
public class StandardTitledLayout extends RelativeLayout {
	private TextView mTitleTextView;
	private TextView mMessageTextView;
	private RelativeLayout mInnerLayout;
	private RelativeLayout mFillerLayout;

	public StandardTitledLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}
	
	public StandardTitledLayout(Context context) {
		super(context);
		initialize(context);
	}
	
	private void initialize(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		mInnerLayout = (RelativeLayout) inflater.inflate(R.layout.sdk_standard_titled_layout, null);
		super.addView(mInnerLayout);
		
		mTitleTextView = (TextView) findViewById(R.id.standard_titled_layout_title);
		mTitleTextView.setId(8756789);
		mMessageTextView = (TextView) findViewById(R.id.standard_titled_layout_msg);
		mFillerLayout = (RelativeLayout) findViewById(R.id.standard_titled_layout_filler);
	}

	/**
	 * Displays a centered message.
	 * @param text
	 */
	public void setText(String text) {
		mMessageTextView.setText(text);
		mMessageTextView.setVisibility(View.VISIBLE);
	}
	
	/**
	 * Displays a title message.
	 * @param text
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
	 * Hides the centered message.
	 */
	public void hideTitle() {
		mTitleTextView.setVisibility(View.GONE);
	}
	
	@Override
	public void addView(View child) {
		mInnerLayout.addView(child);
	}
	
	/**
	 * Add a view to the inner layout, below the title
	 * @param child
	 */
	public void addFillerView(View child){
		mFillerLayout.addView(child);
	}
	
	/**
	 * Remove views from the inner layout, below the title
	 * @return
	 */
	public void removeFillerView(){
		mFillerLayout.removeAllViews();
	}	
	public int getMsgId(){
		return mTitleTextView.getId();
	}
}
