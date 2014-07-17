package org.pocketcampus.platform.android.ui.list;

import java.util.List;

import org.pocketcampus.platform.android.ui.adapter.RatableAdapter;
import org.pocketcampus.platform.android.ui.element.Element;
import org.pocketcampus.platform.android.ui.element.ElementDimension;
import org.pocketcampus.platform.android.ui.labeler.IRatableViewLabeler;

import android.content.Context;
import android.widget.ListView;

/**
 * Labeled list that displays a list of Items using the "ratable" style. Labeled
 * means that it gets the text of its element from a <code>Labeler</code>.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public class RatableListViewElement extends ListView implements Element {

	/** The dimension of the element */
	private ElementDimension mDimension = ElementDimension.NORMAL;
	/** The adapter for the list */
	private RatableAdapter mAdapter;

	public RatableListViewElement(Context context) {
		super(context);
	}

	/**
	 * Shortcut constructor that creates the <code>LabeledArrayAdapter</code>
	 * itself.
	 * 
	 * @param context
	 *            The Application context
	 * @param items
	 *            The list of items to be displayed inthe list
	 * @param labeler
	 *            The Labeler to get the text of the items
	 */
	public RatableListViewElement(Context context,
			List<? extends Object> items,
			IRatableViewLabeler<? extends Object> labeler) {
		super(context);

		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		setLayoutParams(params);

		mAdapter = new RatableAdapter(context, items, labeler);
		mAdapter.setDimension(mDimension);
		setAdapter(mAdapter);
	}

	/**
	 * Sets the adapter dimension
	 * 
	 * @param dimension
	 */
	public void setDimension(ElementDimension dimension) {
		mDimension = dimension;
	}

	/**
	 * Sets the click listener for the line
	 * 
	 * @param lineClickListener
	 */
	public void setOnLineClickListener(OnItemClickListener lineClickListener) {
		mAdapter.setOnLineClickListener(lineClickListener);
	}

	/**
	 * Sets the click listener for the rating
	 * 
	 * @param ratingClickListener
	 */
	public void setOnRatingClickListener(OnItemClickListener ratingClickListener) {
		mAdapter.setOnRatingClickListener(ratingClickListener);
	}

	/** Notifies the ListView that the Data set has changed */
	public void notifyDataSetChanged() {
		mAdapter.notifyDataSetChanged();
	}
}
