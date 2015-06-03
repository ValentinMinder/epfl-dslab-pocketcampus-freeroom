package org.pocketcampus.plugin.news.android;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.pocketcampus.platform.android.core.IView;
import org.pocketcampus.platform.android.core.PluginModel;
import org.pocketcampus.plugin.news.android.iface.INewsModel;
import org.pocketcampus.plugin.news.android.iface.INewsView;
import org.pocketcampus.plugin.news.shared.NewsFeed;
import org.pocketcampus.plugin.news.shared.NewsFeedItemContent;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class NewsModel extends PluginModel implements INewsModel {


	
	/**
	 * Some constants.
	 */
	private static final String NEWS_STORAGE_NAME = "NEWS_STORAGE_NAME";
	
	private static final String NEWS_DISLIKED_FEEDS_KEY = "NEWS_DISLIKED_FEEDS_KEY";
	
	/**
	 * SharedPreferences object responsible for the persistent data storage.
	 */
	private SharedPreferences iStorage;
	
	/**
	 * Reference to the Views that need to be notified when the stored data changes.
	 */
	INewsView mListeners = (INewsView) getListeners();
	
	/**
	 * Member variables containing required data for the plugin.
	 */
	private List<NewsFeed> newsFeeds;
	private NewsFeedItemContent itemContents;
	
	/**
	 * Member variables that need to be persistent
	 */
	private Set<String> dislikedFeeds = new HashSet<String>();
	
	/**
	 * Constructor with reference to the context.
	 * 
	 * We need the context to be able to instantiate
	 * the SharedPreferences object in order to use
	 * persistent storage.
	 * 
	 * @param context is the Application Context.
	 */
	public NewsModel(Context context) {
		iStorage = context.getSharedPreferences(NEWS_STORAGE_NAME, 0);
		
		dislikedFeeds = decodeFeeds(iStorage.getString(NEWS_DISLIKED_FEEDS_KEY, ""));
		
		newsFeeds = new LinkedList<NewsFeed>();
		itemContents = new NewsFeedItemContent();
	}
	
	/**
	 * Setter and getter for iNews
	 */
	public List<NewsFeed> getNewsFeeds() {
		return newsFeeds;
	}
	public void setNewsFeeds(List<NewsFeed> obj) {
		newsFeeds = obj;
		mListeners.gotFeeds();
	}

	public NewsFeedItemContent getItemContents() {
		return itemContents;
	}
	public void setItemContents(NewsFeedItemContent obj) {
		itemContents = obj;
		mListeners.gotContents();
	}
	/**
	 * Getters/Setters for persistent stuff
	 */
	public Set<String> getDislikedFeeds() {
		return dislikedFeeds;
	}
	public void addDislikedFeed(String s) {
		dislikedFeeds.add(s);
		savePrefs();
	}
	public void removeDislikedFeed(String s) {
		dislikedFeeds.remove(s);
		savePrefs();
	}

	private void savePrefs() {
		iStorage.edit()
				.putString(NEWS_DISLIKED_FEEDS_KEY, encodeFeeds(dislikedFeeds))
				.commit();
	}
	
	/**
	 * Returns the Type of the Views associated with this plugin.
	 */
	@Override
	protected Class<? extends IView> getViewInterface() {
		return INewsView.class;
	}

	/**
	 * Returns the registered listeners to by notified.
	 */
	public INewsView getListenersToNotify() {
		return mListeners;
	}
		
	
	/***
	 * HELPERS
	 */
	
	

	private String encodeFeeds(Set<String> ss) {
		return TextUtils.join(",", ss);
	}
	private Set<String> decodeFeeds(String ss) {
		Set<String> decoded = new HashSet<String>();
		if("".equals(ss))
			return decoded;
		for(String s : ss.split("[,]")) {
			decoded.add(s);
		}
		return decoded;
	}
}
