package org.pocketcampus.platform.android.ui.PCSectionedList;

import android.widget.RelativeLayout;

public class PCEmptyLayoutItem implements PCItem {

	private final RelativeLayout layout;
	
	public PCEmptyLayoutItem(RelativeLayout layout) {
		this.layout = layout;
	}
	
	public RelativeLayout getLayout(){
		return layout;
	}
	
	@Override
	public boolean isSection() {
		return false;
	}

	@Override
	public boolean isEmptyLayout() {
		return true;
	}

}
