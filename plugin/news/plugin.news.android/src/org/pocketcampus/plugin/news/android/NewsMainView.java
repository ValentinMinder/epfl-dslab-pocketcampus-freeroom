package org.pocketcampus.plugin.news.android;

import static org.pocketcampus.android.platform.sdk.utils.DialogUtils.showMultiChoiceDialogSbN;
import static org.pocketcampus.android.platform.sdk.utils.SetUtils.difference;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.adapter.LazyAdapter;
import org.pocketcampus.android.platform.sdk.ui.adapter.SeparatedListAdapter;
import org.pocketcampus.android.platform.sdk.utils.DialogUtils.MultiChoiceHandler;
import org.pocketcampus.android.platform.sdk.utils.Preparated;
import org.pocketcampus.android.platform.sdk.utils.Preparator;
import org.pocketcampus.android.platform.sdk.utils.ScrollStateSaver;
import org.pocketcampus.plugin.news.R;
import org.pocketcampus.plugin.news.android.iface.INewsView;
import org.pocketcampus.plugin.news.shared.NewsFeed;
import org.pocketcampus.plugin.news.shared.NewsFeedItem;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.markupartist.android.widget.ActionBar.Action;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

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
	
//	public static final String EXTRAS_KEY_EVENTPOOLID = "eventPoolId";
//	public static final String QUERYSTRING_KEY_EVENTPOOLID = "eventPoolId";
//	public static final String QUERYSTRING_KEY_TICKET = "userTicket";
//	public static final String QUERYSTRING_KEY_EXCHANGETOKEN = "exchangeToken";
//	public static final String QUERYSTRING_KEY_TEMPLATEID = "templateId";
//	public static final String QUERYSTRING_KEY_MARKFAVORITE = "markFavorite";
	public static final String MAP_KEY_NEWSITEMID = "NEWS_ITEM_ID";
	public static final String MAP_KEY_NEWSITEMTITLE = "NEWS_ITEM_TITLE";
//	public static final String MAP_KEY_EVENTITEMTITLE = "EVENT_ITEM_TITLE";
	public static final long MILLISECONDS_DAY = 1000 * 3600 * 24;
	public static final long MILLISECONDS_WEEK = 7 * MILLISECONDS_DAY;
	public static final long MILLISECONDS_MONTH = 30 * MILLISECONDS_DAY;
	public static final long MILLISECONDS_YEAR = 365 * MILLISECONDS_DAY;
	
	private boolean displayingList;
	

	private Map<String, String> feedsInRS = new HashMap<String, String>();
	private Set<String> filteredFeeds = new HashSet<String>();
//	private long eventPoolId;
//	private boolean fetchPast = false;
//	private List<Long> newsInRS = new LinkedList<Long>();
//	private Set<Integer> categsInRS = new HashSet<Integer>();
//	private Set<String> tagsInRS = new HashSet<String>();
//	
//	EventPool thisEventPool;
//	Map<String, List<EventItem>> newsByTags;
//	Set<Integer> filteredCategs = new HashSet<Integer>();
//	Set<String> filteredTags = new HashSet<String>();
//	
	ListView mList;
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
		//disableActionBar();
		setContentView(R.layout.news_main);
		mList = (ListView) findViewById(R.id.news_main_list);
		displayingList = true;
		
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
//		eventPoolId = Constants.CONTAINER_EVENT_ID;
//		boolean processedIntent = false;
//		if(aIntent != null) {
//			Bundle aExtras = aIntent.getExtras();
//			Uri aData = aIntent.getData();
//			if(aExtras != null && aExtras.containsKey(EXTRAS_KEY_EVENTPOOLID)) {
//				eventPoolId = Long.parseLong(aExtras.getString(EXTRAS_KEY_EVENTPOOLID));
//				System.out.println("Started with intent to display pool " + eventPoolId);
//				mController.refreshEventPool(this, eventPoolId, fetchPast, false);
//				processedIntent = true;
//			} else if(aData != null && aData.getQueryParameter(QUERYSTRING_KEY_EVENTPOOLID) != null) {
//				eventPoolId = Long.parseLong(aData.getQueryParameter(QUERYSTRING_KEY_EVENTPOOLID));
//				System.out.println("External start with intent to display pool " + eventPoolId);
//				externalCall(aData);
//				processedIntent = true;
//			}
//		}
//		if(!processedIntent)
//			mController.refreshEventPool(this, eventPoolId, fetchPast, false);
		
	}

	@Override
	protected String screenName() {
		return "/news";
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(displayingList && scrollState != null)
			scrollState.restore(mList);
//		if(thisEventPool != null && thisEventPool.isRefreshOnBack())
//			mController.refreshEventPool(this, eventPoolId, fetchPast, false);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(displayingList && mList != null)
			scrollState = new ScrollStateSaver(mList);
	}
	
	
	@Override
	public void gotFeeds() {
		feedsInRS = new HashMap<String, String>();
		for(NewsFeed i : mModel.getNewsFeeds()) {
			feedsInRS.put(i.getFeedId(), i.getName());
		}
		
		updateDisplay();
	}
	
	
	private void updateFilter() {
		filteredFeeds = difference(feedsInRS.keySet(), mModel.getDislikedFeeds());
		
	}

	private void updateActionBar() {
		removeAllActionsFromActionBar();
		final int restoFilterIcon = (difference(feedsInRS.keySet(), filteredFeeds).size() == 0 ? R.drawable.pocketcampus_filter : R.drawable.pocketcampus_filter_sel);
		if(feedsInRS.size() > 0) {
			addActionToActionBar(new Action() {
				public void performAction(View view) {
					trackEvent("Filter", null);
					showMultiChoiceDialogSbN(NewsMainView.this, feedsInRS, getString(R.string.news_string_filter), filteredFeeds, new MultiChoiceHandler<String>() {
						public void saveSelection(String t, boolean isChecked) {
							if(isChecked)
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
			});
		}
		
		
	}
	

	@Override
	public void gotContents() {
	}

	
	private void updateDisplay() {
		
		updateFilter();
		updateActionBar();

		if(displayingList)
			scrollState = new ScrollStateSaver(mList);
		
//		Set<EventItem> filteredNews = new HashSet<EventItem>();
//		for(String tag : filteredTags) {
//			List<EventItem> tagNews = newsByTags.get(tag);
//			if(tagNews == null) // if tag becomes empty (shorter period selected)
//				continue; // then skip it
//			filteredNews.addAll(tagNews);
//		}
//		
//		//Map<Integer, List<EventItem>> newsByCateg = new HashMap<Integer, List<EventItem>>();
//		SparseArray<List<EventItem>> newsByCateg = new SparseArray<List<EventItem>>();
//		
//		
//		for(EventItem e : filteredNews) {
//			if(e.getEventCateg() < 0)
//				filteredCategs.add(e.getEventCateg()); // make sure special categs are always displayed
//			if(newsByCateg.indexOfKey(e.getEventCateg()) < 0)
//				newsByCateg.put(e.getEventCateg(), new LinkedList<EventItem>());
//			newsByCateg.get(e.getEventCateg()).add(e);
//		}
		
		Map<Long, List<NewsFeedItem>> items = new HashMap<Long, List<NewsFeedItem>>();
		
//		Set<String> titles = new HashSet<String>();
		
		final SparseArray<String> reverseMap = new SparseArray<String>();
//		final Map<Integer, String> reverseMap = new HashMap<Integer, String>();
		
		for(NewsFeed i : mModel.getNewsFeeds()) {
			if(!filteredFeeds.contains(i.getFeedId()))
				continue;
			for(NewsFeedItem item : i.getItems()) {
//				if(titles.contains(item.getTitle()))
//					continue; // de-duplication // TODO this is not ideal
//				titles.add(item.getTitle());
				reverseMap.put(item.getItemId(), i.getFeedId());
				long timeDiff = System.currentTimeMillis() - item.getDate();
				if(timeDiff < MILLISECONDS_DAY) {
					NewsController.addToMap(items, MILLISECONDS_DAY, item);
				} else if(timeDiff < MILLISECONDS_WEEK) {
					NewsController.addToMap(items, MILLISECONDS_WEEK, item);
				} else if(timeDiff < MILLISECONDS_MONTH) {
					NewsController.addToMap(items, MILLISECONDS_MONTH, item);
				} else if(timeDiff < MILLISECONDS_YEAR) {
					NewsController.addToMap(items, MILLISECONDS_YEAR, item);
				}
			}
		}
		
		

		
		
		SeparatedListAdapter adapter = new SeparatedListAdapter(this, R.layout.news_list_header);
//		List<NewsFeed> newsFeeds = mModel.getNewsFeeds();
//		Collections.sort(newsFeeds, NewsController.getNewsFeedComp4sort());
		List<Long> keys = new LinkedList<Long>(items.keySet());
		Collections.sort(keys);
		for(final long i : keys) {
			
			Collections.sort(items.get(i), NewsController.getNewsFeedItemComp4sort());
			Preparated<NewsFeedItem> p = new Preparated<NewsFeedItem>(items.get(i), new Preparator<NewsFeedItem>() {
				public int[] resources() {
					return new int[] { R.id.news_feed_item_title, R.id.news_feed_thumbnail, R.id.news_item_feed_name, R.id.news_feed_item_date };
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
						if(i == MILLISECONDS_DAY)
							return null;
						return new SimpleDateFormat("dd MMM", getResources().getConfiguration().locale).format(new Date(e.getDate()));
					default:
						return null;
					}
				}
				public void finalize(Map<String, Object> map, NewsFeedItem item) {
					map.put(MAP_KEY_NEWSITEMID, "" + item.getItemId());
					map.put(MAP_KEY_NEWSITEMTITLE, item.getTitle());
				}
			});
			adapter.addSection(getHeaderTitle(i), new LazyAdapter(this, p.getMap(), 
					R.layout.news_list_row, p.getKeys(), p.getResources()));
		}
		
//		if(newsFeeds.size() == 0) {
//			displayingList = false;
//			StandardLayout sl = new StandardLayout(this);
//			sl.setText(getString(resId));
//			setContentView(sl);
//		} else {
			if(!displayingList) {
				setContentView(R.layout.news_main);
				mList = (ListView) findViewById(R.id.news_main_list);
				displayingList = true;
			}
			mList.setAdapter(adapter);
			
			mList.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
			
			mList.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					Object o = arg0.getItemAtPosition(arg2);
					if(o instanceof Map<?, ?>) {
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
			
			if(scrollState != null)
				scrollState.restore(mList);
			
//		}
	}
	
	
	private String getHeaderTitle(long x) {
		if(x == MILLISECONDS_DAY)
			return getString(R.string.news_header_last_day);
		if(x == MILLISECONDS_WEEK)
			return getString(R.string.news_header_last_week);
		if(x == MILLISECONDS_MONTH)
			return getString(R.string.news_header_last_month);
		if(x == MILLISECONDS_YEAR)
			return getString(R.string.news_header_last_year);
		return null;
	}
	
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		return true;
	}
	
	@Override
	public void networkErrorCacheExists() {
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.sdk_connection_no_cache_yes), Toast.LENGTH_SHORT).show();
		mController.requestNewsFeeds(this, true);
	}
	
	@Override
	public void networkErrorHappened() {
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.sdk_connection_error_happened), Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void newsServersDown() {
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.sdk_upstream_server_down), Toast.LENGTH_SHORT).show();
	}

}
