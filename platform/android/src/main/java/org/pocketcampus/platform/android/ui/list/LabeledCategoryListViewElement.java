package org.pocketcampus.platform.android.ui.list;

import java.util.List;

import org.pocketcampus.platform.android.ui.adapter.CategoryListAdapter;
import org.pocketcampus.platform.android.ui.adapter.LabeledArrayAdapter;
import org.pocketcampus.platform.android.ui.element.Element;
import org.pocketcampus.platform.android.ui.labeler.ILabeler;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * A list Element that provides support for sections. Each section is added by
 * providing a section label and an adapter for its content.
 * 
 * @author Florian
 * 
 */
public class LabeledCategoryListViewElement extends ListView implements Element {
	/** Main <code>Adapter</code> that contains the different categories. */
	private CategoryListAdapter mAdapter;

	/** The context of the calling application */
	private Context mContext;

	public LabeledCategoryListViewElement(final Context context) {
		super(context);
		mContext = context;

		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		setLayoutParams(params);

		mAdapter = new CategoryListAdapter(context);
		setAdapter(mAdapter);
	}

	public void addSection(String label, List<? extends Object> items,
			ILabeler labeler) {
		ArrayAdapter<?> adapter = new LabeledArrayAdapter(mContext, items,
				labeler);
		mAdapter.addSection(label, adapter);
	}

}
