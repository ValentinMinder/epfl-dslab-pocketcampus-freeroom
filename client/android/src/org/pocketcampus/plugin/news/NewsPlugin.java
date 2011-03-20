package org.pocketcampus.plugin.news;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

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
	NewsProvider newsProvider_;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.news_list);

		newsProvider_ = NewsProvider.getInstance(this);
		
		setupActionBar(true);
		
		setLayout();
		
		
	}

	@Override
	protected void onResume() {
		super.onResume();

		newsProvider_.refreshIfNeeded();
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
		adapter_ = new NewsAdapter(this, newsProvider_);
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
