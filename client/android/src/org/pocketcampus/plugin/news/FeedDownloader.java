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
		
		List<NewsItem> list = new ArrayList<NewsItem>();
		
		if(params.length < 1) {
			return null;
		}
		
		RssParser parser;
		Feed feed;
		for(String feedUrl : params) {
			parser = new RssParser(feedUrl);
			
			parser.parse();
			feed = parser.getFeed();
			
			list.addAll(feed.getItems());
		}
		
		return list;
	}
	
	@Override
	protected void onPostExecute(List<NewsItem> result) {
		adapter_.clear();
		for(NewsItem item : result) {
			adapter_.add(item);
		}
		adapter_.dataSetChanged();
	}


}
