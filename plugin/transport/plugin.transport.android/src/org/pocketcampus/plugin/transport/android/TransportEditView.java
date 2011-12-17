package org.pocketcampus.plugin.transport.android;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.adapter.StandardArrayAdapter;
import org.pocketcampus.android.platform.sdk.ui.dialog.StyledDialog;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledDoubleSeparatedLayout;
import org.pocketcampus.android.platform.sdk.ui.list.ListViewElement;

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
	/** The main layout */
	private StandardTitledDoubleSeparatedLayout mLayout;
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
	@SuppressWarnings("unchecked")
	private void setUpLayout() {
		// Layout
		mLayout = new StandardTitledDoubleSeparatedLayout(this);
		mLayout.setFirstTitle(getResources().getString(
				R.string.transport_add_destination));
		mLayout.setSecondTitle(getResources().getString(
				R.string.transport_remove_destinations));

		// Field to add a new destination
		ArrayList<String> l = new ArrayList<String>();
		l.add(getResources().getString(R.string.transport_new_destination));
		mAddView = new ListViewElement(this, l);
		RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		mAddView.setLayoutParams(p);
		// Click Listener
		mAddView.setOnItemClickListener(new OnItemClickListener() {

			/**
			 * Defines what is to be performed when the user clicks on the
			 * "New destination" field of the list.
			 */
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

		// Destinations that are already there
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
		mListView = new ListViewElement(this, list);
		// Click Listener
		mListView.setOnItemClickListener(new OnItemClickListener() {

			/**
			 * Defines what is to be performed when the user clicks on a
			 * destination of th.e list
			 */
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
		b.setMessage(Html
				.fromHtml(getResources()
						.getString(
								R.string.transport_confirmation_delete_destination_start)
						+ " <b>"
						+ dest
						+ "</b> "
						+ getResources()
								.getString(
										R.string.transport_confirmation_delete_destination_end)));

		b.setPositiveButton(getResources().getString(R.string.transport_yes),
				new OnClickListener() {

					/**
					 * Defines what is to be performed when the user clicks on
					 * the "Yes" button of the dialog.
					 */
					@SuppressWarnings("unchecked")
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						// Remove the destination and update the list
						mDestPrefsEditor.remove(dest);
						mDestPrefsEditor.commit();

						List<String> list = new ArrayList<String>();
						Map<String, Integer> prefs = (Map<String, Integer>) mDestPrefs
								.getAll();
						if (prefs != null) {
							for (String s : prefs.keySet()) {
								list.add(s);
							}
						}
						Collections.sort(list, new StringComparator());

						// Update the list view
						StandardArrayAdapter adapter = new StandardArrayAdapter(
								getApplicationContext(), list);
						mListView.setAdapter(adapter);
						mListView.invalidate();

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
					 * Defines what is to be performed when the user clicks on
					 * the "Yes" button of the dialog.
					 */
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						// Do nothing
						dialog.dismiss();
					}
				});

		// Create and display the dialog
		StyledDialog d = b.create();
		d.show();
	}

	/**
	 * Compares Restaurants according to their names.
	 */
	private class StringComparator implements Comparator<String> {

		/**
		 * Compares two string alphabetically
		 */
		@Override
		public int compare(String arg0, String arg1) {
			return arg0.compareToIgnoreCase(arg1);
		}

	}
}
