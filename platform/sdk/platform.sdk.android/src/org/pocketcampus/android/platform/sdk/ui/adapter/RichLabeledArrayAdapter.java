package org.pocketcampus.android.platform.sdk.ui.adapter;

import java.util.List;

import org.pocketcampus.android.platform.sdk.ui.element.RichView;
import org.pocketcampus.android.platform.sdk.ui.labeler.IRichLabeler;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;

/**
 * An adapter to handle the display of rich labeled objects.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 */
public class RichLabeledArrayAdapter extends AbstractArrayAdapter {
	/** The <code>IRichLabeler</code> to get the object's attributes. */
	private IRichLabeler<? extends Object> mLabeler;
	/** The application context. */
	private Context mContext;
	/** The click listener on the object's line. */
	private OnItemClickListener mOnClickListener;

	/**
	 * CLass constructor.
	 * 
	 * @param context
	 *            The application context.
	 * @param items
	 *            The items to display.
	 * @param labeler
	 *            The <code>IRichLabeler</code> to get the object's attributes.
	 * @throws IllegalArgumentException
	 *             Thrown if the context is null.
	 * @throws IllegalArgumentException
	 *             Thrown if the labeler is null.
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
	 * Overrides the <code>getView</code> method and creates a
	 * <code>RichView</code> for this object.
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = new RichView(getItem(position), mContext, mLabeler,
				mOnClickListener, position);
		return v;
	}

	/**
	 * Sets the listener on the object's line.
	 * 
	 * @param onLineListener
	 *            The listener for the object's line.
	 */
	public void setOnLineClickLIstener(OnItemClickListener onLineListener) {
		mOnClickListener = onLineListener;
	}
}
