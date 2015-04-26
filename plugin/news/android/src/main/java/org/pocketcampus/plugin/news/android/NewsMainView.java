package org.pocketcampus.plugin.news.android;

import static org.pocketcampus.platform.android.utils.DialogUtils.showMultiChoiceDialogSbN;
import static org.pocketcampus.platform.android.utils.SetUtils.difference;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginView;
import org.pocketcampus.platform.android.ui.adapter.LazyAdapter;
import org.pocketcampus.platform.android.ui.adapter.SeparatedListAdapter2;
import org.pocketcampus.platform.android.utils.DialogUtils.MultiChoiceHandler;
import org.pocketcampus.platform.android.utils.Preparated;
import org.pocketcampus.platform.android.utils.Preparator;
import org.pocketcampus.platform.android.utils.ScrollStateSaver;
import org.pocketcampus.plugin.news.R;
import org.pocketcampus.plugin.news.android.iface.INewsView;
import org.pocketcampus.plugin.news.shared.NewsFeed;
import org.pocketcampus.plugin.news.shared.NewsFeedItem;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.markupartist.android.widget.Action;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

/**
 * NewsMainView - Main view that shows list of News.
 * 
 * This is the main view in the News Plugin.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class NewsMainView extends PluginView implements INewsView {

	private NewsController mController;
	private NewsModel mModel;

	// public static final String EXTRAS_KEY_EVENTPOOLID = "eventPoolId";
	// public static final String QUERYSTRING_KEY_EVENTPOOLID = "eventPoolId";
	// public static final String QUERYSTRING_KEY_TICKET = "userTicket";
	// public static final String QUERYSTRING_KEY_EXCHANGETOKEN =
	// "exchangeToken";
	// public static final String QUERYSTRING_KEY_TEMPLATEID = "templateId";
	// public static final String QUERYSTRING_KEY_MARKFAVORITE = "markFavorite";
	public static final String MAP_KEY_NEWSITEMID = "NEWS_ITEM_ID";
	public static final String MAP_KEY_NEWSITEMTITLE = "NEWS_ITEM_TITLE";
	// public static final String MAP_KEY_EVENTITEMTITLE = "EVENT_ITEM_TITLE";
	public static final long MILLISECONDS_DAY = 1000 * 3600 * 24;
	public static final long MILLISECONDS_WEEK = 7 * MILLISECONDS_DAY;
	public static final long MILLISECONDS_MONTH = 30 * MILLISECONDS_DAY;
	public static final long MILLISECONDS_YEAR = 365 * MILLISECONDS_DAY;

	private boolean displayingList;

	private Map<String, String> feedsInRS = new HashMap<String, String>();
	private Set<String> filteredFeeds = new HashSet<String>();
	// private long eventPoolId;
	// private boolean fetchPast = false;
	// private List<Long> newsInRS = new LinkedList<Long>();
	// private Set<Integer> categsInRS = new HashSet<Integer>();
	// private Set<String> tagsInRS = new HashSet<String>();
	//
	// EventPool thisEventPool;
	// Map<String, List<EventItem>> newsByTags;
	// Set<Integer> filteredCategs = new HashSet<Integer>();
	// Set<String> filteredTags = new HashSet<String>();
	//
	StickyListHeadersListView mList;
	ScrollStateSaver scrollState;

	protected Class<? extends PluginController> getMainControllerClass() {
		return NewsController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {

		// Get and cast the controller and model
		mController = (NewsController) controller;
		mModel = (NewsModel) controller.getModel();

		// The ActionBar is added automatically when you call setContentView
		// disableActionBar();

		setActionBarTitle(getString(R.string.news_plugin_title));
	}

	/**
	 * Handles the intent that was used to start this plugin.
	 * 
	 * We need to read the Extras.
	 */
	@Override
	protected void handleIntent(Intent aIntent) {
		mController.requestNewsFeeds(this, false);

	}

	@Override
	protected String screenName() {
		return "/news";
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (displayingList && scrollState != null)
			scrollState.restore(mList);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (displayingList && mList != null)
			scrollState = new ScrollStateSaver(mList);
	}

	@Override
	public void gotFeeds() {
		feedsInRS = new HashMap<String, String>();
		for (NewsFeed i : mModel.getNewsFeeds()) {
			feedsInRS.put(i.getFeedId(), i.getName());
		}

		updateDisplay();
	}

	private void updateFilter() {
		filteredFeeds = difference(feedsInRS.keySet(), mModel.getDislikedFeeds());

	}

	private void updateActionBar() {
		removeAllActionsFromActionBar();
		final int restoFilterIcon = (difference(feedsInRS.keySet(), filteredFeeds).size() == 0 ? R.drawable.pocketcampus_filter
				: R.drawable.pocketcampus_filter_sel);
		if (feedsInRS.size() > 0) {
			addActionToActionBar(new Action() {
				public void performAction(View view) {
					trackEvent("Filter", null);
					showMultiChoiceDialogSbN(NewsMainView.this, feedsInRS, getString(R.string.news_string_filter),
							filteredFeeds, new MultiChoiceHandler<String>() {
								public void saveSelection(String t, boolean isChecked) {
									if (isChecked)
										mModel.removeDislikedFeed(t);
									else
										mModel.addDislikedFeed(t);
									updateDisplay();
								}
							});
				}

				public int getDrawable() {
					return restoFilterIcon;
				}

				@Override
				public String getDescription() {
					return getString(R.string.news_string_filter);
				}
			});
		}

	}

	@Override
	public void gotContents() {
	}

	private void updateDisplay() {

		setContentView(R.layout.news_main);
		mList = (StickyListHeadersListView) findViewById(R.id.news_main_list);
		displayingList = true;

		updateFilter();
		updateActionBar();

		if (displayingList)
			scrollState = new ScrollStateSaver(mList);

		Map<Long, List<NewsFeedItem>> items = new HashMap<Long, List<NewsFeedItem>>();

		final SparseArray<String> reverseMap = new SparseArray<String>();

		for (NewsFeed i : mModel.getNewsFeeds()) {
			if (!filteredFeeds.contains(i.getFeedId()))
				continue;
			for (NewsFeedItem item : i.getItems()) {
				// if(titles.contains(item.getTitle()))
				// continue; // de-duplication // TODO this is not ideal
				// titles.add(item.getTitle());
				reverseMap.put(item.getItemId(), i.getFeedId());
				long timeDiff = System.currentTimeMillis() - item.getDate();
				if (timeDiff < MILLISECONDS_DAY) {
					NewsController.addToMap(items, MILLISECONDS_DAY, item);
				} else if (timeDiff < MILLISECONDS_WEEK) {
					NewsController.addToMap(items, MILLISECONDS_WEEK, item);
				} else if (timeDiff < MILLISECONDS_MONTH) {
					NewsController.addToMap(items, MILLISECONDS_MONTH, item);
				} else if (timeDiff < MILLISECONDS_YEAR) {
					NewsController.addToMap(items, MILLISECONDS_YEAR, item);
				}
			}
		}

		SeparatedListAdapter2 adapter = new SeparatedListAdapter2(this, R.layout.sdk_separated_list_header2);
		// List<NewsFeed> newsFeeds = mModel.getNewsFeeds();
		// Collections.sort(newsFeeds, NewsController.getNewsFeedComp4sort());
		List<Long> keys = new LinkedList<Long>(items.keySet());
		Collections.sort(keys);
		for (final long i : keys) {

			Collections.sort(items.get(i), NewsController.getNewsFeedItemComp4sort());
			Preparated<NewsFeedItem> p = new Preparated<NewsFeedItem>(items.get(i), new Preparator<NewsFeedItem>() {
				public int[] resources() {
					return new int[] { R.id.news_feed_item_title, R.id.news_feed_thumbnail, R.id.news_item_feed_name,
							R.id.news_feed_item_date };
				}

				public Object content(int res, final NewsFeedItem e) {
					switch (res) {
					case R.id.news_feed_item_title:
						return e.getTitle();
					case R.id.news_feed_thumbnail:
						return NewsController.getResizedPicUrl(e.getImageUrl(), 24);
					case R.id.news_item_feed_name:
						return feedsInRS.get(reverseMap.get(e.getItemId()));
					case R.id.news_feed_item_date:
						if (i == MILLISECONDS_DAY)
							return null;
						return new SimpleDateFormat("dd MMM", getResources().getConfiguration().locale)
								.format(new Date(e.getDate()));
					default:
						return null;
					}
				}

				public void finalize(Map<String, Object> map, NewsFeedItem item) {
					map.put(MAP_KEY_NEWSITEMID, "" + item.getItemId());
					map.put(MAP_KEY_NEWSITEMTITLE, item.getTitle());
				}
			});

			LazyAdapter lazyAdapter = new LazyAdapter(this, p.getMap(), R.layout.news_list_row, p.getKeys(),
					p.getResources());
			lazyAdapter.setStubImage(R.drawable.news_icon);
			lazyAdapter.setNoImage(R.drawable.news_icon);
			lazyAdapter.setImageOnFail(R.drawable.news_icon);
			adapter.addSection(getHeaderTitle(i), lazyAdapter);
		}

		// if(newsFeeds.size() == 0) {
		// displayingList = false;
		// StandardLayout sl = new StandardLayout(this);
		// sl.setText(getString(resId));
		// setContentView(sl);
		// } else {
		if (!displayingList) {
			setContentView(R.layout.news_main);
			mList = (StickyListHeadersListView) findViewById(R.id.news_main_list);
			displayingList = true;
		}
		mList.setAdapter(adapter);

		mList.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));

		mList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Object o = arg0.getItemAtPosition(arg2);
				if (o instanceof Map<?, ?>) {
					String eId = ((Map<?, ?>) o).get(MAP_KEY_NEWSITEMID).toString();
					String eTitle = ((Map<?, ?>) o).get(MAP_KEY_NEWSITEMTITLE).toString();
					Intent i = new Intent(NewsMainView.this, NewsItemView.class);
					i.putExtra(NewsItemView.EXTRAS_KEY_NEWSITEMID, eId);
					NewsMainView.this.startActivity(i);
					trackEvent("OpenNewsItem", eId + "-" + eTitle);
				} else {
					Toast.makeText(getApplicationContext(), o.toString(), Toast.LENGTH_SHORT).show();
				}
			}
		});

		if (scrollState != null)
			scrollState.restore(mList);

		// }
	}

	private String getHeaderTitle(long x) {
		if (x == MILLISECONDS_DAY)
			return getString(R.string.news_header_last_day);
		if (x == MILLISECONDS_WEEK)
			return getString(R.string.news_header_last_week);
		if (x == MILLISECONDS_MONTH)
			return getString(R.string.news_header_last_month);
		if (x == MILLISECONDS_YEAR)
			return getString(R.string.news_header_last_year);
		return null;
	}

	// @Override
	// public boolean onPrepareOptionsMenu(Menu menu) {
	// menu.clear();
	// return true;
	// }

	@Override
	public void networkErrorCacheExists() {
		Toast.makeText(getApplicationContext(), getResources().getString(R.string.sdk_connection_no_cache_yes),
				Toast.LENGTH_SHORT).show();
		mController.requestNewsFeeds(this, true);
	}

	@Override
	public void networkErrorHappened() {
		setUnrecoverableErrorOccurred(getString(R.string.sdk_connection_error_happened));
	}

	@Override
	public void newsServersDown() {
		setUnrecoverableErrorOccurred(getString(R.string.sdk_upstream_server_down));
	}

}
