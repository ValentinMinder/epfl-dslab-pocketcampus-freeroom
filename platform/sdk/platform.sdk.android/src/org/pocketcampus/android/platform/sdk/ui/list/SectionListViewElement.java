//package org.pocketcampus.android.platform.sdk.ui.list;
//
//import org.pocketcampus.android.platform.sdk.ui.adapter.SectionListAdapter;
//import org.pocketcampus.android.platform.sdk.ui.element.Element;
//
//import android.content.Context;
//import android.widget.ListView;
//
///**
// * ListView that displays a list of Items using the sections style.
// * 
// * @author Elodie
// * 
// */
//public class SectionListViewElement extends ListView implements Element {
//	private SectionListAdapter mAdapter;
//
//	public SectionListViewElement(Context context) {
//		super(context);
//
//		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
//				LayoutParams.FILL_PARENT);
//		setLayoutParams(params);
//
//		mAdapter = new SectionListAdapter(context);
//		setAdapter(mAdapter);
//	}
//
//	@Override
//	public SectionListAdapter getAdapter() {
//		return mAdapter;
//	}
//
//}
