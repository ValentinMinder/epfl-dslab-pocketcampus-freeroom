package org.pocketcampus.plugin.transport.android;

import java.util.ArrayList;
import java.util.Map;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledDoubleLayout;
import org.pocketcampus.android.platform.sdk.ui.list.ListViewElement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;

/**
 * The edit view of the Transport Plugin. Displays the list of current
 * destinations as well as a field "add destination" in order to let the user
 * edit his preferred destinations.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * @author Pascal <pascal.scheiben@epfl.ch>
 * @author Florian <florian.laurent@epfl.ch>
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
	private/* Labeled */ListViewElement mAddView;
	/** The list of current preferred destinations */
	private ListViewElement mListView;
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
	 * On display. Called when first displaying the view. Retrieves the model
	 * and the controller and ...
	 */
	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {
		mController = (TransportController) controller;
		mModel = (TransportModel) mController.getModel();

		mDestPrefs = getSharedPreferences(DEST_PREFS_NAME, 0);
		mDestPrefsEditor = mDestPrefs.edit();

		/** Layout */
		mLayout = new StandardTitledDoubleLayout(this);
		mLayout.setTitle(getResources().getString(
				R.string.transport_edit_destinations));

		/** Add some */
		ArrayList<String> l = new ArrayList<String>();
		l.add(getResources().getString(R.string.transport_add_destination));
		mAddView = new ListViewElement(this, l);
		RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		mAddView.setLayoutParams(p);
		mAddView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent i = new Intent(getApplicationContext(),
						TransportAddView.class);
				startActivity(i);
				finish();
			}
		});

		mLayout.addFirstLayoutFillerView(mAddView);

		/** Already there */
		ArrayList<String> list = new ArrayList<String>();
		Map<String, Integer> prefs = (Map<String, Integer>) mDestPrefs.getAll();
		if (prefs != null) {
			for (String s : prefs.keySet()) {
				list.add(s);
				Log.d("TRANSPORT", s + " was in the preferences.");
			}
		}

		mListView = new ListViewElement(this, list);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				//Remove from prefs and remove from the list
			}

		});
		mLayout.addSecondLayoutFillerView(mListView);

		setContentView(mLayout);
	}
}
