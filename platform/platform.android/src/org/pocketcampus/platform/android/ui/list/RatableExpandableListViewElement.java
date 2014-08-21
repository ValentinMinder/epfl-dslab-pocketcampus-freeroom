package org.pocketcampus.platform.android.ui.list;

import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import org.pocketcampus.platform.android.ui.adapter.RatableExpandableListAdapter;
import org.pocketcampus.platform.android.ui.element.Element;
import org.pocketcampus.platform.android.ui.labeler.IRatableViewConstructor;
import org.pocketcampus.platform.android.ui.labeler.IRatableViewLabeler;

import android.content.Context;

/**
 * Separated list that displays a list with different sections.
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * 
 */
public class RatableExpandableListViewElement extends ExpandableListViewElement
		implements Element {

	/** The adapter for the ListView */
	private RatableExpandableListAdapter mAdapter;

	/**
	 * Constructor
	 * 
	 * @param context
	 *            the context of the calling Activity
	 */
	public RatableExpandableListViewElement(Context context) {
		super(context);
	}

	/**
	 * Shortcut constructor that creates the <code>LabeledArrayAdapter</code>
	 * itself.
	 * 
	 * @param context
	 *            The Application Context
	 * @param items
	 *            The items to be displayed in the List
	 * @param labeler
	 *            The labeler to get the text of the items
	 */
	public RatableExpandableListViewElement(Context context,
			HashMap<String, ? extends Vector<? extends Object>> items,
			IRatableViewLabeler<? extends Object> viewLabeler,
			IRatableViewConstructor viewConstructor) {
		super(context, items, viewLabeler, viewConstructor);

		/** Sorts the list of restaurants */
		SortedSet<String> sortedHeaders = new TreeSet<String>(items.keySet());

		Vector<String> headers = new Vector<String>(sortedHeaders);
		mAdapter = new RatableExpandableListAdapter(headers, items,
				viewConstructor, viewLabeler, context);
		setAdapter(mAdapter);
	}

	/**
	 * Sets the click listener on the line
	 * 
	 * @param lineClickListener
	 */
	public void setOnLineClickListener(OnItemClickListener lineClickListener) {
		mAdapter.setOnLineClickListener(lineClickListener);
	}

	/**
	 * Sets the click listener on the rating
	 * 
	 * @param ratingListener
	 */
	public void setOnRatingClickListener(OnItemClickListener ratingListener) {
		mAdapter.setOnRatingClickListener(ratingListener);
	}

	/** To get the Adapter from the Application */
	@Override
	public RatableExpandableListAdapter getExpandableListAdapter() {
		return mAdapter;
	}

	/** Notifies the ListView that the Data set has changed */
	public void notifyDataSetChanged() {
		mAdapter.notifyDataSetChanged();
	}

}
