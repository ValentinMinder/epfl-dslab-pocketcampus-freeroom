package org.pocketcampus.android.platform.sdk.core;

import java.util.ArrayList;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController.ControllerBinder;

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

/**
 * Base class for all plugins. Gives access to action bar and logging functionalities.
 * TODO way to disable the action bar
 * 
 * @author Florian
 */
public abstract class PluginView extends Activity {

	private ActionBar mActionBar;
	private ServiceConnection mServiceConnection;
	private ArrayList<PluginController> mControllers = new ArrayList<PluginController>();
	private boolean mActionBarDisabled = false;
	
	public interface ViewBoundCallback {
		void onViewBound(PluginController controller);
	}

	protected boolean getController(Class<? extends PluginController> controllerClass, ViewBoundCallback callback) {
		Intent intent = new Intent(getApplicationContext(), controllerClass);
		mServiceConnection = makeServiceConnection(callback);
		return bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
	}

	protected void releaseController(PluginController controller) {
		if(controller == null) return;
		controller.getModel().removeListener(this);
	}

	/**
	 * Creates a connection to this <code>Activity</code>'s <code>Service</code>, and calls <code>onReady</code> when it's done. 
	 * Crashes if it fails.
	 * 
	 * If there's no <code>Service</code> to be run for this <code>Activity</code>, override this method and ignore <code>getControllerClass</code> and <code>onReady</code>.
	 */
	@Override
	protected final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Class<? extends Service> controllerClass = getMainControllerClass();

		// no controller to connect to
		if(controllerClass == null) {
			onDisplay(savedInstanceState, null);
			return;
		}

		Intent intent = new Intent(getApplicationContext(), controllerClass);

		ViewBoundCallback callback = new ViewBoundCallback() {
			@Override
			public void onViewBound(PluginController controller) {
				onDisplay(savedInstanceState, controller);
			}
		};

		mServiceConnection = makeServiceConnection(callback);

		boolean isBound = bindService(intent, mServiceConnection, BIND_AUTO_CREATE);

		if(!isBound) {
			throw new RuntimeException("View couldn't bind to service! " + intent);
		}
	}

	/**
	 * 
	 * Calling <code>super.onDislay</code> is not necessary.
	 * @param savedInstanceState
	 * @param controller
	 */
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {}

	@Override
	protected void onResume() {
		super.onResume();
		
		// Register a request activity listener that shows a spinner in the ActionBar when a request is running.
		RequestActivityListener activityListener = new RequestActivityListener() {
			@Override
			public void requestStarted() {
				if(mActionBar == null) {
					return;
				}
				
				mActionBar.setProgressBarVisibility(View.VISIBLE);
			}

			@Override
			public void requestStopped() {
				if(mActionBar == null) {
					return;
				}
				
				mActionBar.setProgressBarVisibility(View.GONE);
			}
		};
		
		GlobalContext globalContext = (GlobalContext) getApplicationContext();
		globalContext.setRequestActivityListener(activityListener);
	}
	
	/**
	 * Called when the <code>Activity</code> is created and the connection to the <code>Service</code> is established.
	 * @param savedInstanceState
	 * @param controller
	 */
	//	protected abstract void onDisplay(Bundle savedInstanceState, PluginController controller);

	//	/**
	//	 * Event triggered by the Model when something goes wrong with the network.
	//	 */
	//	public abstract void onNetworkError(); TODO remove?

	/**
	 * Creates the <code>ServiceConnection</code> used between this <code>Activity</code> and its <code>Service</code>.
	 * Also takes care of registering the View with the Model.
	 * @param savedInstanceState
	 * @return
	 */
	private ServiceConnection makeServiceConnection(final ViewBoundCallback callback) {
		return new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName className, IBinder service) {
				PluginController controller = ((ControllerBinder) service).getController();
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
	 * Overriden method to automatically add the ActionBar.
	 */
	@Override
	public void setContentView(int layoutResID) {
		LayoutInflater inflater = getLayoutInflater();
		View view = inflater.inflate(layoutResID, null);
		setupActionBar(view);
	}

	/**
	 * @see #setContentView(int)
	 */
	@Override
	public void setContentView(View view) {
		setupActionBar(view);
	}

	/**
	 * @see #setContentView(int)
	 */
	@Override
	public void setContentView(View view, LayoutParams params) {
		setupActionBar(view);
	}
	
	// TODO addContentView!!
	
	/**
	 * Adds the given view as the Activity content, with the ActionBar at the top. 
	 * @param view
	 */
	private void setupActionBar(View view) {
		if(mActionBarDisabled) {
			super.setContentView(view);
			return;
		}
		
		LayoutInflater inflater = getLayoutInflater();
		View actionBarView = inflater.inflate(R.layout.sdk_actionbar_layout, null);
		super.setContentView(actionBarView);

		LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		RelativeLayout actionBarLayout = (RelativeLayout) actionBarView.findViewById(R.id.sdk_actionbar_layout);
		actionBarLayout.addView(view, layoutParams);

		mActionBar = (ActionBar) actionBarView.findViewById(R.id.sdk_actionbar_layout_actionbar);
		mActionBar.setTitle(getString(R.string.app_name));
	}

	/**
	 * Do not setup the ActionBar for this Activity.
	 * Must be called before <code>setContentView</code>.
	 * @param enabled
	 */
	protected void disableActionBar() {
		mActionBarDisabled  = true;
	}
	
	/**
	 * Returns the ActionBar, use it to add custom button to it.
	 * @return
	 */
	public ActionBar getActionBar() {
		return mActionBar;
	}

	@Override
	protected void onDestroy() {
		// removes all the Model listeners
		for(PluginController controller : mControllers) {
			controller.getModel().removeListener(this);
		}

		// releases the service connection
		if(mServiceConnection != null) {
			unbindService(mServiceConnection);
		}

		super.onDestroy();
	}

	/**
	 * Specifies which class to use as this View's Controller. May return null if no Controller is necessary.
	 * @return
	 */
	protected Class<? extends Service> getMainControllerClass() {return null;}
}

