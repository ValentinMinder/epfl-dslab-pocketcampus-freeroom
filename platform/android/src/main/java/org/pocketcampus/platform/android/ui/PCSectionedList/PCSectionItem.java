package org.pocketcampus.platform.android.ui.PCSectionedList;

public class PCSectionItem implements PCItem{

	private final String title;
	private final String description;
	
	public PCSectionItem(String title) {
		this.title = title;
		this.description = "";
	}
	
	public PCSectionItem(String title, String description) {
		this.title = title;
		this.description = description;
	}
	
	public String getTitle(){
		return title;
	}
	
	public String getDescription(){
		return description;
	}
	
	@Override
	public boolean isSection() {
		return true;
	}

	@Override
	public boolean isEmptyLayout() {
		return false;
	}

}
