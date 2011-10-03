package org.pocketcampus.android.platform.sdk.ui.adapter;

import java.util.List;

import org.pocketcampus.android.platform.sdk.ui.element.Labeler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class LabeledArrayAdapter extends AbstractArrayAdapter {
	private List<String> mItemLabels;
	private LayoutInflater mInflater;
	private Labeler mLabeler;
	
	public LabeledArrayAdapter(Context context, List<? extends Object> items, Labeler<? extends Object> labeler) {
		super(context, items);
		
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		mItemLabels = itemLabels;
		mLabeler = labeler;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = mInflater.inflate(getLayoutResourceId(), null);
		}
		
		TextView textView = (TextView) view.findViewById(getTextViewResourceId());
		//textView.setText(mItemLabels.get(position));
		Object obj = getItem(position);
		textView.setText(mLabeler.getLabel(obj));
		
		return view;
	}
}
