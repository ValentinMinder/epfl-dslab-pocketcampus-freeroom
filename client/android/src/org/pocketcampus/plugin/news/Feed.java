package org.pocketcampus.plugin.news;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a feed with a list of news items.
 * 
 * @status complete
 * 
 * @author Johan
 * 
 * @see org.pocketcampus.plugin.news.NewsItem
 * @see org.pocketcampus.plugin.news.Feed
 *
 */
public class Feed {
	
	private String title_;
	private String link_;
	private String description_;
	private List<NewsItem> items_;
	
	public Feed() {
		items_ = new ArrayList<NewsItem>();
	}
	
	public void addItem(NewsItem item) {
		items_.add(item.clone());
	}
	
	public void setTitle(String title) {
		this.title_ = title;
	}
	
	public void setLink(String link) {
		this.link_ = link;
	}
	
	public void setDescription(String description) {
		this.description_ = description;
	}
	
	public String getDescription() {
		return description_;
	}
	
	public List<NewsItem> getItems() {
		return items_;
	}
	
	public String getLink() {
		return link_;
	}
	
	public String getTitle() {
		return title_;
	}
}
