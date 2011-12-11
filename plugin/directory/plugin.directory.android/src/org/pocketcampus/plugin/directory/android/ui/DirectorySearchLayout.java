package org.pocketcampus.plugin.directory.android.ui;


import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.ui.element.InputBarElement;
import org.pocketcampus.android.platform.sdk.ui.element.OnKeyPressedListener;
import org.pocketcampus.plugin.directory.android.DirectoryController;

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
	private InputBarElement mEditName;
	private RelativeLayout mInnerLayout;
	private DirectoryController mController;

	public DirectorySearchLayout(Context context, DirectoryController con, AttributeSet attrs) {
		super(context, attrs);
		initialize(context, con);
	}
	
	public DirectorySearchLayout(Context context, DirectoryController con) {
		super(context);
		initialize(context, con);
	}
	
	private void initialize(Context context, DirectoryController controller) {
		mController = controller;
		
		LayoutInflater inflater = LayoutInflater.from(context);
		mInnerLayout = (RelativeLayout) inflater.inflate(R.layout.directory_search_layout, null);
		
		LayoutParams full = new LayoutParams(-1,-1);
		addView(mInnerLayout, full);
		
		
		//mEditFName = (EditText)findViewById(R.id.directory_fname);
		mEditName = (InputBarElement)findViewById(R.id.directory_lname);
		
		mEditName.setOnKeyPressedListener(new OnKeyPressedListener() {
			@Override
			public void onKeyPressed(String text) {
				mController.getAutoCompleted(text);
				
			}
		});

		
	}
	
	public String getName(){
		return mEditName.getInputText();
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