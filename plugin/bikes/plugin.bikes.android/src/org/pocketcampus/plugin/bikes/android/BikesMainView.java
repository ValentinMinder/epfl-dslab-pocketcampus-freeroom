package org.pocketcampus.plugin.bikes.android;

import java.util.ArrayList;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.PCSectionedList.PCEmptyLayoutItem;
import org.pocketcampus.android.platform.sdk.ui.PCSectionedList.PCEntryAdapter;
import org.pocketcampus.android.platform.sdk.ui.PCSectionedList.PCItem;
import org.pocketcampus.android.platform.sdk.ui.PCSectionedList.PCSectionItem;
import org.pocketcampus.android.platform.sdk.ui.labeler.ILabeler;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.plugin.bikes.android.iface.IBikesView;
import org.pocketcampus.plugin.bikes.android.ui.BikesStationDialog;
import org.pocketcampus.plugin.bikes.shared.BikeEmplacement;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;

/**
 * Initial and only view on the bikes plugin.
 * Composed of a list containing a specific layout with four <code>textView</code>
 * to have a aligned final layout.
 * 
 * @author Pascal <pascal.scheiben@gmail.com>
 */
public class BikesMainView extends PluginView implements IBikesView {

	/** Controller for this view*/
	private BikesController mController;
	/** Model for this view*/
	private BikesModel mModel;

	/** List containing each of the <code>BikeEmplacement</code>*/
	private ListView mList;
	/** Global layout for this view */
	private StandardTitledLayout mLayout;

	/** Listener for the clicks on the list*/
	private OnItemClickListener oicl;

	/**
	 * Prepare the view to receive the data.
	 */
	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {
		//Tracker
		Tracker.getInstance().trackPageView("bikes");
		
		// Get and cast the controller and model
		mController = (BikesController) controller;
		mModel = (BikesModel) controller.getModel();

		mLayout = new StandardTitledLayout(this);
		mLayout.hideTitle();
		setContentView(mLayout);

		mController.getAvailableBikes();
		mLayout.setText(getString(R.string.bikes_loading));

		setUpActionBar();

		oicl = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View arg1, int pos,
					long arg3) {
				PCItem item = (PCItem) adapter.getItemAtPosition(pos);
				if (item.isEmptyLayout()) {
//					String msg = "";

					// make this a little less ugly, getting the relative layout
					// to get first textview using the hadcoded id and finally
					// converting the charSequence to a string
					String stationsName = ((TextView) ((PCEmptyLayoutItem) adapter
							.getItemAtPosition(pos)).getLayout()
							.findViewById(4)).getText().toString();
					
					for (BikeEmplacement be : mModel.getAvailablesBikes()) {
						if (be.name.equals(stationsName)) {
							
							//old code to show a toast instead of a popup dialog
//							String ab;
//							if (be.numberOfAvailableBikes == 1)
//								ab = getString(R.string.bikes_available_bike);
//							else
//								ab = getString(R.string.bikes_available_bikes);
//
//							String ep;
//							if (be.numberOfEmptySpaces == 1)
//								ep = getString(R.string.bikes_empty_slot);
//							else
//								ep = getString(R.string.bikes_empty_slots);
//
//							msg = be.name
//									+
//									// " is at:\n" +
//									// "Lat: " + be.geoLat + "\n" +
//									// "Lon: " + be.geoLng + "\n" +
//									"\n" + getString(R.string.bikes_has) + " "
//									+ be.numberOfAvailableBikes + " " + ab
//									+ "\n" + getString(R.string.bikes_and)
//									+ " " + be.numberOfEmptySpaces + " " + ep;

							
							BikesStationDialog dialog = new BikesStationDialog(
									BikesMainView.this, be);
							dialog.show();

//							Tracker
							Tracker.getInstance().trackPageView("bikes/home/dialog/" + stationsName);
							// exiting the loop
							break;
						}
					}
				}
			}

		};

	}
	
	/**
	 * Defines what the main controller is for this view. This is optional, some view may not need
	 * a controller (see for example the dashboard).
	 * 
	 * This is only a shortcut for what is done in <code>getOtherController()</code> below: if you know you'll
	 * need a controller before doing anything else in this view, you can define it as you're main controller so you
	 * know it'll be ready as soon as <code>onDisplay()</code> is called.
	 */
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return BikesController.class;
	}

	/**
	 * Method to refresh all the data of the <code>BikeEmplacement</code>
	 */
	private void displayData() {

		if (mModel.getAvailablesBikes().size() > 0)
			mLayout.setText("");

		ArrayList<PCItem> items = new ArrayList<PCItem>();
		items.add(new PCSectionItem(getString(R.string.bikes_velopass),
				getString(R.string.bikes_Available)));

		for (BikeEmplacement be : mModel.getAvailablesBikes()) {
			String nbBikes;
			int q = be.numberOfAvailableBikes;
			int pl = be.numberOfEmptySpaces + q;
			nbBikes = "" + q;

			nbBikes = nbBikes + " / ";

			if (pl < 10)
				nbBikes = nbBikes + " " + pl;
			else
				nbBikes = nbBikes + pl;

			if (pl > 0) {
				RelativeLayout listElement = new RelativeLayout(this);
//				int textAppearanceID = R.style.PocketCampusTheme_Primary_Title;
				int layoutsWidth = 40;
				float textSize = 18f;
				// bikeEmplacement name
				TextView titleView = new TextView(this);
				titleView.setText(be.name);
				titleView.setId(4);
				// titleView.setTextAppearance(this, textAppearanceID);
				titleView.setTextSize(textSize);
				LayoutParams titleParams = new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
				titleParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				titleParams.setMargins(15, 3, 3, 3);
				listElement.addView(titleView, titleParams);

				TextView totalPlacesView = new TextView(this);
				totalPlacesView.setText(pl + "");
				totalPlacesView.setGravity(Gravity.RIGHT);
				// totalPlacesView.setTextAppearance(this, textAppearanceID);
				totalPlacesView.setTextSize(textSize);
				LayoutParams totalParams = new LayoutParams(layoutsWidth,
						LayoutParams.FILL_PARENT);
				totalParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				totalParams.setMargins(3, 3, 15, 3);
				totalPlacesView.setId(3);
				listElement.addView(totalPlacesView, totalParams);

				TextView slashView = new TextView(this);
				slashView.setText("/");
				slashView.setGravity(Gravity.RIGHT);
				// slashView.setTextAppearance(this, textAppearanceID);
				slashView.setTextSize(textSize);
				slashView.setId(2);
				LayoutParams slashParams = new LayoutParams(layoutsWidth / 3,
						LayoutParams.FILL_PARENT);
				slashParams.addRule(RelativeLayout.LEFT_OF,
						totalPlacesView.getId());
				slashParams.setMargins(3, 3, 3, 3);
				listElement.addView(slashView, slashParams);

				TextView bikesAvailableView = new TextView(this);
				bikesAvailableView.setText(q + "");
				bikesAvailableView.setGravity(Gravity.RIGHT);
				// bikesAvailableView.setTextAppearance(this, textAppearanceID);
				bikesAvailableView.setTextSize(textSize);
				bikesAvailableView.setId(1);
				LayoutParams availableParams = new LayoutParams(layoutsWidth,
						LayoutParams.FILL_PARENT);
				availableParams.addRule(RelativeLayout.LEFT_OF,
						slashView.getId());
				availableParams.setMargins(3, 3, 3, 3);
				listElement.addView(bikesAvailableView, availableParams);

				items.add(new PCEmptyLayoutItem(listElement));
			}
		}

		PCEntryAdapter adapter = new PCEntryAdapter(this, items);

		mList = new ListView(this);
		mList.setOnItemClickListener(oicl);
		mList.setAdapter(adapter);

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		mLayout.setLayoutParams(layoutParams);
		mList.setLayoutParams(layoutParams);
		mLayout.addFillerView(mList);

	}

	/**
	 * Prepare the action bar 
	 */
	private void setUpActionBar() {
		ActionBar a = getActionBar();
		if (a != null) {
			RefreshAction refresh = new RefreshAction();
			a.addAction(refresh, 0);
		}
	}

	/**
	 * Inner action class to be added to the action bar.
	 * Implements the ActionBar.Action interfce
	 */
	private class RefreshAction implements Action {

		/**
		 * The constructor which doesn't do anything
		 */
		RefreshAction() {
		}

		/**
		 * Returns the resource for the icon of the button in the action bar
		 */
		@Override
		public int getDrawable() {
			return R.drawable.sdk_action_bar_refresh;
		}

		/**
		 * Defines what is to be performed when the user clicks on the button in
		 * the action bar
		 */
		@Override
		public void performAction(View view) {
//			Tracker
			Tracker.getInstance().trackPageView("bikes/actionbar/refresh");
			
			if(mList != null && mLayout != null && mController != null){
				mList.invalidateViews();
				mLayout.removeFillerView();
				mLayout.setText(getResources().getString(R.string.bikes_loading));
				mController.getAvailableBikes();
			}
		}
	}

	ILabeler<BikeEmplacement> labeler = new ILabeler<BikeEmplacement>() {
		@Override
		public String getLabel(BikeEmplacement obj) {
			String nice;
			nice = obj.name + " " + obj.numberOfAvailableBikes;
			return nice;
		}
	};

	/**
	 * Called by the model when something is wrong with the network.
	 * Display a centered error message.
	 */
	@Override
	public void networkErrorHappened() {
		//Tracker
		Tracker.getInstance().trackPageView("bikes/network_error");
		
		mLayout.setText(getString(R.string.bikes_try_again_later));
	}

	/**
	 * Called by the model when the results of the <code>BikeRequest</code>is updated.
	 * Displays of refreshes the data.
	 */
	@Override
	public void bikeListUpdated() {
		mLayout.removeFillerView();
		displayData();
	}

}
