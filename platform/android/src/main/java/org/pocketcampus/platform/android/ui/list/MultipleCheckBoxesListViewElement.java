package org.pocketcampus.platform.android.ui.list;

import java.util.List;

import org.pocketcampus.platform.android.ui.adapter.MultipleCheckBoxesArrayAdapter;
import org.pocketcampus.platform.android.ui.element.Element;
import org.pocketcampus.platform.android.ui.labeler.ILabeler;

import android.content.Context;
import android.widget.ListView;

/**
 * <code>ListView</code> displaying a list of items along with two
 * <code>CheckBox</code>, allowing the user to check one or none of them, which
 * will express if he likes or dislike an item.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 */
public class MultipleCheckBoxesListViewElement extends ListView implements
		Element {
	/** The adapter for the items in the list. */
	private MultipleCheckBoxesArrayAdapter mAdapter;

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
	public MultipleCheckBoxesListViewElement(Context context,
			List<? extends Object> items, ILabeler<? extends Object> labeler) {
		super(context);
		mAdapter = new MultipleCheckBoxesArrayAdapter(context, items, labeler);
		setAdapter(mAdapter);
	}

	/**
	 * Sets the click listener for both <code>CheckBox</code>.
	 * 
	 * @param clickListener
	 *            The click listener created in the application.
	 */
	public void setOnCheckBoxClickListener(OnItemClickListener clickListener) {
		mAdapter.setOnCheckBoxClickListener(clickListener);
	}

}
