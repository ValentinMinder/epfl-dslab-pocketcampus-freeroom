package org.pocketcampus.platform.android.ui.list;

import java.util.List;

import org.pocketcampus.platform.android.ui.adapter.LabeledArrayAdapter;
import org.pocketcampus.platform.android.ui.element.Element;
import org.pocketcampus.platform.android.ui.element.ElementDimension;
import org.pocketcampus.platform.android.ui.labeler.ILabeler;

import android.content.Context;
import android.widget.ListView;

/**
 * Labeled list that displays a list of Item using the default style.
 * Labeled means that it gets the text of its element from a <code>Labeler</code>.
 * @author Florian
 *
 */
public class LabeledListViewElement extends ListView implements Element {
	
	private ElementDimension mDimension = ElementDimension.NORMAL;

	public LabeledListViewElement(Context context) {
		super(context);
	}
	
	/**
	 * Shortcut constructor that creates the <code>LabeledArrayAdapter</code> itself.
	 * @param context
	 * @param items
	 * @param labeler
	 */
	public LabeledListViewElement(Context context, List<? extends Object> items, ILabeler<? extends Object> labeler) {
		super(context);
		
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		setLayoutParams(params);
		
		LabeledArrayAdapter adapter = new LabeledArrayAdapter(context, items, labeler);
		adapter.setDimension(mDimension);
		setAdapter(adapter);
	}

	public void setDimension(ElementDimension dimension) {
		mDimension = dimension;
	}
	
}
