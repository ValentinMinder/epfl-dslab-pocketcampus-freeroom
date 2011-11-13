package org.pocketcampus.android.platform.sdk.ui.element;

import java.util.List;

import org.pocketcampus.android.platform.sdk.ui.adapter.RatableAdapter;
import org.pocketcampus.android.platform.sdk.ui.labeler.IRatableViewLabeler;

import android.content.Context;
import android.widget.ListView;

/**
 * Labeled list that displays a list of Item using the ratable style.
 * Labeled means that it gets the text of its element from a <code>Labeler</code>.
 * @author Oriane
 *
 */
public class RatableListViewElement extends ListView implements Element {
	
	private ElementDimension mDimension = ElementDimension.NORMAL;
	private RatableAdapter mAdapter;

	public RatableListViewElement(Context context) {
		super(context);
	}
	
	/**
	 * Shortcut constructor that creates the <code>LabeledArrayAdapter</code> itself.
	 * @param context
	 * @param items
	 * @param labeler
	 */
	public RatableListViewElement(Context context, List<? extends Object> items, IRatableViewLabeler<? extends Object> labeler) {
		super(context);
		
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		setLayoutParams(params);
		
		mAdapter = new RatableAdapter(context, items, labeler);
		mAdapter.setDimension(mDimension);
		setAdapter(mAdapter);
	}

	public void setDimension(ElementDimension dimension) {
		mDimension = dimension;
	}
	
	public void setOnLineClickListener(OnItemClickListener l) {
		mAdapter.setOnLineClickListener(l);
	}
	
	public void setOnRatingClickListener(OnItemClickListener l) {
		mAdapter.setOnRatingClickListener(l);
	}
	
}
