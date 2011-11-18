package org.pocketcampus.android.platform.sdk.ui.list;

import java.util.List;

import org.pocketcampus.android.platform.sdk.ui.adapter.PreferencesAdapter;
import org.pocketcampus.android.platform.sdk.ui.element.Element;
import org.pocketcampus.android.platform.sdk.ui.labeler.ILabeler;

import android.content.Context;
import android.widget.ListView;

/**
 * ListView that displays a list of Item using the default style.
 * @author Oriane
 *
 */
public class PreferencesListViewElement extends ListView implements Element {
	private PreferencesAdapter mAdapter;

	public PreferencesListViewElement(Context context, List<? extends Object> items, ILabeler labeler, String prefName) {
		super(context);
		
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		this.setLayoutParams(params);
		
		mAdapter = new PreferencesAdapter(context, items, labeler, prefName);
		setAdapter(mAdapter);
		
	}
	
	public void setOnItemClickListener(OnItemClickListener l) {
		mAdapter.setOnCheckBoxClickListener(l);
	}

}
