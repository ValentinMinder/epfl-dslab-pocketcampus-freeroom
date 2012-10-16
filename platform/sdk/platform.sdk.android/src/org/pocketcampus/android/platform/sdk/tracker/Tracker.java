package org.pocketcampus.android.platform.sdk.tracker;

import android.content.Context;

import static org.pocketcampus.android.platform.sdk.core.PCAndroidConfig.PC_ANDR_CFG;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

/**
 * The Tracker used to send Google analytics about what the user uses in the
 * Application. Each Plugin should call it with a tag of the form
 * "plugin_name/view/action". No private informations (like password, username,
 * etc) are allowed to be tracked though.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 */
public class Tracker {
	/** The tracker used in the whole application. */
	private static Tracker mTracker = new Tracker();
	/**
	 * The GoogleTracker used here to call methods to send data to the google
	 * analytics.
	 */
	private GoogleAnalyticsTracker mGoogleTracker = null;

	private final int DISPATCH_PERIOD = 10;

	/**
	 * Class constructor.
	 * 
	 * Gets an instance of the tracker by calling the
	 * <code>GoogleAnalyticsTracker.getInstance()</code> method.
	 */
	private Tracker() {
		mGoogleTracker = GoogleAnalyticsTracker.getInstance();
	}

	/**
	 * Returns the global tracker.
	 * 
	 * @return mTracker the global tracker
	 */
	public static Tracker getInstance() {
		return mTracker;
	}

	/**
	 * Starts the Tracker checking if the application is in debug or release
	 * mode.
	 * 
	 * @param context
	 *            The application context.
	 */
	public void start(Context context) {
		try {
			mGoogleTracker.startNewSession(PC_ANDR_CFG.getString("GA_TRACKING_CODE"), DISPATCH_PERIOD, context);
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
	public void trackPageView(String pageView) {
		try {
			mGoogleTracker.trackPageView("v3r1/" + pageView);
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
	public void trackEvent(String category, String action, String label,
			int value) {
		try {
			mGoogleTracker.trackEvent(category, action, label, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets a custom variable to track user activity.
	 * 
	 * @param index
	 *            The slot for the custom variable. This is a number whose value
	 *            can range from 1 - 5, inclusive. A custom variable should be
	 *            placed in one slot only and not be re-used across different
	 *            slots.
	 * @param name
	 *            The name for the custom variable. This is a string that
	 *            identifies the custom variable and appears in the top-level
	 *            Custom Variables report of the Analytics reports.
	 * @param value
	 *            The value for the custom variable. This is a string that is
	 *            paired with a name. You can pair a number of values with a
	 *            custom variable name. The value appears in the table list of
	 *            the UI for a selected variable name. Typically, you will have
	 *            two or more values for a given name. For example, you might
	 *            define a custom variable name gender and supply male and
	 *            female as two possible values.
	 */
	public void setCustomVar(int index, String name, String value) {
		try {
			mGoogleTracker.setCustomVar(index, name, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param index
	 *            The slot for the custom variable. This is a number whose value
	 *            can range from 1 - 5, inclusive. A custom variable should be
	 *            placed in one slot only and not be re-used across different
	 *            slots.
	 * @param name
	 *            The name for the custom variable. This is a string that
	 *            identifies the custom variable and appears in the top-level
	 *            Custom Variables report of the Analytics reports.
	 * @param value
	 *            The value for the custom variable. This is a string that is
	 *            paired with a name. You can pair a number of values with a
	 *            custom variable name. The value appears in the table list of
	 *            the UI for a selected variable name. Typically, you will have
	 *            two or more values for a given name. For example, you might
	 *            define a custom variable name gender and supply male and
	 *            female as two possible values.
	 * @param scope
	 *            The scope for the custom variable. As described above, the
	 *            scope defines the level of user engagement with your site. It
	 *            is a number whose possible values are 1 (visitor-level), 2
	 *            (session-level), or 3 (page-level). When left undefined, the
	 *            custom variable scope defaults to page-level interaction.
	 */
	public void setCustomVar(int index, String name, String value, int scope) {
		try {
			mGoogleTracker.setCustomVar(index, name, value, scope);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Stop the Tracker when it is no longer needed.
	 */
	public void stop() {
		try {
			mGoogleTracker.stopSession();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
