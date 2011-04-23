package org.pocketcampus.plugin.mainscreen;

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
	
	public MainscreenNews(String title, String content, int id, PluginBase plugin) {
		this.content_ = content;
		this.title_ = title;
		this.id_ = id;
		this.plugin_ = plugin;
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
	
}
