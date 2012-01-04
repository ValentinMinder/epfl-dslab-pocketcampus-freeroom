package org.pocketcampus.android.platform.sdk.ui.adapter;

import java.util.List;

import android.content.Context;

/**
 * The adapter for the CheckBoxesView.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 */
public class CheckBoxesArrayAdapter extends AbstractCheckBoxesArrayAdapter {

	/**
	 * Class constructor calling the super constructor.
	 * 
	 * @param context
	 *            The application context.
	 * @param items
	 *            The list of items to be displayed.
	 */
	public CheckBoxesArrayAdapter(Context context, List<? extends Object> items) {
		super(context, items);
	}

}
