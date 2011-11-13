package org.pocketcampus.android.platform.sdk.ui.element;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.pocketcampus.android.platform.sdk.ui.adapter.ExpandableListAdapter;
import org.pocketcampus.android.platform.sdk.ui.labeler.IRatableViewConstructor;
import org.pocketcampus.android.platform.sdk.ui.labeler.IRatableViewLabeler;

import android.content.Context;
import android.widget.ExpandableListView;

/**
 * Separated list that displays a list with different sections.
 * 
 * @author Elodie
 * 
 */
public class SeparatedListViewElement extends ExpandableListView implements
		Element {

	private ElementDimension mDimension = ElementDimension.NORMAL;
	private ExpandableListAdapter mAdapter;

	public SeparatedListViewElement(Context context) {
		super(context);
	}

	/**
	 * Shortcut constructor that creates the <code>LabeledArrayAdapter</code>
	 * itself.
	 * 
	 * @param context
	 * @param items
	 * @param labeler
	 */
	public SeparatedListViewElement(Context context,
			HashMap<String, ? extends Vector<? extends Object>> items,
			IRatableViewLabeler<? extends Object> viewLabeler,
			IRatableViewConstructor viewConstructor) {
		super(context);

		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		setLayoutParams(params);

		Vector<String> headers = new Vector<String>(items.keySet());
		mAdapter = new ExpandableListAdapter(headers, items, viewConstructor,
				viewLabeler, context);
		// mAdapter.setDimension(mDimension);
		setAdapter(mAdapter);
	}

	public void setDimension(ElementDimension dimension) {
		mDimension = dimension;
	}

	public void setOnLineClickListener(OnItemClickListener l) {
		// mAdapter.setOnLineClickListener(l);
	}

	public void setOnRatingClickListener(OnItemClickListener l) {
		// mAdapter.setOnRatingClickListener(l);
	}
}
