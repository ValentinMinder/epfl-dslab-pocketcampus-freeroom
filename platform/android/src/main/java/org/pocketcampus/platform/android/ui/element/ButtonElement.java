package org.pocketcampus.platform.android.ui.element;

import android.content.Context;
import android.widget.Button;

/**
 * @author Florian <florian.laurent@epfl.ch>
 *
 */
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
