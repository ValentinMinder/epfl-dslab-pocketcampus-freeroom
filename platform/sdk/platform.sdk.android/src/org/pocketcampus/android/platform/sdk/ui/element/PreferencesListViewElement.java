package org.pocketcampus.android.platform.sdk.ui.element;

import java.util.List;

import org.pocketcampus.android.platform.sdk.ui.adapter.StandardPreferencesArrayAdapter;

import android.content.Context;
import android.widget.ListView;

/**
 * ListView that displays a list of Item using the default style.
 * @author Florian
 *
 */
public class PreferencesListViewElement extends ListView implements Element {
	private StandardPreferencesArrayAdapter mAdapter;

	public PreferencesListViewElement(Context context, List<? extends Object> items) {
		super(context);
		
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		setLayoutParams(params);
		
		mAdapter = new StandardPreferencesArrayAdapter(context, items);
		setAdapter(mAdapter);
		
	}
	
	public void setOnItemClickListener(OnItemClickListener l) {
		mAdapter.setOnItemClickListener(l);
	}

}
