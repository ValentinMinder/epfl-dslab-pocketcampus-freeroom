package org.pocketcampus.plugin.mainscreen;

/**
 * 
 * This class contains the information needed to display a news on the mainscreen
 * 
 * @status complete
 * 
 * @author Guillaume
 *
 */
public class MainscreenNews {

	private String title_;
	private String content_;
	private int id_;
	
	public MainscreenNews(String title, String content, int id) {
		this.content_ = content;
		this.title_ = title;
		this.id_ = id;
	}
	
	public String getContent_() {
		return content_;
	}
	
	public String getTitle_() {
		return title_;
	}
	
	public int getId_() {
		return id_;
	}
	
}
