package org.pocketcampus.platform.android.ui.PCSectionedList;

public class PCEntryItem implements PCItem{

	public final String title;
	public final String subtitle;
	public final String id;

	public PCEntryItem(String title, String subtitle, String id) {
		this.title = title;
		this.subtitle = subtitle;
		this.id = id;
	}
	
	@Override
	public boolean isSection() {
		return false;
	}

	@Override
	public boolean isEmptyLayout() {
		return false;
	}

}
