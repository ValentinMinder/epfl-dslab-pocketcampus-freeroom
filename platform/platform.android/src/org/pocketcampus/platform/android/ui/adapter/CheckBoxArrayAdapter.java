package org.pocketcampus.platform.android.ui.adapter;

import java.util.List;

import org.pocketcampus.platform.android.ui.element.CheckBoxView;
import org.pocketcampus.platform.android.ui.labeler.ILabeler;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;

/**
 * An array Adapter to handle a view with a text and two CheckBoxes considered
 * as a positive one and a negative one. It's designed to be used along with the
 * CheckBoxesListViewElement or an equivalent.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 */
public class CheckBoxArrayAdapter extends AbstractArrayAdapter {
	/** The labeler from the application, to get the object's attributes. */
	private ILabeler<? extends Object> mLabeler;
	/** The application context. */
	private Context mContext;
	/** The click listener for the object's line. */
	private OnItemClickListener mOnItemClickListener;

	/**
	 * Class constructor.
	 * 
	 * @param context
	 *            The application context.
	 * @param items
	 *            The list of items to be displayed in the ListView.
	 * @param labeler
	 *            The labeler from the application, that will let the adapter
	 *            get the object's attributes
	 * @throws IllegalArgumentException
	 *             Thrown if the labeler is null.
	 */
	public CheckBoxArrayAdapter(Context context, List<? extends Object> items,
			ILabeler<? extends Object> labeler) {
		super(context, items);

		if (labeler == null) {
			new IllegalArgumentException("Labeler cannot be null!");
		}

		mLabeler = labeler;
		mContext = context;
	}

	/**
	 * Overrides the <code>getView</code>. Creates a
	 * <code>PreferencesView</code> for this object and sets its
	 * <code>LayoutParameters</code>.
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CheckBoxView cv = new CheckBoxView(getItem(position), mContext,
				mLabeler, mOnItemClickListener, position);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		cv.setLayoutParams(params);
		return cv;
	}

	/**
	 * Sets the click listener for the object's line.
	 * 
	 * @param itemClickListener
	 *            The listener on the object's line.
	 */
	public void setOnCheckBoxClickListener(OnItemClickListener itemClickListener) {
		mOnItemClickListener = itemClickListener;
	}

}
