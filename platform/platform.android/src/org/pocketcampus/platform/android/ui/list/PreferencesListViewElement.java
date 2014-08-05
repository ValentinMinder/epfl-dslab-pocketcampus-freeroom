package org.pocketcampus.platform.android.ui.list;

import java.util.List;

import org.pocketcampus.platform.android.ui.adapter.PreferencesAdapter;
import org.pocketcampus.platform.android.ui.element.Element;
import org.pocketcampus.platform.android.ui.labeler.ILabeler;

import android.content.Context;
import android.widget.ListView;

/**
 * <code>ListView</code> displaying a list of items using the default style. It
 * is called <code>PreferencesListView</code> because it is used to let the user
 * choose preferences for these items.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 */
public class PreferencesListViewElement extends ListView implements Element {
	private PreferencesAdapter mAdapter;

	/**
	 * Class constructor.
	 * 
	 * @param context
	 *            The application context.
	 * @param items
	 *            The list of items to be displayed in the list.
	 * @param labeler
	 *            The labeler to get the objects attributes.
	 * @param prefName
	 *            The name of the <code>SharedPreferences</code> to retrieve.
	 */
	public PreferencesListViewElement(Context context,
			List<? extends Object> items, ILabeler<? extends Object> labeler, String prefName) {
		super(context);

		// Layout parameters
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		this.setLayoutParams(params);

		// Creating the adapter
		mAdapter = new PreferencesAdapter(context, items, labeler, prefName);
		setAdapter(mAdapter);

	}

	/**
	 * Sets the click listener for the <code>CheckBox</code>.Since it is a
	 * <code>PreferencesListViewElement</code>, the listener should modify in
	 * some way the preferences about the clicked object.
	 * 
	 * @param clickListener
	 *            The click listener created in the application.
	 */
	public void setOnItemClickListener(OnItemClickListener l) {
		mAdapter.setOnCheckBoxClickListener(l);
	}

}
