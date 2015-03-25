package org.pocketcampus.plugin.news.android;

import java.util.Arrays;
import java.util.Map;

import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginView;
import org.pocketcampus.platform.android.ui.adapter.LazyAdapter;
import org.pocketcampus.platform.android.ui.adapter.MultiListAdapter;
import org.pocketcampus.platform.android.utils.Preparated;
import org.pocketcampus.platform.android.utils.Preparator;
import org.pocketcampus.platform.android.utils.ScrollStateSaver;
import org.pocketcampus.plugin.news.R;
import org.pocketcampus.plugin.news.android.iface.INewsView;
import org.pocketcampus.plugin.news.shared.NewsFeedItemContent;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

/**
 * EventDetailView - View that shows an Event details.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class NewsItemView extends PluginView implements INewsView {

	private NewsController mController;
	private NewsModel mModel;
	
	public static final String EXTRAS_KEY_NEWSITEMID = "newsItemId";
//	public static final String QUERYSTRING_KEY_EVENTITEMID = "eventItemId";
//    public final static String MAP_KEY_EVENTPOOLID = "EVENT_POOL_ID";
//    public final static String MAP_KEY_EVENTPOOLTITLE = "EVENT_POOL_TITLE";
//    public final static String MAP_KEY_EVENTPOOLCLICKLINK = "EVENT_POOL_CLICKLINK";  
	
	private ListView mList;
	
	private int newsItemId;
//	private List<Long> displayedPools = new LinkedList<Long>();
	
	ScrollStateSaver scrollState;
	
	@Override
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
		setContentView(R.layout.news_main2);
		mList = (ListView) findViewById(R.id.news_main_list2);
		

		setActionBarTitle(getString(R.string.news_plugin_title));
	}

	/**
	 * Handles the intent that was used to start this plugin.
	 * 
	 * We need to read the Extras.
	 */
	@Override
	protected void handleIntent(Intent aIntent) {
		newsItemId = 0;
		if(aIntent != null) {
			Bundle aExtras = aIntent.getExtras();
			if(aExtras != null && aExtras.containsKey(EXTRAS_KEY_NEWSITEMID)) {
				newsItemId = Integer.parseInt(aExtras.getString(EXTRAS_KEY_NEWSITEMID));
				System.out.println("Started with intent to display news item " + newsItemId);
			}
		}
		if(newsItemId == 0) {
			finish();
			return;
		}
		
		mController.requestItemContents(this, newsItemId, false);
	}

	@Override
	protected String screenName() {
		return "/news/item";
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(scrollState != null)
			scrollState.restore(mList);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(mList != null)
			scrollState = new ScrollStateSaver(mList);
	}
	

	@Override
	public void gotFeeds() {
		
	}

	@Override
	public void gotContents() {
	
		final NewsFeedItemContent contents = mModel.getItemContents();
		
		// update action bar
		removeAllActionsFromActionBar();
		addActionToActionBar(new com.markupartist.android.widget.Action() {
			public void performAction(View view) {
				trackEvent("Share", null);
				Intent sendIntent = new Intent(Intent.ACTION_SEND);
				sendIntent.putExtra(Intent.EXTRA_TEXT, contents.getLink());
				sendIntent.setType("text/plain");
				startActivity(sendIntent);
			}
			public int getDrawable() {
				return R.drawable.sdk_share;
			}
			@Override
			public String getDescription() {
				return getString(R.string.sdk_share);
			}
		});
		addActionToActionBar(new com.markupartist.android.widget.Action() {
			public void performAction(View view) {
				trackEvent("ViewInBrowser", null);
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(contents.getLink()));
				startActivity(browserIntent);
			}
			public int getDrawable() {
				return R.drawable.news_globe;
			}
			@Override
			public String getDescription() {
				return getString(R.string.news_view_in_browser);
			}
		});
		
		// create our list and custom adapter
		MultiListAdapter adapter = new MultiListAdapter();
		
	
		Preparated<NewsFeedItemContent> p1 = new Preparated<NewsFeedItemContent>(Arrays.asList(contents), new Preparator<NewsFeedItemContent>() {
			public int[] resources() {
				return new int[] { R.id.news_item_image, R.id.news_item_title, R.id.news_item_feed_name, R.id.news_item_details, R.id.news_item_big_image, R.id.news_item_body };
			}
			public Object content(int res, NewsFeedItemContent e) {
				String picUrl;
				switch (res) {
				case R.id.news_item_image:
					return -1;
//					picUrl = NewsController.getResizedPicUrl(e.getImageUrl(), 12);
//					if(picUrl == null)
//						return -1;
//					return picUrl;
				case R.id.news_item_title:
					return e.getTitle();
				case R.id.news_item_feed_name:
					return e.getFeedName();
				case R.id.news_item_details:
					return null;
//					return "<a href=\"" + e.getLink() + "\">Open in Browser</a>";
				case R.id.news_item_big_image:
					picUrl = NewsController.getResizedPicUrl(e.getImageUrl(), 60);
					if(picUrl == null)
						return -1;
					return picUrl;
				case R.id.news_item_body:
					System.out.println(e.getContent());
					return NewsController.sanitizeContents(e.getContent());
				default:
					return null;
				}
			}
			public void finalize(Map<String, Object> map, NewsFeedItemContent item) {
				map.put(LazyAdapter.NOT_SELECTABLE, "1");
				map.put(LazyAdapter.LINK_CLICKABLE, "1");
			}
		});
		adapter.addSection( new LazyAdapter(this, p1.getMap(),
				R.layout.news_item_details, p1.getKeys(), p1.getResources())  );
		

//		if(parentEvent.isSetChildrenPools() && parentEvent.getChildrenPools().size() > 0) {
//			LinkedList<EventPool> eventPools = new LinkedList<EventPool>();
//			displayedPools.clear();
//			for(long poolId : parentEvent.getChildrenPools()){
//				EventPool pool = mModel.getEventPool(poolId);
//				if(pool == null)
//					continue;
//				displayedPools.add(poolId);
//				eventPools.add(pool);
//			}
//			Collections.sort(eventPools, getEventPoolComp4sort());
//			Preparated<EventPool> p4 = new Preparated<EventPool>(eventPools, new Preparator<EventPool>() {
//				public int[] resources() {
//					return new int[] { R.id.event_title, R.id.event_place, R.id.event_thumbnail };
//				}
//				public String content(int res, EventPool e) {
//					switch (res) {
//					case R.id.event_title:
//						return e.getPoolTitle();
//					case R.id.event_place:
//						return e.getPoolPlace();
//					case R.id.event_thumbnail:
//						return e.getPoolPicture();
//					default:
//						return null;
//					}
//				}
//				public void finalize(Map<String, Object> map, EventPool item) {
//					if(item.isSetOverrideLink()) {
//						map.put(MAP_KEY_EVENTPOOLCLICKLINK, item.getOverrideLink());
//					} else {
//						map.put(MAP_KEY_EVENTPOOLID, item.getPoolId() + "");
//						map.put(MAP_KEY_EVENTPOOLTITLE, item.getPoolTitle());
//					}
//				}
//			});
//			adapter.addSection( new LazyAdapter(this, p4.getMap(),
//					R.layout.event_pool_row, p4.getKeys(), p4.getResources()));
//		}

	
		mList.setAdapter(adapter);
		mList.setScrollingCacheEnabled(false);
		
		mList.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
		
//		mList.setOnItemClickListener(new OnItemClickListener() {
//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//				Object o = arg0.getItemAtPosition(arg2);
//				if(o instanceof Map<?, ?> && ((Map<?, ?>) o).containsKey(MAP_KEY_EVENTPOOLCLICKLINK)) {
//					Intent i = new Intent(Intent.ACTION_VIEW);
//					i.setData(Uri.parse(((Map<?, ?>) o).get(MAP_KEY_EVENTPOOLCLICKLINK).toString()));
//					NewsItemView.this.startActivity(i);
//				} else if(o instanceof Map<?, ?> && ((Map<?, ?>) o).containsKey(MAP_KEY_EVENTPOOLID)) {
//					String eId = ((Map<?, ?>) o).get(MAP_KEY_EVENTPOOLID).toString();
//					String eTitle = ((Map<?, ?>) o).get(MAP_KEY_EVENTPOOLTITLE).toString();
//					Intent i = new Intent(NewsItemView.this, NewsMainView.class);
//					i.putExtra(EXTRAS_KEY_EVENTPOOLID, eId);
//					NewsItemView.this.startActivity(i);
//					trackEvent("ShowEventPool", eId + "-" + eTitle);
//				} else {
//					Toast.makeText(getApplicationContext(), o.toString(), Toast.LENGTH_SHORT).show();
//				}
//			}
//		});
		
		
		
	}
	
	
	@Override
	public void networkErrorCacheExists() {
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.sdk_connection_no_cache_yes), Toast.LENGTH_SHORT).show();
		mController.requestItemContents(this, newsItemId, true);
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
