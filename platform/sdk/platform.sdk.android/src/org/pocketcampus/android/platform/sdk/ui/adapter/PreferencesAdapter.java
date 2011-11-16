package org.pocketcampus.android.platform.sdk.ui.adapter;

import java.util.List;

import org.pocketcampus.android.platform.sdk.ui.element.PreferencesView;
import org.pocketcampus.android.platform.sdk.ui.labeler.ILabeler;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;

public class PreferencesAdapter extends AbstractArrayAdapter {
	private ILabeler mLabeler;
	private Context mContext;
	private List<? extends Object> mItems;
	private OnItemClickListener mOnCheckBoxClickListener;
	
	public PreferencesAdapter(Context context, List<? extends Object> items, ILabeler<? extends Object> labeler) {
		super(context, items);
		mContext = context;
		mLabeler = labeler;
		mItems = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		PreferencesView rv = new PreferencesView(getItem(position), mContext, mLabeler, mOnCheckBoxClickListener, position);
		return rv;
	}
	
	public void setOnCheckBoxClickListener(OnItemClickListener l) {
		mOnCheckBoxClickListener = l;
	}
	
}
