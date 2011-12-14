package org.pocketcampus.plugin.transport.android;

import java.util.ArrayList;
import java.util.Map;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.adapter.StandardArrayAdapter;
import org.pocketcampus.android.platform.sdk.ui.dialog.StyledDialog;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledDoubleLayout;
import org.pocketcampus.android.platform.sdk.ui.list.ListViewElement;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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
	private ListViewElement mAddView;
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
	 * and the controller and the<code>setUpLayout()</code> method.
	 */
	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {
		mController = (TransportController) controller;
		mModel = (TransportModel) mController.getModel();

		mDestPrefs = getSharedPreferences(DEST_PREFS_NAME, 0);
		mDestPrefsEditor = mDestPrefs.edit();

		// Set up the layout
		setUpLayout();
	}

	/**
	 * Sets up the layout with a standard layout and two list view. One to add a
	 * destination, the other one to display the current preferred destination
	 * and let the user edit them.
	 */
	private void setUpLayout() {
		// Layout
		mLayout = new StandardTitledDoubleLayout(this);
		mLayout.setTitle(getResources().getString(
				R.string.transport_edit_destinations));

		// Add some
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

		// Already there
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
				// Show the confirmation dialog

				confirmationDialog(arg0.getItemAtPosition(arg2).toString());
			}

		});
		mLayout.addSecondLayoutFillerView(mListView);

		setContentView(mLayout);
	}

	/**
	 * Creates and shows a confirmation dialog for removing a destination from
	 * the preferred destinations.
	 */
	private void confirmationDialog(String s) {
		final String dest = s;
		StyledDialog.Builder b = new StyledDialog.Builder(this);
		b.setCanceledOnTouchOutside(true);
		b.setTitle(getResources().getString(R.string.transport_confirmation));
		b.setMessage(getResources().getString(
				R.string.transport_confirmation_delete_destination_start)
				+ "\n"
				+ dest
				+ "\n"
				+ getResources().getString(
						R.string.transport_confirmation_delete_destination_end));

		b.setPositiveButton(getResources().getString(R.string.transport_yes),
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						// Remove the destination and update the list
						mDestPrefsEditor.remove(dest);
						mDestPrefsEditor.commit();
						
						ArrayList<String> list = new ArrayList<String>();
						Map<String, Integer> prefs = (Map<String, Integer>) mDestPrefs.getAll();
						if (prefs != null) {
							for (String s : prefs.keySet()) {
								list.add(s);
							}
						}
						
						StandardArrayAdapter adapter = new StandardArrayAdapter(
								getApplicationContext(), list);
						mListView.setAdapter(adapter);
						mListView.invalidate();
						
						dialog.dismiss();
					}
				});

		b.setNegativeButton(getResources().getString(R.string.transport_no),
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						// Do nothing
						dialog.dismiss();
					}
				});

		StyledDialog d = b.create();
		d.show();
	}
}
