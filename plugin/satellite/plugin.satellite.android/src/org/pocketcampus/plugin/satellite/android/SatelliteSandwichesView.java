package org.pocketcampus.plugin.satellite.android;

import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.labeler.ILabeler;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.android.platform.sdk.ui.list.LabeledListViewElement;
import org.pocketcampus.plugin.satellite.android.iface.ISatelliteMainView;
import org.pocketcampus.plugin.satellite.shared.Sandwich;

import android.app.Service;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * The Sandwiches View of the Satellite plugin. Displays the list of sandwiches
 * that Satellite proposes.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 */
public class SatelliteSandwichesView extends PluginView implements ISatelliteMainView {
	/** The Plugin Controller */
	private SatelliteController mController;
	/** The Plugin Model */
	private SatelliteModel mModel;
	/** A Standard Titled Layout */
	private StandardTitledLayout mLayout;

	/** Returns the class of the SatelliteController */
	@Override
	protected Class<? extends Service> getMainControllerClass() {
		return SatelliteController.class;
	}

	/**
	 * Initializes the view for the sandwiches
	 */
	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {
		mController = (SatelliteController) controller;
		mModel = (SatelliteModel) mController.getModel();

		mLayout = new StandardTitledLayout(this);
		mLayout.setTitle(getResources().getString(
				R.string.satellite_menu_sandwiches));
		mLayout.setText(getResources().getString(
				R.string.satellite_nothing_to_display));
		setContentView(mLayout);

		showSandwiches();
	}

	/**
	 * Asks the controller for the sandwiches.
	 */
	private void showSandwiches() {
		mController.getSandwiches();
	}

	/**
	 * Called when the data was updated in the Plugin Model. Displays the
	 * sandwichList in a LabeledListView.
	 */
	public void sandwichesUpdated() {
		Log.d("SATELLITE", "Sandwiches updated (View)");
		mLayout.removeFillerView();

		List<Sandwich> sandwiches = mModel.getSandwiches();

		if (sandwiches != null && !sandwiches.isEmpty()) {
			LabeledListViewElement l = new LabeledListViewElement(this,
					sandwiches, mSandwichLabeler);

			mLayout.hideText();
			mLayout.addFillerView(l);
		}
	}

	/**
	 * Display a toast when a network error happened.
	 */
	@Override
	public void networkErrorHappened() {
		Toast.makeText(this,
				getResources().getString(R.string.satellite_network_error),
				Toast.LENGTH_SHORT).show();
	}

	/**
	 * The labeler for a Sandwich, to tell how it has to be displayed in a
	 * generic view.
	 */
	ILabeler<Sandwich> mSandwichLabeler = new ILabeler<Sandwich>() {

		@Override
		public String getLabel(Sandwich sandwich) {
			return sandwich.getName();
		}

	};

	@Override
	public void beerUpdated() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beersUpdated() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void affluenceUpdated() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void eventsUpdated() {
		// TODO Auto-generated method stub
		
	}


}
