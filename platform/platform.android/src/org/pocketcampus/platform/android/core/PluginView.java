package org.pocketcampus.platform.android.core;

import static org.pocketcampus.platform.android.core.GlobalContext.GA_EVENT_CATEG;

import java.util.ArrayList;

import org.pocketcampus.R;
import org.pocketcampus.platform.android.core.PluginController.ControllerBinder;
import org.pocketcampus.platform.android.tracker.GATracker;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;

/**
 * Base class for all plugins. Gives access to action bar and logging
 * functionalities.
 * Gives a way to handle Intents, and to disable the activity title.
 * 
 * @author Florian <florian.laurent@epfl.ch>
 * @author Amer <amer.chamseddine@epfl.ch>
 */
public abstract class PluginView extends Activity {

	private RelativeLayout actionBarLayout;
	private ActionBar mActionBar;
	private ServiceConnection mServiceConnection;
	private ArrayList<PluginController> mControllers = new ArrayList<PluginController>();
	private boolean mActionBarDisabled = false;
	private boolean mActionBarLayoutWrapContent = false;

	public interface ViewBoundCallback {
		void onViewBound(PluginController controller);
	}

	protected boolean getController(
			Class<? extends PluginController> controllerClass,
			ViewBoundCallback callback) {
		Intent intent = new Intent(getApplicationContext(), controllerClass);
		mServiceConnection = makeServiceConnection(callback);
		return bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
	}

	protected void releaseController(PluginController controller) {
		if (controller == null)
			return;
		controller.getModel().removeListener(this);
	}

	/**
	 * Creates a connection to this <code>Activity</code>'s <code>Service</code>
	 * , and calls <code>onReady</code> when it's done. Crashes if it fails.
	 * 
	 * If there's no <code>Service</code> to be run for this
	 * <code>Activity</code>, override this method and ignore
	 * <code>getControllerClass</code> and <code>onReady</code>.
	 */
	@Override
	protected final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		onPreCreate();

		Class<? extends Service> controllerClass = getMainControllerClass();

		final Intent thisIntent = getIntent();

		// no controller to connect to
		if (controllerClass == null) {
			onDisplay(savedInstanceState, null);
			handleIntent(thisIntent);
			return;
		}

		Intent intent = new Intent(getApplicationContext(), controllerClass);

		ViewBoundCallback callback = new ViewBoundCallback() {
			@Override
			public void onViewBound(PluginController controller) {
				onDisplay(savedInstanceState, controller);
				handleIntent(thisIntent);
			}
		};

		mServiceConnection = makeServiceConnection(callback);

		boolean isBound = bindService(intent, mServiceConnection,
				BIND_AUTO_CREATE);

		if (!isBound) {
			throw new RuntimeException("View couldn't bind to service! "
					+ intent);
		}
	}

	/**
	 * Handles the case when an activity receives an Intent while already
	 * started and redirects the intent to handleIntent
	 */
	@Override
	protected final void onNewIntent(Intent intent) {
		handleIntent(intent);
	}

	/**
	 * 
	 * Calling <code>super.onDislay</code> is not necessary.
	 * 
	 * @param savedInstanceState
	 * @param controller
	 */
	protected abstract void onDisplay(Bundle savedInstanceState,
			PluginController controller);
	
	protected abstract String screenName();
	
	public void trackEvent(String action, String label) {
		GATracker.getInstance().sendEvent(GA_EVENT_CATEG, screenName() + "-" + action, label, null);
	}

	/**
	 * Called when the activity is started after receiving an intent.
	 * Normally, if the activity was previously created,
	 * onNewIntent receives the intent, otherwise,
	 * onCreate receives it.
	 * In both cases if you want to handle that intent
	 * just override this function
	 */
	protected void handleIntent(Intent intent) {
	}

	/**
	 * Called from onCreate before creating anything
	 * Here we can do things like disabling Activity title
	 */
	protected void onPreCreate() {
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Register a request activity listener that shows a spinner in the
		// ActionBar when a request is running.
		RequestActivityListener activityListener = new RequestActivityListener() {
			@Override
			public void requestsChanged(int count) {
				if (mActionBar == null) {
					return;
				}

				mActionBar.setProgressBarVisibility(count == 0 ? View.GONE : View.VISIBLE);
			}

		};

		GlobalContext globalContext = (GlobalContext) getApplicationContext();
		globalContext.setRequestActivityListener(activityListener);
		
		// update the action bar in case a request is already running from another plugin
		activityListener.requestsChanged(globalContext.getRequestsCount());
		
		GATracker.getInstance().sendScreen(screenName());
	}

	/**
	 * Called when the <code>Activity</code> is created and the connection to
	 * the <code>Service</code> is established.
	 * 
	 * @param savedInstanceState
	 * @param controller
	 */
	// protected abstract void onDisplay(Bundle savedInstanceState,
	// PluginController controller);

	/**
	 * Creates the <code>ServiceConnection</code> used between this
	 * <code>Activity</code> and its <code>Service</code>. Also takes care of
	 * registering the View with the Model.
	 * 
	 * @param savedInstanceState
	 * @return
	 */
	private ServiceConnection makeServiceConnection(
			final ViewBoundCallback callback) {
		return new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName className,
					IBinder service) {
				PluginController controller = ((ControllerBinder) service)
						.getController();
				controller.getModel().addListener(PluginView.this);
				mControllers.add(controller);
				callback.onViewBound(controller);
			}

			@Override
			public void onServiceDisconnected(ComponentName className) {
				System.out.println("Service crashed!");
				// should never be seen as it's running in the same thread
			}
		};
	}

	/**
	 * Override method to automatically add the ActionBar.
	 */
	@Override
	public void setContentView(int layoutResID) {
		setContentView(layoutResID, null);
	}

	/**
	 * Method to automatically add the ActionBar.
	 */
	public void setContentView(int layoutResID, LayoutParams params) {
		LayoutInflater inflater = getLayoutInflater();
		View view = inflater.inflate(layoutResID, null);
		setupActionBar(view,  params);
	}

	/**
	 * @see #setContentView(int)
	 */
	@Override
	public void setContentView(View view) {
		setupActionBar(view, null);
	}

	/**
	 * @see #setContentView(int)
	 */
	@Override
	public void setContentView(View view, LayoutParams params) {
		setupActionBar(view, params);
	}

	// TODO addContentView!!

	/**
	 * Adds the given view as the Activity content, with the ActionBar at the
	 * top.
	 * 
	 * @param view
	 */
	private void setupActionBar(View view, LayoutParams layoutParams) {
		if (mActionBarDisabled) {
			if(layoutParams == null)
				super.setContentView(view);
			else
				super.setContentView(view, layoutParams);
			return;
		}
		
		if(actionBarLayout == null) {
			LayoutInflater inflater = getLayoutInflater();
			int layout = (mActionBarLayoutWrapContent ? R.layout.sdk_actionbar_layout_wrap : R.layout.sdk_actionbar_layout);
			View actionBarView = inflater.inflate(layout, null);
			super.setContentView(actionBarView);
			actionBarLayout = (RelativeLayout) actionBarView.findViewById(R.id.sdk_actionbar_layout);
			mActionBar = (ActionBar) actionBarView.findViewById(R.id.sdk_actionbar_layout_actionbar);
			mActionBar.setTitle(getString(R.string.app_name));
		}
		
		actionBarLayout.removeAllViews();
		if(layoutParams == null) {
			int param = (mActionBarLayoutWrapContent ? LayoutParams.WRAP_CONTENT : LayoutParams.FILL_PARENT);
			layoutParams = new LayoutParams(param, param);
		}
		actionBarLayout.addView(view, layoutParams);
	}
	
	/**
	 * Adds an action to the ActionBar.
	 * @param action The Action to add.
	 */
	protected void addActionToActionBar(Action action) {
		mActionBar.addAction(action);
	}

	/**
	 * Adds an action to the ActionBar.
	 * @param action The Action to add.
	 * @param index The position where to add it.
	 */
	protected void addActionToActionBar(Action action, int index) {
		mActionBar.addAction(action, index);
	}
	
	/**
	 * Removes an Action from the ActionBar.
	 * @param action The Action to be removed.
	 */
	protected void removeActionFromActionBar(Action action) {
		mActionBar.removeAction(action);
	}
	
	/**
	 * Removes an Action from the ActionBar.
	 * @param index The index of the Action to be removed.
	 */
	protected void removeActionFromActionBar(int index) {
		mActionBar.removeActionAt(index);
	}
	
	/**
	 * Removes all the Actions from the ActionBar.
	 */
	protected void removeAllActionsFromActionBar() {
		mActionBar.removeAllActions();
	}
	
	/**
	 * Sets title of ActionBar.
	 */
	protected void setActionBarTitle(CharSequence title) {
		mActionBar.setTitle(title);
	}
	
	/**
	 * Do not setup the ActionBar for this Activity. Must be called before
	 * <code>setContentView</code>.
	 * 
	 * @param enabled
	 */
	protected void disableActionBar() {
		mActionBarDisabled = true;
	}
	
	/**
	 * Call this if you want to set ActionBar
	 * but you want the layout to wrap contents,
	 * e.g., in dialog popups.
	 * Must be called before
	 * <code>setContentView</code>.
	 */
	protected void makeActionBarLayoutWrapContent() {
		mActionBarLayoutWrapContent = true;
	}

	@Override
	protected void onDestroy() {
		// removes all the Model listeners
		for (PluginController controller : mControllers) {
			controller.getModel().removeListener(this);
		}

		// releases the service connection
		if (mServiceConnection != null) {
			unbindService(mServiceConnection);
		}

		super.onDestroy();
	}

	/**
	 * Specifies which class to use as this View's Controller. May return null
	 * if no Controller is necessary.
	 * 
	 * @return
	 */
	protected Class<? extends Service> getMainControllerClass() {
		return null;
	}
}
