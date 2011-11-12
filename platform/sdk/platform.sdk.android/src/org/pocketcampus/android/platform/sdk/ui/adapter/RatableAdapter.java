package org.pocketcampus.android.platform.sdk.ui.adapter;

import java.util.List;

import org.pocketcampus.android.platform.sdk.ui.element.RatableView;
import org.pocketcampus.android.platform.sdk.ui.labeler.IRatableLabeler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class RatableAdapter extends AbstractArrayAdapter {
	private LayoutInflater mInflater;
	private IRatableLabeler mLabeler;
	private Context mContext;
	private List<? extends Object> mItems;
	private OnItemClickListener mOnLineClickListener;
	private OnItemClickListener mOnRatingClickListener;
	
	public RatableAdapter(Context context, List<? extends Object> items, IRatableLabeler<? extends Object> labeler) {
		super(context, items);
		mContext = context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
