package org.pocketcampus.platform.android.ui.adapter;

import java.util.HashMap;
import java.util.Vector;

import org.pocketcampus.platform.android.ui.labeler.IRatableViewConstructor;
import org.pocketcampus.platform.android.ui.labeler.IRatableViewLabeler;

import android.content.Context;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 
 * An Ex
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * 
 */
public class RatableExpandableListAdapter extends ExpandableListAdapter {
	private OnItemClickListener mOnLineClickListener;
	private OnItemClickListener mOnRatingClickListener;

	/**
	 * Constructor
	 * 
	 * @param context
	 *            context of the application the list view is in
	 * @param headers
	 *            restaurant full menu represented in the list section.
	 */
	public RatableExpandableListAdapter(Vector<String> headers,
			HashMap<String, ? extends Vector<? extends Object>> items,
			IRatableViewConstructor viewConstructor,
			IRatableViewLabeler<? extends Object> viewLabeler, Context context) {
		super(headers, items, viewConstructor, viewLabeler, context);
	}

	public void setOnLineClickListener(OnItemClickListener l) {
		mOnLineClickListener = l;
	}

	public void setOnRatingClickListener(OnItemClickListener l) {
		mOnRatingClickListener = l;
	}
}
