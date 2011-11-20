package org.pocketcampus.plugin.directory.android.ui;


import org.pocketcampus.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/**
 * Standard <code>Layout</code> with a centered message.
 * 
 * @author Pascal
 *
 */
public class DirectorySearchLayout extends RelativeLayout {
	//private TextView mTextFName;
	//private TextView mTextLName;
	private EditText mEditName;
	private RelativeLayout mInnerLayout;

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
		mInnerLayout = (RelativeLayout) inflater.inflate(R.layout.directory_search_layout, null);
		
		LayoutParams full = new LayoutParams(-1,-1);
		addView(mInnerLayout, full);
		
		
		//mEditFName = (EditText)findViewById(R.id.directory_fname);
		mEditName = (EditText)findViewById(R.id.directory_lname);
		mEditName.setHint("");
		
	}
	
	public String getName(){
		return mEditName.getText().toString();
	}
	
	public void setOnEditorActionListener(OnEditorActionListener oeal){
		mEditName.setOnEditorActionListener(oeal);
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