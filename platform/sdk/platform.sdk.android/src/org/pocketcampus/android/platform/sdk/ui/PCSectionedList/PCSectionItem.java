package org.pocketcampus.android.platform.sdk.ui.PCSectionedList;

public class PCSectionItem implements PCItem{

	private final String title;
	
	public PCSectionItem(String title) {
		this.title = title;
	}
	
	public String getTitle(){
		return title;
	}
	
	@Override
	public boolean isSection() {
		return true;
	}

}
