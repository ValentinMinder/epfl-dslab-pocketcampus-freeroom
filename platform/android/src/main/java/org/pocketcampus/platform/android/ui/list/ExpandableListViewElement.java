package org.pocketcampus.platform.android.ui.list;

import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import org.pocketcampus.platform.android.ui.adapter.ExpandableListAdapter;
import org.pocketcampus.platform.android.ui.element.Element;
import org.pocketcampus.platform.android.ui.labeler.IRatableViewConstructor;
import org.pocketcampus.platform.android.ui.labeler.IRatableViewLabeler;

import android.content.Context;
import android.widget.ExpandableListView;

/**
 * Separated list that displays a list with different sections.
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * 
 */
public class ExpandableListViewElement extends ExpandableListView implements
		Element {

	/** The Adapter for the ListView */
	private ExpandableListAdapter mAdapter;

	/**
	 * Constructor
	 * 
	 * @param context
	 *            the context of the calling Activity
	 */
	public ExpandableListViewElement(Context context) {
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
	public ExpandableListViewElement(Context context,
			HashMap<String, ? extends Vector<? extends Object>> items,
			IRatableViewLabeler<? extends Object> viewLabeler,
			IRatableViewConstructor viewConstructor) {
		super(context);

		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		setLayoutParams(params);

		// Sort the list of restaurants
		SortedSet<String> sortedHeaders = new TreeSet<String>(items.keySet());

		Vector<String> headers = new Vector<String>(sortedHeaders);
		mAdapter = new ExpandableListAdapter(headers, items, viewConstructor,
				viewLabeler, context);

		setAdapter(mAdapter);
	}
}