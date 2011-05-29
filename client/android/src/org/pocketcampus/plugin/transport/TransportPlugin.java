package org.pocketcampus.plugin.transport;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import org.pocketcampus.R;
import org.pocketcampus.core.communication.RequestHandler;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.core.plugin.IAllowsID;
import org.pocketcampus.core.plugin.ICallback;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.plugin.logging.Tracker;
import org.pocketcampus.plugin.mainscreen.IMainscreenNewsProvider;
import org.pocketcampus.plugin.mainscreen.MainscreenNews;
import org.pocketcampus.plugin.transport.request.ConnectionsRequest;
import org.pocketcampus.shared.plugin.transport.QueryConnectionsResult;
import org.pocketcampus.shared.utils.DateUtils;
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
public class TransportPlugin extends PluginBase implements IMainscreenNewsProvider, IAllowsID  {
	public static final String REFERENCE_DESTINATION = "Ecublens VD, EPFL";
	
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
		transportDisplayManager_.setupSummaryList((Map<String, String>) commonDestPrefs_.getAll());
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

		case R.id.transport_menu_revert:
			transportDisplayManager_.switchDirection();
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

	@SuppressWarnings("unchecked")
	@Override
	public void getNews(final Context ctx, final ICallback callback) {
		SharedPreferences commonDestPrefs = ctx.getSharedPreferences("CommonDestPrefs", 0);
		
		if(commonDestPrefs == null)
			return;
		
		final TransportPlugin that = this;
		final int destNum = 0;
		
		Map<String, String> commonDestinations = (Map<String, String>) commonDestPrefs.getAll();
		for(final String destination : commonDestinations.values()) {
			
			class SummaryConnectionsRequest extends ConnectionsRequest {
				@Override
				protected void handleConnections(QueryConnectionsResult connections) {
					if(connections==null || connections.connections.size()<3) {
						return;
					}
					
					String lessThanAMinute = (String) ctx.getResources().getText(R.string.transport_lessThanAMinute);
					String to = (String) ctx.getResources().getText(R.string.transport_to);
					String departure = (String) ctx.getResources().getText(R.string.transport_departures_short);
					String in = (String) ctx.getResources().getText(R.string.transport_in);
					String nextOne = (String) ctx.getResources().getText(R.string.transport_nextone);
					String then = (String) ctx.getResources().getText(R.string.transport_then);

					Date departureTime = connections.connections.get(0).departureTime;
					String nextDepartures = " "+in+" " + DateUtils.formatDateDelta(new Date(), departureTime, lessThanAMinute);
					
					String next1 = DateUtils.formatDateDelta(new Date(), connections.connections.get(1).departureTime, lessThanAMinute);
					String next2 = DateUtils.formatDateDelta(new Date(), connections.connections.get(2).departureTime, lessThanAMinute);
					String followingDepartures = nextOne+" "+in+" " + next1 +", "+then+" "+in+" " + next2 + ".";
					
					MainscreenNews newsObj = new MainscreenNews(departure+" "+to+" "+destination + nextDepartures, followingDepartures, destNum, that, new Date());
					
					final ArrayList<MainscreenNews> news = new ArrayList<MainscreenNews>();
					news.add(newsObj);
					callback.callback(news);
				}
			} 

			RequestParameters params = new RequestParameters();
			params.addParameter("from", REFERENCE_DESTINATION);
			params.addParameter("to", destination);

			requestHandler_.execute(new SummaryConnectionsRequest(), "connections", params);
		}
		
	}
	
	public static void makeToast(int textId) {
		Notification.showToast(context_, context_.getResources().getString(textId));
	}
}













