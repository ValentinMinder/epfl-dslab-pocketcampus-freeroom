package org.pocketcampus.plugin.news;

import java.util.ArrayList;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.DisplayBase;

import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.ListView;

public class NewsDisplay extends DisplayBase {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.news_list);
		
		ListView l = (ListView) findViewById(R.id.news_list_list);
		NewsAdapter a = new NewsAdapter(getApplicationContext(), R.layout.news_newsentry, new ArrayList<NewsItem>());
		//a.setDebugData();
		l.setAdapter(a);
		
	}
	


	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
}
