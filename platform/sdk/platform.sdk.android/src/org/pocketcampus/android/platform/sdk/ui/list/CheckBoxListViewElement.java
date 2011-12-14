package org.pocketcampus.android.platform.sdk.ui.list;

import java.util.List;

import org.pocketcampus.android.platform.sdk.ui.adapter.CheckBoxArrayAdapter;
import org.pocketcampus.android.platform.sdk.ui.element.Element;
import org.pocketcampus.android.platform.sdk.ui.labeler.ILabeler;

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
public class CheckBoxListViewElement extends ListView implements Element {
	private CheckBoxArrayAdapter mAdapter;

	public CheckBoxListViewElement(Context context,
			List<? extends Object> items, ILabeler labeler) {
		super(context);
		mAdapter = new CheckBoxArrayAdapter(context, items, labeler);
		setAdapter(mAdapter);
	}

	/**
	 * Sets the click listener for the positive CheckBox
	 * 
	 * @param clickListener
	 *            The click listener created in the application
	 */
	public void setOnCheckBoxClickListener(OnItemClickListener clickListener) {
		mAdapter.setOnCheckBoxClickListener(clickListener);
	}

}
