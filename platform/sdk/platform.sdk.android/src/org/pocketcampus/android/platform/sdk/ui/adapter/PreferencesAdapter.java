package org.pocketcampus.android.platform.sdk.ui.adapter;

import java.util.List;

import org.pocketcampus.android.platform.sdk.ui.element.PreferencesView;
import org.pocketcampus.android.platform.sdk.ui.labeler.ILabeler;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;

/**
 * An array Adapter to handle the PreferencesView. It's designed to be used
 * along with the PreferencesListView or an equivalent.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 */
public class PreferencesAdapter extends AbstractArrayAdapter {
	/** The Labeler from the Application, to get the Object attributes */
	private ILabeler mLabeler;
	/** The Application context */
	private Context mContext;
	/** The List of Items in the ListView */
	private List<? extends Object> mItems;
	/** The ItemClickListener to be set from the Application */
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
	public PreferencesAdapter(Context context, List<? extends Object> items,
			ILabeler<? extends Object> labeler, String prefName) {
		super(context, items);
		
		if(context == null) {
			new IllegalArgumentException("Context cannot be null!");
		}
		
		if(items == null) {
			new IllegalArgumentException("The list of items cannot be null!");
		}
		
		if (labeler == null) {
			new IllegalArgumentException("Labeler cannot be null!");
		}
		
		if(prefName == null) {
			new IllegalArgumentException("Preferences name cannot be null!");
		}
		
		mContext = context;
		mLabeler = labeler;
		mItems = items;
		mPrefName = prefName;
	}

	/**
	 * Overrides the getView() method. Creates a PreferencesView and sets its
	 * LayoutParameters.
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		PreferencesView rv = new PreferencesView(getItem(position), mContext,
				mLabeler, mPrefName, mOnCheckBoxClickListener, position);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		rv.setLayoutParams(params);
		return rv;
	}

	/**
	 * Sets the CheckBoxClickListener from the Application
	 * 
	 * @param checkBoxListener
	 *            The Listener set by the Applciation
	 */
	public void setOnCheckBoxClickListener(OnItemClickListener checkBoxListener) {
		mOnCheckBoxClickListener = checkBoxListener;
	}

}
