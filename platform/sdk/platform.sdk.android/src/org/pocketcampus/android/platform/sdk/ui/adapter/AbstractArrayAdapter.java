package org.pocketcampus.android.platform.sdk.ui.adapter;

import java.util.List;

import org.pocketcampus.android.platform.sdk.R;

import android.content.Context;
import android.widget.ArrayAdapter;

public abstract class AbstractArrayAdapter extends ArrayAdapter<Object> {
	// TODO make customizable
	private static int mLayoutResourceId = R.layout.sdk_list_entry;
	private static int mTextViewResourceId = R.id.travel_summary_time;

	public AbstractArrayAdapter(Context context, List<? extends Object> items) {
		super(context, mLayoutResourceId, mTextViewResourceId, items.toArray());
	}
	
	protected int getLayoutResourceId() {
		return mLayoutResourceId;
	}
	
	protected int getTextViewResourceId() {
		return mTextViewResourceId;
	}

}
