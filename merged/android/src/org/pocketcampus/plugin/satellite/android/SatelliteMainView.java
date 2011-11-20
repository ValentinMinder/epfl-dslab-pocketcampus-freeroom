package org.pocketcampus.plugin.satellite.android;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardLayout;
import org.pocketcampus.plugin.satellite.android.iface.ISatelliteMainView;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

/**
 * The Main View of the Satellite plugin, first displayed when accessing
 * Satellite.
 * 
 * Displays the beer of the month, a list of all beers, the next events and the
 * affluence at Satellite.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public class SatelliteMainView extends PluginView implements ISatelliteMainView {

	/** Main Activity */
	private Activity mActivity;

	/** Satellite Model */
	private SatelliteModel mModel;

	/** Satellite Controller */
	private SatelliteController mController;

	/** Standard Layout */
	private StandardLayout mLayout;

	/**
	 * Defines what the main controller is for this view.
	 */
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return SatelliteController.class;
	}

	/**
	 * Called once the view is connected to the controller. If you don't
	 * implement <code>getMainControllerClass()</code> then the controller given
	 * here will simply be <code>null</code>.
	 */
	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {
		mActivity = this;
		mController = (SatelliteController) controller;
		mModel = (SatelliteModel) mController.getModel();

		mLayout = new StandardLayout(this);
		setContentView(mLayout);

		displayData();
	}

	/**
	 * Called when this view is accessed after already having been initialized
	 * before
	 */
	@Override
	protected void onRestart() {
		super.onRestart();

	}

	public void displayData() {
		mLayout.setText(getResources().getString(
				R.string.satellite_nothing_to_display));
	}

	/**
	 * Main Satellite Options menu contains access to Beers, Events, Sandwiches
	 * and back to Main Page
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.satellite_menu, menu);
		return true;
	}

	/**
	 * Decides what happens when the options menu is opened and an option is
	 * chosen (what view to display)
	 */
	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		if (item.getItemId() == R.id.satellite_main_page) {

		} else if (item.getItemId() == R.id.satellite_beers) {

		} else if (item.getItemId() == R.id.satellite_events) {

		} else if (item.getItemId() == R.id.satellite_sandwiches) {

		}

		return true;
	}

	@Override
	public void beerUpdated() {

	}

	@Override
	public void beersUpdated() {

	}

	@Override
	public void sandwichesUpdated() {

	}

	@Override
	public void affluenceUpdated() {

	}

	@Override
	public void eventsUpdated() {

	}

	/**
	 * Displays a toast when an error happens upon contacting the server
	 */
	@Override
	public void networkErrorHappened() {
		Toast toast = Toast.makeText(getApplicationContext(),
				getString(R.string.satellite_network_error), Toast.LENGTH_SHORT);
		toast.show();
	}

}