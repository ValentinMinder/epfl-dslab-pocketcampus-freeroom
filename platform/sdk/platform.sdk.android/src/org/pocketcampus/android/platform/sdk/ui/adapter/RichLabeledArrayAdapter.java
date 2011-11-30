package org.pocketcampus.android.platform.sdk.ui.adapter;

import java.util.List;

import org.pocketcampus.android.platform.sdk.ui.element.RichView;
import org.pocketcampus.android.platform.sdk.ui.labeler.IRichLabeler;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;

/**
 * An Adapter to handle the display of rich labeled Objects
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 */
public class RichLabeledArrayAdapter extends AbstractArrayAdapter {
	/** The RichLabeler to get the object's attributes */
	private IRichLabeler mLabeler;
	/** The application context */
	private Context mContext;
	/** The listener on the object line */
	private OnItemClickListener mOnClickListener;

	/**
	 * The constructor
	 * 
	 * @param context
	 *            The application context
	 * @param items
	 *            The items to display
	 * @param labeler
	 *            The RichLabeler to get the object's attributes
	 */
	public RichLabeledArrayAdapter(Context context,
			List<? extends Object> items, IRichLabeler<? extends Object> labeler) {
		super(context, items);

		if (context == null) {
			new IllegalArgumentException("Context cannot be null!");
		}

		if (mLabeler == null) {
			new IllegalArgumentException("Labeler cannot be null!");
		}

		mContext = context;
		mLabeler = labeler;
	}

	/**
	 * Overrides the getView method and creates an RichView for each labeled
	 * object
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = new RichView(getItem(position), mContext, mLabeler,
				mOnClickListener, position);
		return v;
	}

	public void setOnLineClickLIstener(OnItemClickListener onLineListener) {
		mOnClickListener = onLineListener;
	}
}
