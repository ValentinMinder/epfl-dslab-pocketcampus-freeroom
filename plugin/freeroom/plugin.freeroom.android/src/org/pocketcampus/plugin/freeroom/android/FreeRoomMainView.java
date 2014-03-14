package org.pocketcampus.plugin.freeroom.android;

import java.util.ArrayList;
import java.util.Calendar;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomRequest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * MainView is the entry of the plugin, displaying the possible menus/features
 * available.
 * <p>
 * 
 * @author FreeFroom Project Team - Julien WEBER <julien.weber@epfl.ch> and
 *         Valentin MINDER <valentin.minder@epfl.ch>
 * 
 */
public class FreeRoomMainView extends FreeRoomAbstractView implements
		IFreeRoomView {

	private FreeRoomController mController;
	private FreeRoomModel mModel;

	private StandardTitledLayout mLayout;

	private ListView mList;
	private ArrayList<String> mListValues;

	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return FreeRoomController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {
		// Tracker
		Tracker.getInstance().trackPageView("freeroom");

		// Get and cast the controller and model
		mController = (FreeRoomController) controller;
		mModel = (FreeRoomModel) controller.getModel();

		// Setup the layout
		mLayout = new StandardTitledLayout(this);

		// The ActionBar is added automatically when you call setContentView
		setContentView(mLayout);
		mLayout.setTitle(getString(R.string.freeroom_title_main_title));

		initializeMainView();
	}

	/**
	 * This is called when the Activity is resumed.
	 * 
	 * If the user presses back on the Authentication window, This Activity is
	 * resumed but we do not have the freeroomCookie. In this case we close the
	 * Activity.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		/*
		 * if(mModel != null && mModel.getFreeRoomCookie() == null) { // Resumed
		 * and lot logged in? go back finish(); }
		 */
	}

	private void initializeMainView() {
		mList = new ListView(this);
		LayoutParams p = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		mList.setLayoutParams(p);

		mListValues = new ArrayList<String>();
		mList.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line,
				android.R.id.text1, mListValues));

		mListValues.add(getString(R.string.freeroom_menu_freeroom_now));
		mListValues.add(getString(R.string.freeroom_menu_freeroom_search));
		mListValues.add(getString(R.string.freeroom_menu_check_occupancy));

		mList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent i = null;
				boolean flag = true;
				switch (arg2) {
				case 0:
					Calendar calendar = Calendar.getInstance();
					FreeRoomRequest req = org.pocketcampus.plugin.freeroom.android.utils.Converter
							.convert(calendar.get(Calendar.DAY_OF_WEEK),
									calendar.get(Calendar.HOUR_OF_DAY),
									calendar.get(Calendar.HOUR_OF_DAY) + 1);
					// construct and launch the UI.
					i = new Intent(FreeRoomMainView.this,
							FreeRoomResultView.class);
					FreeRoomMainView.this.startActivity(i);
					// send the request to the controller
					mController.prepareSearchFreeRoom(req);
					flag = false;
					break;
				case 1:
					i = new Intent(FreeRoomMainView.this,
							FreeRoomSearchView.class);
					break;
				case 2:
					i = new Intent(FreeRoomMainView.this,
							FRCheckOccupancySearchView.class);
					break;
				default:
					break;
				}
				if (flag) {
					FreeRoomMainView.this.startActivity(i);
				}
			}

		});
		mLayout.addFillerView(mList);
	}

	@Override
	public void freeRoomResultsUpdated() {
		// we do nothing here
	}

	@Override
	public void autoCompletedUpdated() {
		// we do nothing here
	}

	@Override
	public void occupancyResultUpdated() {
		// we do nothing here
		Log.v("fr-main", "listener to occupancyResultUpdated called");
	}
}
