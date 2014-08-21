package org.pocketcampus.platform.android.ui.list;

import java.util.List;

import org.pocketcampus.platform.android.ui.adapter.StandardArrayAdapter;
import org.pocketcampus.platform.android.ui.element.Element;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * ListView that displays a list of Item using the default style.
 * @author Florian
 *
 */
public class ListViewElement extends ListView implements Element {
	private ArrayAdapter<Object> mAdapter;

	public ListViewElement(Context context, List<? extends Object> items) {
		super(context);
		
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		setLayoutParams(params);
		
		mAdapter = new StandardArrayAdapter(context, items);
		setAdapter(mAdapter);
	}
	
}
