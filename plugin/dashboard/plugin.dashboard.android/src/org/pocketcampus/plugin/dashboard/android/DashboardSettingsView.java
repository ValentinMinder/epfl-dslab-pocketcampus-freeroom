package org.pocketcampus.plugin.dashboard.android;

import java.util.Arrays;
import java.util.Map;

import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginView;
import org.pocketcampus.platform.android.ui.adapter.LazyAdapter;
import org.pocketcampus.platform.android.ui.adapter.SeparatedListAdapter;
import org.pocketcampus.platform.android.utils.Preparated;
import org.pocketcampus.platform.android.utils.Preparator;
import org.pocketcampus.plugin.dashboard.R;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

/**
 * 
 * DashboardSettingsView
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class DashboardSettingsView extends PluginView {

	private ListView mList;

	public static final String MAP_KEY_INTENT = "MAP_KEY_INTENT";
	public static final String MAP_KEY_FALLBACK_INTENT = "MAP_KEY_FALLBACK_INTENT";
	public static final String MAP_KEY_TRACKING_TEXT = "MAP_KEY_TRACKING_TEXT";
	
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return DashboardController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {

		setContentView(R.layout.dashboard_settings);
		
		mList = (ListView) findViewById(R.id.dashboard_settings_list);
		
		updateDisplay();
	}
	
	
	private void updateDisplay() {

		
		ListEntry authItem = new ListEntry(getString(R.string.dashboard_accounts), 
				new Intent(Intent.ACTION_VIEW, Uri.parse("pocketcampus://authentication.plugin.pocketcampus.org/status")), 
				null, 
				R.drawable.dashboard_tequila, 
				"OpenAuthentication");
		
		ListEntry aboutItem = new ListEntry(getString(R.string.dashboard_about), 
				new Intent(this, DashboardAboutView.class), 
				null, 
				R.drawable.dashboard_about, 
				"OpenAbout");
		
		ListEntry playStoreItem = new ListEntry(getString(R.string.dashboard_rate), 
				new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=org.pocketcampus")), 
				new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=org.pocketcampus")), 
				R.drawable.dashboard_rate_on_store, 
				"RateOnStore");
		
		ListEntry facebookItem = new ListEntry(getString(R.string.dashboard_like),
				new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/188616577853493")), 
				new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/188616577853493")), 
				R.drawable.dashboard_like_on_fb, 
				"LikeOnFacebook");
		
		ListEntry websiteItem = new ListEntry(getString(R.string.dashboard_website), 
				new Intent(Intent.ACTION_VIEW, Uri.parse("http://pocketcampus.epfl.ch")), 
				null, 
				R.drawable.app_icon, 
				"ViewWebsite");
		
		
		SeparatedListAdapter adapter = new SeparatedListAdapter(this, R.layout.dashboard_settings_list_header);

		Preparated<ListEntry> p = new Preparated<ListEntry>(Arrays.asList(authItem, aboutItem, playStoreItem, facebookItem, websiteItem), new Preparator<ListEntry>() {
			public int[] resources() {
				return new int[] { R.id.dashboard_settings_item_title, R.id.dashboard_settings_item_thumbnail };
			}
			public Object content(int res, final ListEntry e) {
				switch (res) {
				case R.id.dashboard_settings_item_title:
					return e.title;
				case R.id.dashboard_settings_item_thumbnail:
					return e.thumbnail;
				default:
					return null;
				}
			}
			public void finalize(Map<String, Object> map, ListEntry item) {
				map.put(MAP_KEY_INTENT, item.intent);
				map.put(MAP_KEY_FALLBACK_INTENT, item.fallbackIntent);
				map.put(MAP_KEY_TRACKING_TEXT, item.trackingText);
			}
		});
		adapter.addSection(getString(R.string.dashboard_settings), new LazyAdapter(this, p.getMap(), 
				R.layout.dashboard_settings_list_row, p.getKeys(), p.getResources()));
		


		mList.setAdapter(adapter);
		
		mList.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
		
		mList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Object o = arg0.getItemAtPosition(arg2);
				if(o instanceof Map<?, ?>) {
					Intent intent = (Intent) ((Map<?, ?>) o).get(MAP_KEY_INTENT);
					Intent fallbackIntent = (Intent) ((Map<?, ?>) o).get(MAP_KEY_FALLBACK_INTENT);
					String trackingText = ((Map<?, ?>) o).get(MAP_KEY_TRACKING_TEXT).toString();
					trackEvent(trackingText, null);
					try {
						DashboardSettingsView.this.startActivity(intent);
					} catch (ActivityNotFoundException e) {
						if(fallbackIntent != null)
							DashboardSettingsView.this.startActivity(fallbackIntent);
					}
				} else {
					Toast.makeText(getApplicationContext(), o.toString(), Toast.LENGTH_SHORT).show();
				}
			}
		});
		
	}
	
	@Override
	protected String screenName() {
		return "/dashboard/settings";
	}
	
	
	
	/***
	 * HELPERS
	 */
	
	public static class ListEntry {
		String title;
		Intent intent;
		Intent fallbackIntent;
		int thumbnail;
		String trackingText;
		public ListEntry(String title, Intent intent, Intent fallbackIntent, int thumbnail, String trackingText) {
			this.title = title;
			this.intent = intent;
			this.fallbackIntent = fallbackIntent;
			this.thumbnail = thumbnail;
			this.trackingText = trackingText;
		}
	}
	
}