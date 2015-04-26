package org.pocketcampus.platform.android.ui.adapter;

import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.platform.android.ui.element.ElementDimension;

import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * @author Florian <florian.laurent@epfl.ch>
 */
public abstract class AbstractArrayAdapter extends ArrayAdapter<Object> {
	protected static int mLayoutResourceId = R.layout.sdk_list_entry;
	protected static int mTextViewResourceId = R.id.sdk_list_entry_text;

	private ElementDimension mDimension = ElementDimension.NORMAL;
	
	public AbstractArrayAdapter(Context context, List<? extends Object> items) {
		super(context, mLayoutResourceId, mTextViewResourceId, items.toArray());
	}
	
	public void setDimension(ElementDimension dimension) {
		mDimension = dimension;
	}
	
}
