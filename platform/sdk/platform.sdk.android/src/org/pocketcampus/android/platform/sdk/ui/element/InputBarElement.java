package org.pocketcampus.android.platform.sdk.ui.element;

import android.content.Context;
import android.widget.RelativeLayout;

/**
 * Displays an input bar with a button on its right.
 * @author Florian
 *
 */
public class InputBarElement extends RelativeLayout {

	EditTextElement mEditText;
	ButtonElement mButton;
	
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
		LayoutParams editTextParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		setLayoutParams(editTextParams);
		
		mEditText = new EditTextElement(context, hintText);
		mEditText.setLayoutParams(editTextParams);
		mEditText.setSingleLine();
		addView(mEditText);
		
		LayoutParams buttonParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		buttonParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		
		mButton = new ButtonElement(context, buttonText);
		mButton.setLayoutParams(buttonParams);
		addView(mButton);
	}
	
	public void setOnButtonClickListener(OnClickListener listener) {
		mButton.setOnClickListener(listener);
	}

	public String getText() {
		return mEditText.getText().toString();
	}
}
