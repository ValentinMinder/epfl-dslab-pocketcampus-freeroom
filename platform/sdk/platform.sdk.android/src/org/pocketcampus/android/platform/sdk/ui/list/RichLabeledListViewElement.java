package org.pocketcampus.android.platform.sdk.ui.list;

import java.util.List;

import org.pocketcampus.android.platform.sdk.ui.adapter.RichLabeledArrayAdapter;
import org.pocketcampus.android.platform.sdk.ui.element.Element;
import org.pocketcampus.android.platform.sdk.ui.element.ElementDimension;
import org.pocketcampus.android.platform.sdk.ui.labeler.IRichLabeler;

import android.content.Context;
import android.widget.ListView;

/**
 * <code>ListView</code> displaying a list of items using the default style. It
 * is called <code>RichLabeledListView</code> because it gets its objects
 * attributes from a <code>RichLabeler</code>.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 */
public class RichLabeledListViewElement extends ListView implements Element {
	/** The dimensions of the list. */
	private ElementDimension mDimension = ElementDimension.NORMAL;

	/**
	 * Class constructor.
	 * 
	 * @param context
	 *            The application context.
	 */
	public RichLabeledListViewElement(Context context) {
		super(context);
	}

	/**
	 * Class constructor creating the <code>RichLabeledArrayAdapter</code>
	 * itself.
	 * 
	 * @param context
	 *            The application context.
	 * @param items
	 *            The list of items to be displayed.
	 * @param labeler
	 *            The labeler to get the objects attributes.
	 */
	public RichLabeledListViewElement(Context context,
			List<? extends Object> items, IRichLabeler<? extends Object> labeler) {
		super(context);

		// Layout parameters
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		setLayoutParams(params);

		// Creating the adapter
		RichLabeledArrayAdapter adapter = new RichLabeledArrayAdapter(context,
				items, labeler);
		adapter.setDimension(mDimension);
		setAdapter(adapter);
	}

}
