package org.pocketcampus.android.platform.sdk.core;

import java.util.ArrayList;

import org.pocketcampus.android.platform.sdk.R;
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
	
	/**
	 * Adds the given view as the Activity content, with the ActionBar at the top. 
	 * @param view
	 */
	private void setupActionBar(View view) {
		LayoutInflater inflater = getLayoutInflater();
		View actionBarView = inflater.inflate(R.layout.sdk_actionbar_layout, null);
		super.setContentView(actionBarView);

		RelativeLayout actionBarLayout = (RelativeLayout) actionBarView.findViewById(R.id.sdk_actionbar_layout);
		actionBarLayout.addView(view);

		ActionBar actionBar = (ActionBar) actionBarView.findViewById(R.id.sdk_actionbar_layout_actionbar);
		actionBar.setTitle(getString(R.string.app_name));
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

