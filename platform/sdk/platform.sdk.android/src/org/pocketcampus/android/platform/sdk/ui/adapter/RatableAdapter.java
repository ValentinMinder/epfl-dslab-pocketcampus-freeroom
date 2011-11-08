package org.pocketcampus.android.platform.sdk.ui.adapter;

import java.util.List;

import org.pocketcampus.android.platform.sdk.ui.element.RatableView;
import org.pocketcampus.android.platform.sdk.ui.labeler.IRatableLabeler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RatableAdapter extends AbstractArrayAdapter {
	private LayoutInflater mInflater;
	private IRatableLabeler mLabeler;
	private Context mContext;
	private List<? extends Object> mItems;
	
	public RatableAdapter(Context context, List<? extends Object> items, IRatableLabeler<? extends Object> labeler) {
		super(context, items);
		mContext = context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mLabeler = labeler;
		mItems = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
//		View view = convertView;
//		if (view == null) {
//			view = mInflater.inflate(mLayoutResourceId, null);
//		}
//		
//		TextView textView = (TextView) view.findViewById(mTextViewResourceId);
//		Object obj = getItem(position);
//		textView.setText(mLabeler.getTitle(obj));
		
		RatableView rv = new RatableView(getItem(position), mContext, mLabeler);
		
		return rv;
	}
}
