package org.pocketcampus.android.platform.sdk.ui.list;

import java.util.List;

import org.pocketcampus.android.platform.sdk.ui.adapter.FeedWithImageAdapter;
import org.pocketcampus.android.platform.sdk.ui.element.Element;
import org.pocketcampus.android.platform.sdk.ui.element.ElementDimension;
import org.pocketcampus.android.platform.sdk.ui.labeler.IFeedViewLabeler;

import android.content.Context;
import android.widget.ListView;

/**
 * Labeled list that displays a list of Items using the feed style. Labeled
 * means that it gets the text of its element from a <code>Labeler</code>.
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * 
 */
public class FeedWithImageListViewElement extends ListView implements Element {

	/** The dimension of the element */
	private ElementDimension mDimension = ElementDimension.NORMAL;
	/** The adapter for the list */
	private FeedWithImageAdapter mAdapter;

	/**
	 * Constructor
	 * 
	 * @param context
	 *            The context of the calling application.
	 */
	public FeedWithImageListViewElement(Context context) {
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
	public FeedWithImageListViewElement(Context context,
			List<? extends Object> items,
			IFeedViewLabeler<? extends Object> labeler) {
		super(context);

		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		setLayoutParams(params);

		mAdapter = new FeedWithImageAdapter(context, items, labeler);
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
}
