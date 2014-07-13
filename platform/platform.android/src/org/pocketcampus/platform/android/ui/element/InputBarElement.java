package org.pocketcampus.platform.android.ui.element;

import org.pocketcampus.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView.OnEditorActionListener;

/**
 * Displays an input bar with a button on its right.
 * 
 * @author Florian <florian.laurent@epfl.ch>
 *
 */
public class InputBarElement extends RelativeLayout {
	private int mRessource = R.drawable.sdk_magnify_mini_icon;
	
	private EditTextElement mEditText;
	private ButtonElement mButton;
	private ImageButton mImageButton;
	private RelativeLayout mInnerLayout;
	
	/**
	 * @param context
	 * @param buttonText The text on the button.
	 */
	public InputBarElement(Context context) {
		super(context);
		initialize(context, null, null);
	}
	
	/**
	 * @param context
	 * @param buttonText The text on the button.
	 */
	public InputBarElement(Context context, String buttonText) {
		super(context);
		initialize(context, buttonText, null);
	}
	
	/**
	 * @param context
	 * @param buttonText The text on the button.
	 * @param hintText The hintText of the edit text.
	 */
	public InputBarElement(Context context, String buttonText, String hintText) {
		super(context);
		initialize(context, buttonText, hintText);
	}

	private void initialize(Context context, String buttonText, String hintText) {
		// EDIT TEXT
		LayoutParams editTextParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		editTextParams.setMargins(8, 8, 8, 8);
		
		
		mEditText = new EditTextElement(context, hintText);
		mEditText.setLayoutParams(editTextParams);
		mEditText.setSingleLine();
		mEditText.setId(1);
		
		
		super.addView(mEditText);
		
		// BUTTON
		LayoutParams buttonParams = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.FILL_PARENT);
		buttonParams.setMargins(15, 13, 14, 15);
		buttonParams.addRule(RelativeLayout.ALIGN_TOP, mEditText.getId());
		buttonParams.addRule(RelativeLayout.ALIGN_RIGHT, mEditText.getId());
		buttonParams.addRule(RelativeLayout.ALIGN_BOTTOM, mEditText.getId());
		
		LayoutParams imageButtonParams = new LayoutParams(buttonParams);
		imageButtonParams.width = 45;
		
		mButton = new ButtonElement(context);
		mImageButton = new ImageButton(context);
		
		setButtonText(buttonText);
		super.addView(mButton, buttonParams);
		super.addView(mImageButton, buttonParams);
		
		// LAYOUT
		LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		layoutParams.addRule(RelativeLayout.BELOW, mEditText.getId());
		
		mInnerLayout = new RelativeLayout(context);
		mInnerLayout.setLayoutParams(layoutParams);
		super.addView(mInnerLayout);
		
		// gives focus to the EditText and shows the keyboard
		mEditText.requestFocus();
	}
	
	public void setOnButtonClickListener(OnClickListener listener) {
		mButton.setOnClickListener(listener);
		mImageButton.setOnClickListener(listener);
	}
	
	public void setImeOptions(int opt){
		mEditText.setImeOptions(opt );
	}
	
	public void setOnEditorActionListener(OnEditorActionListener onEditorActionListener){
		mEditText.setOnEditorActionListener(onEditorActionListener);
	}
	
	

	public void setOnKeyPressedListener(final OnKeyPressedListener listener) {
		mEditText.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				listener.onKeyPressed(s.toString());
			}
		});
	}
	
	public String getInputText() {
		return mEditText.getText().toString();
	}
	
	public void setInputText(String text){
		mEditText.setText(text);
	}
	
	public void setCursorAtEnd(){
		mEditText.setSelection(mEditText.getText().length());
	}
	
	public void setButtonText(String text) {
		if(text==null ) {
			mButton.setVisibility(GONE);
			mImageButton.setVisibility(GONE);
			
		} else if(text.equals("")){
			mButton.setVisibility(GONE);
			Drawable img = getContext().getResources().getDrawable(mRessource);
			mImageButton.setVisibility(VISIBLE);
			mImageButton.setImageDrawable(img);
		}else{
			mButton.setText(text);
			mButton.setVisibility(VISIBLE);
			mImageButton.setVisibility(GONE);
		}
	}
	
	
	public void setImageButtonDrawableRessource(int ressource){
		this.mRessource = ressource;
	}
	
	public void setInputHint(String hint) {
		mEditText.setHint(hint);
	}

	@Override
	public void addView(View child) {
		mInnerLayout.addView(child);
	}
}























