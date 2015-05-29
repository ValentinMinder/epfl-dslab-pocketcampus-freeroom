package org.pocketcampus.platform.android.ui.adapter;

import java.util.ArrayList;

import org.pocketcampus.platform.android.R;
import org.pocketcampus.platform.android.ui.element.IconTextView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * An array adapter to handle a view with an icon and a text. You can set the
 * icon used and the list of items to be displayed in the list. It is designed
 * to be used along with a <code>ListView</code> or an equivalent.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 */
public class IconTextArrayAdapter extends ArrayAdapter<String> {
	/** The application context. */
	private Context mContext;
	/** The icon resource value. */
	private int mIconResourceId;

	/**
	 * Class constructor.
	 * 
	 * @param context
	 *            The application context.
	 * @param items
	 *            The list of item to be displayed in the list.
	 * @param iconResourceId
	 *            The resource value for the icon that will appear in each line
	 *            next to the text.
	 */
	public IconTextArrayAdapter(Context context, ArrayList<String> items,
			int iconResourceId) {
		super(context, R.id.sdk_list_entry_text, items);
		mContext = context;
		mIconResourceId = iconResourceId;
	}

	/**
	 * Overrides the <code>getView</code> method. Creates an
	 * <code>IconTextView</code> for this object and sets its
	 * <code>LayoutParameters</code>.
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		view = new IconTextView(getItem(position), mContext, mIconResourceId);
		return view;
	}

}
