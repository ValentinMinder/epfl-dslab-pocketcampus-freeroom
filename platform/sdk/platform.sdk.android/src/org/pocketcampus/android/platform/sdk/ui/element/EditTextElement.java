package org.pocketcampus.android.platform.sdk.ui.element;

import android.content.Context;
import android.widget.EditText;

public class EditTextElement extends EditText {

	public EditTextElement(Context context, String hintText) {
		super(context);
		setHint(hintText);
	}

}
