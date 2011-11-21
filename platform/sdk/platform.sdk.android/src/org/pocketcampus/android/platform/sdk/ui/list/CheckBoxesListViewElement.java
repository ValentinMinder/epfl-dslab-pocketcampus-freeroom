package org.pocketcampus.android.platform.sdk.ui.list;

import java.util.List;

import org.pocketcampus.android.platform.sdk.ui.adapter.CheckBoxesArrayAdapter;
import org.pocketcampus.android.platform.sdk.ui.element.Element;

import android.content.Context;
import android.widget.ListView;

/**
 * ListView that displays a list of Item along with two CheckBoxes each, that
 * allows the user to check one or none of them, which will express if he likes
 * or dislike an item.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public class CheckBoxesListViewElement extends ListView implements Element {
	private CheckBoxesArrayAdapter mAdapter;

	public CheckBoxesListViewElement(Context context,
			List<? extends Object> items) {
		super(context);

		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		setLayoutParams(params);

		mAdapter = new CheckBoxesArrayAdapter(context, items);
		setAdapter(mAdapter);
	}

	/**
	 * Sets the click listener for the CheckBoxes
	 * 
	 * @param clickListener
	 *            The click listener created in the application
	 */
	public void setOnItemClickListener(OnItemClickListener clickListener) {
		mAdapter.setOnItemClickListener(clickListener);
	}

}
