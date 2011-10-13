package org.pocketcampus.android.platform.sdk.ui.element;

import java.util.List;

import org.pocketcampus.android.platform.sdk.ui.adapter.LabeledArrayAdapter;

import android.content.Context;
import android.widget.ListView;

/**
 * Labeled list that displays a list of Item using the default style.
 * (Labeled means that it gets the text of its element from a <code>Labeler</code>.)
 * @author Florian
 *
 */
public class LabeledListViewElement extends ListView implements Element {
	public LabeledListViewElement(Context context, List<? extends Object> items, Labeler<? extends Object> labeler) {
		super(context);
		
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		setLayoutParams(params);
		
		LabeledArrayAdapter adapter = new LabeledArrayAdapter(context, items, labeler);
		setAdapter(adapter);
	}

	public LabeledListViewElement(Context context) {
		super(context);
	}
	
}
