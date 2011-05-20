package org.pocketcampus.plugin.news;

import java.util.ArrayList;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.ICallback;
import org.pocketcampus.core.plugin.NoIDException;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.core.ui.ActionBar;
import org.pocketcampus.core.ui.ActionBar.Action;
import org.pocketcampus.plugin.logging.Tracker;
import org.pocketcampus.plugin.mainscreen.IAllowsID;
import org.pocketcampus.plugin.mainscreen.IMainscreenNewsProvider;
import org.pocketcampus.plugin.mainscreen.MainscreenNews;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
public class NewsPlugin extends PluginBase implements IMainscreenNewsProvider, INewsListener, IAllowsID {

	private NewsAdapter adapter_;
	private NewsProvider newsProvider_;
	private ActionBar actionBar_;
	private final static int NB_NEWS = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_list);

		newsProvider_ = NewsProvider.getInstance(this);
		newsProvider_.addNewsListener(this);

		setupActionBar(true);

		setLayout();
		
		// An Intent was sent
		if(hasIDInIntent()) {
			try {
				showNews(getIDFromIntent());
			} catch (NoIDException e) {}
		}
		
		Tracker.getInstance().trackPageView("news/home");
	}

	@Override
	protected void setupActionBar(boolean addHomeButton) {

		actionBar_ = (ActionBar) findViewById(R.id.actionbar);
		actionBar_.addAction(new Action() {

			@Override
			public void performAction(View view) {
				newsProvider_.forceRefresh();
			}

			@Override
			public int getDrawable() {
				return R.drawable.refresh;
			}
		});
		
		super.setupActionBar(addHomeButton);

	}

	@Override
	protected void onResume() {
		super.onResume();

		newsProvider_.refreshIfNeeded();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		newsProvider_.removeNewsListener(this);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.news, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
			case R.id.news_menu_settings:
				Intent intent = new Intent(this, NewsPreference.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				return true;
	
			default:
				return super.onOptionsItemSelected(item);
		}
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
					adapter_.setClickedItem(position);
					l.setSelection(position);

					Tracker.getInstance().trackPageView("news/previewItem");
				}
			}
		});
	}

	/**
	 * Show a news because someone clicked on the mainscreen
	 * @param id ID of the news, this is actually the position in the adapter
	 */
	private void showNews(int id) {
		adapter_.setClickedItem(id);
		final ListView l = (ListView) findViewById(R.id.news_list_list);
		l.setSelection(id);

		Log.d(this.getClass().toString(), "Show news " + id);
		Tracker.getInstance().trackPageView("news/fromMainscreen");
	}

	@Override
	public void newsRefreshing() {
		actionBar_.setProgressBarVisibility(View.VISIBLE);
	}

	@Override
	public void newsRefreshed() {
		actionBar_.setProgressBarVisibility(View.GONE);
	}

	@Override
	public void getNews(Context ctx, final ICallback callback) {
		final NewsProvider np = NewsProvider.getInstance(ctx);

		// Number of news to display
		int count = np.getCount();
		
		if(count < 1) {
			np.addNewsListener(new INewsListener() {
				@Override
				public void newsRefreshing() {}
				
				@Override
				public void newsRefreshed() {
					callMainscreen(np, callback);
					np.removeNewsListener(this);
				}
			});
			
			np.refreshIfNeeded();
		} else {
			callMainscreen(np, callback);
		}
	}
	
	private void callMainscreen(NewsProvider np, ICallback callback) {
		ArrayList<MainscreenNews> l = new ArrayList<MainscreenNews>();
		
		int count = np.getCount();
		int min = Math.min(count, NB_NEWS);
		
		NewsItem tmp;
		for(int i = 0; i < min; ++i) {
			tmp = np.getItem(i);
			l.add(new MainscreenNews(tmp.getTitle(), tmp.getFormatedDescription().subSequence(0, 150).toString(), i, this, tmp.getPubDateDate()));
		}
		
		callback.callback(l);
	}


}
