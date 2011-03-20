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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * PluginBase class for the News plugin. 
 * 
 * @status complete
 * 
 * @author Jonas
 *
 */
public class NewsPlugin extends PluginBase {

	NewsAdapter adapter_;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.news_list);

		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.setTitle(getResources().getString(R.string.app_name));
		actionBar.addAction(new ActionBar.IntentAction(this, MainscreenPlugin.createIntent(this), R.drawable.mini_home));

		setLayout();
	}

	@Override
	protected void onResume() {
		super.onResume();

		adapter_.refreshIfNeeded();
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
	
	private void setLayout() {
		final ListView l = (ListView) findViewById(R.id.news_list_list);
		adapter_ = new NewsAdapter(this, new ArrayList<NewsItem>());
		l.setAdapter(adapter_);

		l.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(adapter_ != null) {
					adapter_.setClickedItem(parent, view, position, id);
					l.smoothScrollToPosition(position);
				}
			}
		});
	}


}
