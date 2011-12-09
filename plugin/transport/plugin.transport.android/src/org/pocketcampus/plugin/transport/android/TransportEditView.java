package org.pocketcampus.plugin.transport.android;

import java.util.Map;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledDoubleLayout;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;

/**
 * The edit view of the Transport Plugin. Displays the list of current
 * destinations as well as a field "add destination" in order to let the user
 * edit his preferred destinations.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * @author Pascal <pascal.scheiben@epfl.ch>
 * @author FLorian <florian.laurent@epfl.ch>
 */
public class TransportEditView extends PluginView {
	/* MVC */
	/** The plugin controller */
	private TransportController mController;
	/** The plugin model */
	private TransportModel mModel;
	/** The main layout */
	private StandardTitledDoubleLayout mLayout;
	/** The first one-element-ListView to add a destination */

	/** The list of current preferred destinations */

	/* Preferences */
	/** The pointer to access and modify preferences stored on the phone */
	private SharedPreferences mDestPrefs;
	/** Interface to modify values in SharedPreferences object */
	private Editor mDestPrefsEditor;
	/** The name under which the preferences are stored on the phone */
	private static final String DEST_PREFS_NAME = "TransportDestinationsPrefs";

	/**
	 * Defines what the main controller is for this view.
	 */
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return TransportController.class;
	}

	/**
	 * On display. Called when first displaying the view Retrieve the model,
	 * controller and the preferences and calls onDisplay to create the layout.
	 * Then it calls the method that creates the destinations list filled with
	 * the preferred destinations of the user
	 */
	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {
		mController = (TransportController) controller;
		mModel = (TransportModel) mController.getModel();

		mDestPrefs = getSharedPreferences(DEST_PREFS_NAME, 0);
		mDestPrefsEditor = mDestPrefs.edit();

		/** Testing ... */
		Map<String, Integer> prefs = (Map<String, Integer>) mDestPrefs.getAll();
		if (prefs != null) {
			for(String s : prefs.keySet()){
				Log.d("TRANSPORT",s + " was in the preferences.");
			}
		}

		mLayout = new StandardTitledDoubleLayout(this);
		mLayout.setTitle(getResources().getString(
				R.string.transport_edit_destinations));

		setContentView(mLayout);
	}
	
	/**
	 * 
	 */
	@Override
	public void onBackPressed(){
		super.onBackPressed();
		
	}

}
