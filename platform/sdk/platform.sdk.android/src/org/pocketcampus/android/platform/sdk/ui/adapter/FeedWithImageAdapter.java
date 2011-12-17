package org.pocketcampus.android.platform.sdk.ui.adapter;

import java.util.List;

import org.pocketcampus.android.platform.sdk.ui.element.FeedWithImageView;
import org.pocketcampus.android.platform.sdk.ui.labeler.IFeedViewLabeler;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;

/**
 * An ArrayAdapter used to display a list of Feed elements
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * 
 */
public class FeedWithImageAdapter extends AbstractArrayAdapter {
	/** The labeler that defines the information to display from the object */
	private IFeedViewLabeler mLabeler;

	/** The context of the calling View */
	private Context mContext;

	/** The listener used for callback when a line in the adapter is pressed */
	private OnItemClickListener mOnLineClickListener;

	/**
	 * Constructor for a FeedAdapter
	 * 
	 * @param context
	 *            the context of the calling View
	 * @param items
	 *            the list of items to display
	 * @param labeler
	 *            the labeler to get information from the object
	 */
	public FeedWithImageAdapter(Context context, List<? extends Object> items,
			IFeedViewLabeler<? extends Object> labeler) {
		super(context, items);
		if (items != null && labeler != null) {
			mContext = context;
			mLabeler = labeler;
		}
	}

	/**
	 * Constructs the view for each line of the Adapter
	 * 
	 * @param position
	 *            the position for which the view is needed
	 * @param convertView
	 *            the view that was previously represented
	 * @param parent
	 *            the parent of this View
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		FeedWithImageView rv = new FeedWithImageView(getItem(position), mContext, mLabeler,
				mOnLineClickListener, position);
		return rv;
	}

	/**
	 * Sets the listener for line clicks
	 * 
	 * @param l
	 *            the listener for the lines
	 */
	public void setOnLineClickListener(OnItemClickListener l) {
		mOnLineClickListener = l;
	}

}
