package org.pocketcampus.plugin.pushnotif.android;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.plugin.pushnotif.GCMIntentService;
import org.pocketcampus.plugin.pushnotif.android.iface.IPushNotifView;
import static org.pocketcampus.android.platform.sdk.core.PCAndroidConfig.PC_ANDR_CFG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;

/**
 * PushNotifMainView - Main view that shows PushNotif courses.
 * 
 * This is the main view in the PushNotif Plugin.
 * It checks if the user is logged in, if not it pings
 * the Authentication Plugin.
 * When it gets back a valid SessionId it fetches the
 * user's PushNotif data.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class PushNotifMainView extends PluginView implements IPushNotifView {

	private PushNotifController mController;
	private PushNotifModel mModel;
	
	private StandardTitledLayout mLayout;
	    
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return PushNotifController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		//Tracker
		Tracker.getInstance().trackPageView("pushnotif");
		
		// Get and cast the controller and model
		mController = (PushNotifController) controller;
		mModel = (PushNotifModel) controller.getModel();

		// Setup the layout
		mLayout = new StandardTitledLayout(this);

		// The ActionBar is added automatically when you call setContentView
		setContentView(mLayout);
		//mLayout.hideTitle();

		
		
        // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(this);
        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        GCMRegistrar.checkManifest(this);

        final String regId = GCMRegistrar.getRegistrationId(this);
        if (regId.equals("")) {
			Log.v("DEBUG", "PushNotifMainView::onDisplay not reg with gcm");
            // Automatically registers application on startup.
            GCMRegistrar.register(this, PC_ANDR_CFG.getString("GCM_SENDER_ID"));
        } else {
			Log.v("DEBUG", "PushNotifMainView::onDisplay reg with gcm");
            // Device is already registered on GCM, check server.
			mController.setRegistrationId(regId);
            if (GCMRegistrar.isRegisteredOnServer(this)) {
    			Log.v("DEBUG", "PushNotifMainView::onDisplay reg with PC");
                // Skips registration.
            } else {
    			Log.v("DEBUG", "PushNotifMainView::onDisplay not reg with PC");
                // Try to register again, but not in the UI thread.
                // It's also necessary to cancel the thread onDestroy(),
                // hence the use of AsyncTask instead of a raw thread.
            	mController.getTequilaToken();
            }        	
        }
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.pushnotif_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		/*if(item.getItemId() == R.id.pushnotif_menu_events) {
			Intent i = new Intent(this, PushNotifEventsView.class);
			startActivity(i);
		}*/
		return true;
	}
	
	@Override
	public void networkErrorHappened() {
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.pushnotif_connection_error_happened), Toast.LENGTH_SHORT).show();
	}

}
