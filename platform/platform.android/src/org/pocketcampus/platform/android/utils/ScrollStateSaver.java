package org.pocketcampus.platform.android.utils;

import android.view.View;
import android.widget.ListView;

/****
 * ScrollStateSaver
 * @author Amer Chamseddine <amer@accandme.com>
 *
 */

public  class ScrollStateSaver {
	int index;
	int top;
	public ScrollStateSaver(ListView mList) {
		index = mList.getFirstVisiblePosition();
		View v = mList.getChildAt(0);
		top = (v == null ? 0 : v.getTop());
	}
	public void restore(ListView mList) {
		mList.setSelectionFromTop(index, top);
	}
}
