package org.pocketcampus.android.platform.sdk.ui.element;

import java.util.List;

import org.pocketcampus.android.platform.sdk.ui.adapter.StandardMinimalArrayAdapter;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * ListView that displays a list of Item using the minimal style. (without anything but text)
 * @author Oriane
 *
 */
public class MinimalListViewElement extends ListView implements Element {
	private ArrayAdapter<Object> mAdapter;

	public MinimalListViewElement(Context context, List<? extends Object> items) {
		super(context);
		
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		setLayoutParams(params);
		
		mAdapter = new StandardMinimalArrayAdapter(context, items);
		setAdapter(mAdapter);
	}
	
}
