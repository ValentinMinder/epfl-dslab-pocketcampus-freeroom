package org.pocketcampus.android.platform.sdk.ui.element;

import java.util.List;

import org.pocketcampus.android.platform.sdk.ui.adapter.LabeledArrayAdapter;

import android.content.Context;
import android.widget.ListView;

/**
 * ListView that displays a list of Item using the default style.
 * @author Florian
 *
 */
public class LabeledListViewElement extends ListView implements Element {
	private LabeledArrayAdapter mAdapter;

	public LabeledListViewElement(Context context, List<? extends Object> items, Labeler<? extends Object> labeler) {
		super(context);
		
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		setLayoutParams(params);
		
		LabeledArrayAdapter adapter = new LabeledArrayAdapter(context, items, labeler);
		setAdapter(adapter);
	}
	
}
