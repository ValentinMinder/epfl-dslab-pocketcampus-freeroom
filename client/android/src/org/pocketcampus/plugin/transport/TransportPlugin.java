package org.pocketcampus.plugin.transport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.pocketcampus.R;
import org.pocketcampus.core.communication.RequestHandler;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.plugin.logging.Tracker;
import org.pocketcampus.plugin.mainscreen.IMainscreenNewsProvider;
import org.pocketcampus.plugin.mainscreen.MainscreenNews;
import org.pocketcampus.utils.Notification;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * 
 * @author Pascal
 * @author Florian
 *
 */
public class TransportPlugin extends PluginBase implements IMainscreenNewsProvider {
	public static final String REFERENCE_DESTINATION = "Ecublens VD, EPFL";
	public static final String REFERENCE_DESTINATION_SHORTNAME = "EPFL";
	
	private static RequestHandler requestHandler_;

	private static Context context_;
	private TransportDisplayManager transportDisplayManager_;
	
	
	private SharedPreferences commonDestPrefs_;
	private Map<String, String> commonDestinations_;
	
	
	public TransportPlugin() {
		requestHandler_ = getRequestHandler();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transport_main);
		
		transportDisplayManager_ = new TransportDisplayManager(this, requestHandler_);
		context_ = getApplicationContext();
		
		Tracker.getInstance().trackPageView("transport/home");
		
		displaySummary();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		@SuppressWarnings("unchecked")
		Map<String, String> commonDestinationsInPrefs = (Map<String, String>) commonDestPrefs_.getAll();
		
		if(!commonDestinationsInPrefs.equals(commonDestinations_)) {
			displaySummary();
		}
	}

	@SuppressWarnings("unchecked")
	private void displaySummary() {
		commonDestPrefs_ = getSharedPreferences("CommonDestPrefs", 0);
		boolean noDestination = (commonDestPrefs_.getAll().size() == 0);
		transportDisplayManager_.setupSummaryList((Map<String, String>) commonDestPrefs_.getAll(), noDestination);
	}

	@Override
	protected void setupActionBar(boolean addHomeButton) {
		super.setupActionBar(addHomeButton);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.transport, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;

		switch (item.getItemId()) {
		case R.id.transport_menu_settings:
			intent = new Intent(this, TransportPreference.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			return true;

		case R.id.transport_menu_detailed:
//			intent = new Intent(this, TransportDetailed.class);
//			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			startActivity(intent);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public PluginInfo getPluginInfo() {
		return new TransportInfo();
	}

	@Override
	public PluginPreference getPluginPreference() {
		return new TransportPreference();
	}

	public static RequestHandler getTransportRequestHandler() {
		return requestHandler_;
	}

	protected static String getReferenceDestination() {
		return REFERENCE_DESTINATION;
	}

	@Override
	public List<MainscreenNews> getNews(Context ctx) {
		SharedPreferences commonDestPrefs = ctx.getSharedPreferences("CommonDestPrefs", 0);
		ArrayList<MainscreenNews> news = new ArrayList<MainscreenNews>();
		
		if(commonDestPrefs == null) {
			return news;
		}
		
		@SuppressWarnings("unchecked")
		Map<String, String> commonDestinations = (Map<String, String>) commonDestPrefs.getAll();
		int destNum = 0;
		
		for(String destination : commonDestinations.values()) {
			MainscreenNews newsObj = new MainscreenNews(destination, "Departures in 5 minutes.", destNum, this, new Date());
			news.add(newsObj);
			destNum++;
		}
		
		return news;
	}
	
	public static void makeToast(int textId) {
		Notification.showToast(context_, context_.getResources().getString(textId));
	}
}













