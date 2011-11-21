package org.pocketcampus.android.platform.sdk.ui.list;

import java.util.List;

import org.pocketcampus.android.platform.sdk.ui.adapter.RichLabeledArrayAdapter;
import org.pocketcampus.android.platform.sdk.ui.element.Element;
import org.pocketcampus.android.platform.sdk.ui.element.ElementDimension;
import org.pocketcampus.android.platform.sdk.ui.labeler.IRichLabeler;

import android.content.Context;
import android.widget.ListView;

/**
 * Labeled list that displays a list of Item using the default style.
 * RichLabeled means that it gets the text of its element from a
 * <code>RichLabeler</code>.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public class RichLabeledListViewElement extends ListView implements Element {

	private ElementDimension mDimension = ElementDimension.NORMAL;

	public RichLabeledListViewElement(Context context) {
		super(context);
	}

	/**
	 * Shortcut constructor that creates the <code>RichLabeledArrayAdapter</code>
	 * itself.
	 * 
	 * @param context
	 * @param items
	 * @param labeler
	 */
	public RichLabeledListViewElement(Context context,
			List<? extends Object> items, IRichLabeler<? extends Object> labeler) {
		super(context);

		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		setLayoutParams(params);

		RichLabeledArrayAdapter adapter = new RichLabeledArrayAdapter(context, items,
				labeler);
		adapter.setDimension(mDimension);
		setAdapter(adapter);
	}

	public void setDimension(ElementDimension dimension) {
		mDimension = dimension;
	}

}
