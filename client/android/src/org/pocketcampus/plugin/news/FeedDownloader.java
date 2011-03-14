package org.pocketcampus.plugin.news;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;

public class FeedDownloader extends AsyncTask<String, Void, List<NewsItem>> {
	
	private NewsAdapter adapter_;
	
	public FeedDownloader(NewsAdapter adapter) {
		this.adapter_ = adapter;
	}

	@Override
	protected List<NewsItem> doInBackground(String... params) {
		
		if(params.length != 1) {
			return null;
		}
		
		RssParser parser = new RssParser(params[0]);
		
		parser.parse();
		Feed feed = parser.getFeed();
		
		return feed.getItems();
	}

	@Override
	protected void onPostExecute(List<NewsItem> result) {
		adapter_.clear();
		for(NewsItem item : result) {
			adapter_.add(item);
		}
	}


}
