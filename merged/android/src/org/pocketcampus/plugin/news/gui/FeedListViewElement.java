package org.pocketcampus.plugin.news.gui;

import java.util.List;

import org.pocketcampus.android.platform.sdk.ui.element.Element;
import org.pocketcampus.plugin.news.shared.NewsItem;

import android.content.Context;
import android.widget.ListView;

/**
 * ListView that displays a list of Item using the RSS style.
 * @author Elodie
 *
 */
public class FeedListViewElement extends ListView implements Element {

	private StandardFeedAdapter mAdapter;

	public FeedListViewElement(Context context, List<NewsItem> newsList) {
		super(context);
		
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		setLayoutParams(params);
		
		mAdapter = new StandardFeedAdapter(context, newsList);
//		setAdapter(mAdapter);
	}
	
	public void setOnItemClickListener(OnItemClickListener l) {
//		mAdapter.setOnItemClickListener(l);
	}
}
