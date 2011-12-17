package org.pocketcampus.android.platform.sdk.ui.adapter;

import java.util.ArrayList;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.ui.element.IconTextView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 *
 */
public class IconTextArrayAdapter extends ArrayAdapter<String> {
	private Context mContext;
	private int mIconResourceId;

	public IconTextArrayAdapter(Context context, ArrayList<String> items,
			int iconResourceId) {
		super(context, R.id.sdk_list_entry_text, items);
		mContext = context;
		mIconResourceId = iconResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		view = new IconTextView(getItem(position), mContext, mIconResourceId);

		return view;
	}

}
