package org.pocketcampus.android.platform.sdk.ui.element;

import android.content.Context;
import android.widget.Button;

public class ButtonElement extends Button implements Element {

	public ButtonElement(Context context) {
		super(context);
	}
	
	public ButtonElement(Context context, String label) {
		super(context);
		setText(label);
	}
	
	public ButtonElement(Context context, int labelResId) {
		super(context);
		setText(labelResId);
	}
	
}
