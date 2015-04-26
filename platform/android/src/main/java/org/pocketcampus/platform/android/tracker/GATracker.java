package org.pocketcampus.platform.android.tracker;

import static org.pocketcampus.platform.android.core.PCAndroidConfig.PC_ANDR_CFG;
import android.content.Context;

import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GAServiceManager;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;

/**
 * The Tracker used to send Google analytics about what the user uses in the
 * Application.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 */
public class GATracker {

	/** The tracker used in the whole application. */
	private static GATracker mTracker = new GATracker();

	/**
	 * Returns the global tracker.
	 * 
	 * @return mTracker the global tracker
	 */
	public static GATracker getInstance() {
		return mTracker;
	}

	/**
	 * The GoogleTracker used here to call methods to send data to the google
	 * analytics.
	 */
	private Tracker mGoogleTracker = null;

	/**
	 * Dispatch interval in seconds
	 */
	private final int DISPATCH_PERIOD = 10;

	/**
	 * Starts the Tracker checking if the application is in debug or release
	 * mode.
	 * 
	 * @param context
	 *            The application context.
	 */
	@SuppressWarnings("deprecation")
	public void start(Context context) {
		try {
			mGoogleTracker = GoogleAnalytics.getInstance(context).getTracker(
					PC_ANDR_CFG.getString("GA_TRACKING_CODE"));
			GAServiceManager.getInstance().setLocalDispatchPeriod(
					DISPATCH_PERIOD);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Tracks a page view and send the data to the google analytics.
	 * 
	 * @param pageView
	 *            The tag of the page view.
	 */
	public void sendScreen(String pageView) {
		if(pageView == null)
			return;
		try {
			// Sending the same screen view hit using MapBuilder.createAppView()
			mGoogleTracker.send(MapBuilder.createAppView()
					.set(Fields.SCREEN_NAME, pageView).build());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Tracks an event and send the data to the google analytics. There are 4
	 * different tags to set when you track an event.
	 * 
	 * @param category
	 *            The name you supply for the group of objects you want to
	 *            track.
	 * @param action
	 *            A string that is uniquely paired with each category, and
	 *            commonly used to define the type of user interaction for the
	 *            web object.
	 * @param label
	 *            An optional string to provide additional dimensions to the
	 *            event data.
	 * @param value
	 *            An integer that you can use to provide numerical data about
	 *            the user event.
	 */
	public void sendEvent(String category, String action, String label,
			Long value) {
		try {
			// MapBuilder.createEvent().build() returns a Map of event fields
			// and values that are set and sent with the hit.
			mGoogleTracker.send(MapBuilder.createEvent(category, action, label,
					value).build());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Dispatches queued hits (view, events, or transactions) to Google
	 * Analytics if a network connection is available, and the local dispatching
	 * service is in use.
	 */
	@SuppressWarnings("deprecation")
	public void dispatch() {
		try {
			GAServiceManager.getInstance().dispatchLocalHits();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
