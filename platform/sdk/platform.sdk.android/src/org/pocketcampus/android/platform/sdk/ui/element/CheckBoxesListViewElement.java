package org.pocketcampus.android.platform.sdk.ui.element;

import java.util.List;

import org.pocketcampus.android.platform.sdk.ui.adapter.CheckBoxesArrayAdapter;

import android.content.Context;
import android.widget.ListView;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * ListView that displays a list of Item using the default style.
 * @author Florian
 *
 */
public class CheckBoxesListViewElement extends ListView implements Element {
	private CheckBoxesArrayAdapter mAdapter;

	public CheckBoxesListViewElement(Context context, List<? extends Object> items) {
		super(context);
		
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		setLayoutParams(params);
		
		mAdapter = new CheckBoxesArrayAdapter(context, items);
		setAdapter(mAdapter);
	}
	
	public void setOnPositiveBoxCheckedChangeListener(OnCheckedChangeListener l) {
		mAdapter.setOnPositiveBoxClickListener(l);
	}
	
	public void setOnNegativeBoxCheckedChangeListener(OnCheckedChangeListener l) {
		mAdapter.setOnNegativeBoxClickListener(l);
	}
	
	public void setOnItemClickListener(OnItemClickListener l) {
		mAdapter.setOnItemClickListener(l);
	}
	
	public List<String> getPositiveTags() {
		return mAdapter.getPositiveTags();
	}
	
	public List<String> getNegativeTags() {
		return mAdapter.getNegativeTags();
	}
}
