package org.pocketcampus.android.platform.sdk.ui.list;

import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import org.pocketcampus.android.platform.sdk.ui.adapter.RatableExpandableListAdapter;
import org.pocketcampus.android.platform.sdk.ui.element.Element;
import org.pocketcampus.android.platform.sdk.ui.labeler.IRatableViewConstructor;
import org.pocketcampus.android.platform.sdk.ui.labeler.IRatableViewLabeler;

import android.content.Context;

/**
 * Separated list that displays a list with different sections.
 * 
 * @author Elodie
 * 
 */
public class RatableExpandableListViewElement extends ExpandableListViewElement implements
		Element {

	private RatableExpandableListAdapter mAdapter;
	
	public RatableExpandableListViewElement(Context context) {
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
	public RatableExpandableListViewElement(Context context,
			HashMap<String, ? extends Vector<? extends Object>> items,
			IRatableViewLabeler<? extends Object> viewLabeler,
			IRatableViewConstructor viewConstructor) {
		super(context, items, viewLabeler, viewConstructor);
		// Sort the list of restaurants
		SortedSet<String> sortedHeaders = new TreeSet<String>(items.keySet());

		Vector<String> headers = new Vector<String>(sortedHeaders);
		mAdapter = new RatableExpandableListAdapter(headers, items, viewConstructor,
				viewLabeler, context);
		// mAdapter.setDimension(mDimension);
		setAdapter(mAdapter);
	}

	public void setOnLineClickListener(OnItemClickListener l) {
		mAdapter.setOnLineClickListener(l);
	}
	
	public void setOnRatingClickListener(OnItemClickListener l) {
		mAdapter.setOnRatingClickListener(l);
	}
	
	@Override
	public RatableExpandableListAdapter getExpandableListAdapter() {
		return mAdapter;
	}

}
