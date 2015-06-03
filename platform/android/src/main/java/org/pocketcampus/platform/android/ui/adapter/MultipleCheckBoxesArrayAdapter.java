package org.pocketcampus.platform.android.ui.adapter;

import java.util.List;

import org.pocketcampus.platform.android.ui.element.CheckBoxesView;
import org.pocketcampus.platform.android.ui.labeler.ILabeler;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;

/**
 * An array adapter to handle a view with a text and two <code>CheckBox</code>
 * considered as a positive one and a negative one. It's designed to be used
 * along with the <code>MultipleCheckBoxesListViewElement</code> or an
 * equivalent.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 */
public class MultipleCheckBoxesArrayAdapter extends AbstractArrayAdapter {
	/** The Labeler from the application, to get the object attributes. */
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
	 *            The list of items to be displayed in the list.
	 * @param labeler
	 *            The labeler that will let the adapter get the object's
	 *            attributes.
	 * @param prefName
	 *            The name of the <code>SharedPreferences</code> we want to
	 *            retrieve.
	 * @throws IllegalArgumentException
	 *             Thrown if the context is null.
	 * @throws IllegalArgumentException
	 *             Thrown if the labeler is null.
	 */
	public MultipleCheckBoxesArrayAdapter(Context context,
			List<? extends Object> items, ILabeler<? extends Object> labeler) {
		super(context, items);

		if (context == null) {
			new IllegalArgumentException("Context cannot be null!");
		}
		if (labeler == null) {
			new IllegalArgumentException("Labeler cannot be null!");
		}

		mContext = context;
		mLabeler = labeler;
	}

	/**
	 * Overrides the <code>getView</code> method. Creates a
	 * <code>PreferencesView</code> for this object and sets its
	 * <code>LayoutParameters</code>.
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CheckBoxesView cv = new CheckBoxesView(getItem(position), mContext,
				mLabeler, mOnItemClickListener, position);

		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		cv.setLayoutParams(params);
		return cv;
	}

	/**
	 * Sets the listener for the object's line.
	 * 
	 * @param itemClickListener
	 *            The Listener on the object's line.
	 */
	public void setOnCheckBoxClickListener(OnItemClickListener itemClickListener) {
		mOnItemClickListener = itemClickListener;
	}

}
