package org.pocketcampus.android.platform.sdk.ui.adapter;

import java.util.List;

import org.pocketcampus.android.platform.sdk.ui.element.RatableView;
import org.pocketcampus.android.platform.sdk.ui.labeler.IRatableViewLabeler;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;

public class RatableAdapter extends AbstractArrayAdapter {
	private IRatableViewLabeler mLabeler;
	private Context mContext;
	private List<? extends Object> mItems;
	private OnItemClickListener mOnLineClickListener;
	private OnItemClickListener mOnRatingClickListener;
	
	public RatableAdapter(Context context, List<? extends Object> items, IRatableViewLabeler<? extends Object> labeler) {
		super(context, items);
		mContext = context;
		mLabeler = labeler;
		mItems = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		RatableView rv = new RatableView(getItem(position), mContext, mLabeler, mOnLineClickListener, mOnRatingClickListener, position);
		return rv;
	}
	
	public void setOnLineClickListener(OnItemClickListener l) {
		mOnLineClickListener = l;
	}

	public void setOnRatingClickListener(OnItemClickListener l) {
		mOnRatingClickListener = l;
	}
	
}
