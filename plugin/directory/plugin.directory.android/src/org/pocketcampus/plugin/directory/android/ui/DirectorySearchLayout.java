package org.pocketcampus.plugin.directory.android.ui;

import org.pocketcampus.android.platform.sdk.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Standard <code>Layout</code> with a centered message.
 * 
 * @author Pascal
 *
 */
public class DirectorySearchLayout extends RelativeLayout {
	//private TextView mTextFName;
	//private TextView mTextLName;
	private EditText mEditFName;
	private EditText mEditLName;
	private LinearLayout mInnerLayout;

	public DirectorySearchLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}
	
	public DirectorySearchLayout(Context context) {
		super(context);
		initialize(context);
	}
	
	private void initialize(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		mInnerLayout = (LinearLayout) inflater.inflate(R.layout.directory_search_layout, null);
		
		addView(mInnerLayout);
		
		mEditFName = (EditText)findViewById(R.id.directory_fname);
		mEditLName = (EditText)findViewById(R.id.directory_lname);
	}
	
	public String getFirstName(){
		return mEditFName.getText().toString();
	}
	
	public String getLastName(){
		return mEditLName.getText().toString();
	}

//	/**
//	 * Displays a centered message.
//	 * @param text
//	 */
//	public void setText(String text) {
//		mMessageTextView.setText(text);
//		mMessageTextView.setVisibility(View.VISIBLE);
//	}
//
//	/**
//	 * Hides the centered message.
//	 */
//	public void hideText() {
//		mMessageTextView.setVisibility(View.GONE);
//	}
}