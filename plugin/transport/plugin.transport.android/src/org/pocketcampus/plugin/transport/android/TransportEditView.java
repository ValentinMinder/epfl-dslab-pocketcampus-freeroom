package org.pocketcampus.plugin.transport.android;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.adapter.IconTextArrayAdapter;
import org.pocketcampus.android.platform.sdk.ui.dialog.StyledDialog;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledDoubleSeparatedLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

/**
 * The edit view of the Transport plugin. Displays the list of current stations
 * as well as a field "add station" in order to let the user edit his favorite
 * stations.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * @author Pascal <pascal.scheiben@epfl.ch>
 * @author Florian <florian.laurent@epfl.ch>
 */
public class TransportEditView extends PluginView {
	private Context mContext;
	/** The main layout. */
	private StandardTitledDoubleSeparatedLayout mLayout;
	/** The first one-element-list to add a destination. */
	private ListView mAddList;
	/** The corresponding adapter. */
	private IconTextArrayAdapter mAddAdapter;
	/** The list of current favorite stations. */
	private ListView mDeleteList;
	/** The corresponding adapter. */
	private IconTextArrayAdapter mDeleteListAdapter;

	/** The pointer to access and modify preferences stored on the phone. */
	private SharedPreferences mDestPrefs;
	/** Interface to modify values in the <code>SharedPreferences</code> object. */
	private Editor mDestPrefsEditor;
	/** The name under which the preferences are stored on the phone. */
	private static final String DEST_PREFS_NAME = "TransportDestinationsPrefs";

	/**
	 * Defines what the main controller is for this view.
	 */
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return TransportController.class;
	}

	/**
	 * Called when first displaying the view. Retrieves the model and the
	 * controller and calls the<code>setUpLayout</code> method.
	 */
	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {
		// Tracker
		Tracker.getInstance().trackPageView("transport/editView");

		mContext = getApplicationContext();
		mDestPrefs = getSharedPreferences(DEST_PREFS_NAME, 0);
		mDestPrefsEditor = mDestPrefs.edit();
		// Set up the layout
		setUpLayout();
	}

	/**
	 * Sets up the layout with a standard layout and two lists. One to add a
	 * station, the other one to display the current favorite stations and let
	 * the user remove them.
	 */
	@SuppressWarnings("unchecked")
	private void setUpLayout() {
		// Layout
		mLayout = new StandardTitledDoubleSeparatedLayout(this);
		mLayout.setFirstTitle(getResources().getString(
				R.string.transport_add_station));
		mLayout.setSecondTitle(getResources().getString(
				R.string.transport_remove_stations));

		// Field to add a new destination
		ArrayList<String> l = new ArrayList<String>();
		l.add(getResources().getString(R.string.transport_new_station));

		mAddList = new ListView(this);
		mAddAdapter = new IconTextArrayAdapter(getApplicationContext(), l,
				R.drawable.transport_plus);
		mAddList.setAdapter(mAddAdapter);

		RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		mAddList.setLayoutParams(p);
		// Click Listener
		mAddList.setOnItemClickListener(new OnItemClickListener() {

			/**
			 * When the user clicks on the "New station" field of the list,
			 * opens the <code>TransportAddView Activity</code> to let the user
			 * type a new station.
			 */
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// Tracker
				Tracker.getInstance().trackPageView("transport/editView/add");

				Intent i = new Intent(getApplicationContext(),
						TransportAddView.class);
				startActivity(i);
				finish();
			}
		});
		mLayout.addFirstLayoutFillerView(mAddList);

		// Stations that are already there
		ArrayList<String> list = new ArrayList<String>();
		Map<String, Integer> prefs = (Map<String, Integer>) mDestPrefs.getAll();
		if (prefs != null && !prefs.isEmpty()) {
			for (String s : prefs.keySet()) {
				list.add(s);
			}
		} else {
			mLayout.hideSecondTitle();
		}
		Collections.sort(list);

		// List with stations to remove
		mDeleteList = new ListView(this);
		mDeleteListAdapter = new IconTextArrayAdapter(getApplicationContext(),
				list, R.drawable.transport_minus);
		mDeleteList.setAdapter(mDeleteListAdapter);
		mDeleteList.setOnItemClickListener(new OnItemClickListener() {

			/**
			 * When the user clicks on a station of the list, opens the
			 * confirmation dialog asking if he really wants to remove it from
			 * his favorite stations.
			 */
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// Tracker
				Tracker.getInstance().trackPageView(
						"transport/editView/remove/"
								+ arg0.getItemAtPosition(arg2).toString());

				// Show the confirmation dialog
				confirmationDialog(arg0.getItemAtPosition(arg2).toString());
			}

		});

		mLayout.addSecondLayoutFillerView(mDeleteList);
		setContentView(mLayout);
	}

	/**
	 * Creates and shows a confirmation dialog for removing a station from the
	 * favorite stations.
	 */
	private void confirmationDialog(String s) {
		final String dest = s;
		StyledDialog.Builder b = new StyledDialog.Builder(this);
		b.setCanceledOnTouchOutside(true);
		b.setTitle(getResources().getString(R.string.transport_confirmation));
		b.setMessage(Html.fromHtml(getResources().getString(
				R.string.transport_confirmation_delete_station_start)
				+ " <b>"
				+ dest
				+ "</b> "
				+ getResources().getString(
						R.string.transport_confirmation_delete_station_end)));

		b.setPositiveButton(getResources().getString(R.string.transport_yes),
				new OnClickListener() {

					/**
					 * When the user clicks on the "Yes" button of the dialog,
					 * the station is removed from his favorite ones and the
					 * list is updated.
					 */
					@SuppressWarnings("unchecked")
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						// Tracker
						Tracker.getInstance().trackPageView(
								"transport/editView/dialog/remove");

						// Remove the destination and update the list
						mDestPrefsEditor.remove(dest);
						mDestPrefsEditor.commit();

						ArrayList<String> list = new ArrayList<String>();
						Map<String, Integer> prefs = (Map<String, Integer>) mDestPrefs
								.getAll();
						if (prefs != null) {
							for (String s : prefs.keySet()) {
								list.add(s);
							}
						}
						Collections.sort(list, new StringComparator());

						// Update the list view
						mDeleteListAdapter = new IconTextArrayAdapter(mContext,
								list, R.drawable.transport_minus);
						mDeleteList.setAdapter(mDeleteListAdapter);
						mDeleteList.invalidate();

						if (list.isEmpty()) {
							mLayout.removeSecondLayoutFillerView();
							mLayout.hideSecondTitle();
						}
						dialog.dismiss();
					}
				});

		b.setNegativeButton(getResources().getString(R.string.transport_no),
				new OnClickListener() {

					/**
					 * When the user clicks on the "No" button of the dialog,
					 * nothing happens and the dialog is dismissed.
					 */
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						// Tracker
						Tracker.getInstance().trackPageView(
								"transport/editView/dialog/dontRemove");

						// Do nothing and dismiss the dialog
						dialog.dismiss();
					}
				});

		// Create and display the dialog
		StyledDialog d = b.create();
		d.show();
	}

	/**
	 * Compares stations according to their names.
	 */
	private class StringComparator implements Comparator<String> {

		/**
		 * Compares two string alphabetically.
		 */
		@Override
		public int compare(String arg0, String arg1) {
			return arg0.compareToIgnoreCase(arg1);
		}

	}
}
