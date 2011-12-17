package org.pocketcampus.android.platform.sdk.ui.adapter;

import java.util.List;

import org.pocketcampus.android.platform.sdk.ui.element.CheckBoxView;
import org.pocketcampus.android.platform.sdk.ui.labeler.ILabeler;

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
	/** The Labeler from the Application, to get the Object attributes */
	private ILabeler mLabeler;
	/** The Application context */
	private Context mContext;
	/** The List of Items in the ListView */
	private List<? extends Object> mItems;
	/** The CheckBoxes click listener to be set from the Application */
	private OnItemClickListener mOnCheckBoxClickListener;
	/**
	 * The name of the SharedPreferences to retrieve. They won't be edited
	 * directly from the View, but we want to initialize the CheckBoxes
	 * according to the SharedPreferences.
	 */
	private String mPrefName;

	/**
	 * The constructor
	 * 
	 * @param context
	 *            The Application context
	 * @param items
	 *            The list of items to be displayed in the ListView
	 * @param labeler
	 *            The Labeler from the Application, that will let the Adapter
	 *            get the Objects attributes
	 * @param prefName
	 *            The name of the SharedPreferences we want to retrieve
	 */
	public CheckBoxArrayAdapter(Context context,
			List<? extends Object> items, ILabeler<? extends Object> labeler) {
		super(context, items);
		mContext = context;
		mLabeler = labeler;
		mItems = items;
	}

	/**
	 * Overrides the getView() method. Creates a PreferencesView and sets its
	 * LayoutParameters.
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CheckBoxView cv = new CheckBoxView(getItem(position), mContext,
				mLabeler, mOnCheckBoxClickListener, position);

		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		cv.setLayoutParams(params);
		return cv;
	}

	/**
	 * Sets the positive CheckBox click listener from the Application
	 * 
	 * @param checkBoxListener
	 *            The Listener set by the Application
	 */
	public void setOnCheckBoxClickListener(OnItemClickListener checkBoxListener) {
		mOnCheckBoxClickListener = checkBoxListener;
	}

}
