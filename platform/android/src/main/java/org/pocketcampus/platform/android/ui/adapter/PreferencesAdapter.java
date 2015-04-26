package org.pocketcampus.platform.android.ui.adapter;

import java.util.List;

import org.pocketcampus.platform.android.ui.element.PreferencesView;
import org.pocketcampus.platform.android.ui.labeler.ILabeler;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;

/**
 * An array adapter to handle the <code>PreferencesView</code>. It's designed to
 * be used along with the <code>PreferencesListView</code> or an equivalent.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 */
public class PreferencesAdapter extends AbstractArrayAdapter {
	/** The labeler to get the object's attributes. */
	private ILabeler<? extends Object> mLabeler;
	/** The application context. */
	private Context mContext;
	/** The click listener for the object's line. */
	private OnItemClickListener mOnItemClickListener;
	/**
	 * The name of the <code>SharedPreferences</code> to retrieve. They won't be
	 * edited directly from the view, but we want to initialize the
	 * <code>CheckBoxe</code> according to the <code>SharedPreferences</code>
	 * when the <code>getView</code> is called.
	 */
	private String mPrefName;

	/**
	 * Class constructor.
	 * 
	 * @param context
	 *            The application context.
	 * @param items
	 *            The list of items to be displayed in the ListView.
	 * @param labeler
	 *            The labeler from the application that will let the adapter get
	 *            the object's attributes.
	 * @param prefName
	 *            The name of the <code>SharedPreferences</code> we want to
	 *            retrieve.
	 * @throws IllegalArgumentException
	 *             Thrown if the labeler is null.
	 * @throws IllegalArgumentException
	 *             Thrown if the prefName is null.
	 */
	public PreferencesAdapter(Context context, List<? extends Object> items,
			ILabeler<? extends Object> labeler, String prefName) {
		super(context, items);

		if (labeler == null) {
			new IllegalArgumentException("Labeler cannot be null!");
		}

		if (prefName == null) {
			new IllegalArgumentException("Preferences name cannot be null!");
		}

		mContext = context;
		mLabeler = labeler;
		mPrefName = prefName;
	}

	/**
	 * Overrides the <code>getView</code> method. Creates a
	 * <code>PreferencesView</code> for this object and sets its
	 * <code>LayoutParameters</code>.
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		PreferencesView rv = new PreferencesView(getItem(position), mContext,
				mLabeler, mPrefName, mOnItemClickListener, position);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		rv.setLayoutParams(params);
		return rv;
	}

	/**
	 * Sets the <code>CheckBox</code> listener on the object's line.
	 * 
	 * @param itemClickListener
	 *            The listener on the object's line.
	 */
	public void setOnCheckBoxClickListener(OnItemClickListener itemClickListener) {
		mOnItemClickListener = itemClickListener;
	}

}
