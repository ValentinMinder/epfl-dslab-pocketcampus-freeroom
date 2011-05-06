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
public class MainscreenNews {

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
	
}
