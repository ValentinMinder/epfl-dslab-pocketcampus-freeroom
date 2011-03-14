package org.pocketcampus.plugin.news;

/**
 * A class that describes a news item, to be displayed by the News plugins
 * 
 * @status complete
 * @author jonas
 * @license 
 *
 */

public class NewsItem {
	private String title_, description_, image_;

	public NewsItem(String title, String description, String image) {
		super();
		this.title_ = title;
		this.description_ = description;
		this.image_ = image;
	}

	public String getTitle() {
		return title_;
	}

	public String getDescription() {
		return description_;
	}

	public String getImage() {
		return image_;
	}
	
}
