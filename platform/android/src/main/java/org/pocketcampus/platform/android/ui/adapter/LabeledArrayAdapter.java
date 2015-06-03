package org.pocketcampus.platform.android.ui.adapter;

import java.util.List;

import org.pocketcampus.platform.android.ui.labeler.ILabeler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author Florian <florian.laurent@epfl.ch>
 *
 */
public class LabeledArrayAdapter extends AbstractArrayAdapter {
	private LayoutInflater mInflater;
	private ILabeler mLabeler;

	public LabeledArrayAdapter(Context context, List<? extends Object> items,
			ILabeler<? extends Object> labeler) {
		super(context, items);

		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mLabeler = labeler;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = mInflater.inflate(mLayoutResourceId, null);
		}

		TextView textView = (TextView) view.findViewById(mTextViewResourceId);
		Object obj = getItem(position);
		textView.setText(mLabeler.getLabel(obj));

		return view;
	}
}
