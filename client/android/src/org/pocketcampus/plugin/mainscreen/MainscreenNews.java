package org.pocketcampus.plugin.mainscreen;

public class MainscreenNews {

	private String title_;
	private String content_;
	
	public MainscreenNews(String title, String content) {
		this.content_ = content;
		this.title_ = title;
	}
	
	public String getContent_() {
		return content_;
	}
	
	public String getTitle_() {
		return title_;
	}
	
}
