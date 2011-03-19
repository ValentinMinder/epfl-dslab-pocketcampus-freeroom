package org.pocketcampus.plugin.news;

import java.util.ArrayList;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.core.ui.ActionBar;
import org.pocketcampus.plugin.mainscreen.MainscreenPlugin;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class NewsPlugin extends PluginBase {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.news_list);
		
		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.setTitle("PocketCampus EPFL");
		actionBar.addAction(new ActionBar.IntentAction(this, MainscreenPlugin.createIntent(this), R.drawable.mini_home));
		
		final ListView l = (ListView) findViewById(R.id.news_list_list);
		final NewsAdapter a = new NewsAdapter(getApplicationContext(), R.layout.news_newsentry, new ArrayList<NewsItem>());
		l.setAdapter(a);
		//a.setDebugData();
		
		l.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				a.setClickedItem(parent, view, position, id);
				l.smoothScrollToPosition(position);
			}
		});
		
	}
	


	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}


	@Override
	public PluginInfo getPluginInfo() {
		return new NewsInfo();
	}



	@Override
	public PluginPreference getPluginPreference() {
		return new NewsPreference();
	}
	
	
}
