package org.pocketcampus.android.platform.sdk.ui.PCSectionedList;

public class PCEntryItem implements PCItem{

	public final String title;
	public final String subtitle;

	public PCEntryItem(String title, String subtitle) {
		this.title = title;
		this.subtitle = subtitle;
	}
	
	@Override
	public boolean isSection() {
		return false;
	}

}
