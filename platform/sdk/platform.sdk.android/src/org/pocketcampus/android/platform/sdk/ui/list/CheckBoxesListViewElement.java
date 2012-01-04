package org.pocketcampus.android.platform.sdk.ui.list;

import java.util.List;

import org.pocketcampus.android.platform.sdk.ui.adapter.CheckBoxesArrayAdapter;
import org.pocketcampus.android.platform.sdk.ui.element.Element;

import android.content.Context;
import android.widget.ListView;

/**
 * <code>ListView</code> displaying a list of items along with two
 * <code>CheckBox</code>, allowing the user to check one or none of them, which
 * will express if he likes or dislike an item.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public class CheckBoxesListViewElement extends ListView implements Element {
	/** The adapter. */
	private CheckBoxesArrayAdapter mAdapter;

	/**
	 * Class constructor.
	 * 
	 * @param context
	 *            The application context.
	 * @param items
	 *            The list of items to be displayed.
	 */
	public CheckBoxesListViewElement(Context context,
			List<? extends Object> items) {
		super(context);

		// Layout parameters
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		setLayoutParams(params);

		// Creating the adapter
		mAdapter = new CheckBoxesArrayAdapter(context, items);
		setAdapter(mAdapter);
	}

	/**
	 * Sets the click listener for both <code>CheckBox</code>
	 * 
	 * @param clickListener
	 *            The click listener created in the application.
	 */
	public void setOnItemClickListener(OnItemClickListener clickListener) {
		mAdapter.setOnItemClickListener(clickListener);
	}

}
