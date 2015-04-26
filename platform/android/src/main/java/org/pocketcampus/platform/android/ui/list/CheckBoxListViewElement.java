package org.pocketcampus.platform.android.ui.list;

import java.util.List;

import org.pocketcampus.platform.android.ui.adapter.CheckBoxArrayAdapter;
import org.pocketcampus.platform.android.ui.element.Element;
import org.pocketcampus.platform.android.ui.labeler.ILabeler;

import android.content.Context;
import android.widget.ListView;

/**
 * <code>ListView</code> displaying a list of items along with a
 * <code>CheckBox</code>, allowing the user to check one or none of them, which
 * will express if he likes or dislike an item.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 */
public class CheckBoxListViewElement extends ListView implements Element {
	private CheckBoxArrayAdapter mAdapter;

	/**
	 * Class constructor.
	 * 
	 * @param context
	 *            The application context.
	 * @param items
	 *            The list of items to be displayed.
	 * @param labeler
	 *            The labeler to get the objects attributes.
	 */
	public CheckBoxListViewElement(Context context,
			List<? extends Object> items, ILabeler<? extends Object> labeler) {
		super(context);
		mAdapter = new CheckBoxArrayAdapter(context, items, labeler);
		setAdapter(mAdapter);
	}

	/**
	 * Sets the click listener for the <code>CheckBox</code>.
	 * 
	 * @param clickListener
	 *            The click listener created in the application.
	 */
	public void setOnCheckBoxClickListener(OnItemClickListener clickListener) {
		mAdapter.setOnCheckBoxClickListener(clickListener);
	}

}
