package org.pocketcampus.plugin.mainscreen;

import java.util.Date;

import org.pocketcampus.core.plugin.PluginBase;

/**
 * 
 * This class contains the information needed to display a news on the mainscreen
 * 
 * @status complete
 * 
 * @author Guillaume
 *
 */
public class MainscreenNews implements Comparable<MainscreenNews> {

	private String title_;
	private String content_;
	private int id_;
	private PluginBase plugin_;
	private Date date_;
	
	public MainscreenNews(String title, String content, int id, PluginBase plugin, Date date) {
		this.content_ = content;
		this.title_ = title;
		this.id_ = id;
		this.plugin_ = plugin;
		this.date_ = date;
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
	
	public PluginBase getPlugin_() {
		return plugin_;
	}
	
	public Date getDate_() {
		return date_;
	}

	@Override
	public int compareTo(MainscreenNews another) {
		if(another == null) return 1;
		if(another.date_ == null ) return 1;
		if(this.date_ == null) return -1;
		if(this.equals(another)) return 0;
		int signum = (int) Math.signum((this.date_.getTime()-another.date_.getTime()));
		return (signum != 0) ? signum : -1; //Because two items may have the same date, but are different and as we use SortedSet, 0 means the items are equal
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null) return false;
		if(o instanceof MainscreenNews) {
			MainscreenNews other = (MainscreenNews) o;
			return content_.equals(other.content_) && id_ == other.id_ && title_.equals(other.title_) && date_.equals(other.date_);
		}
		return super.equals(o);
	}
	
}
