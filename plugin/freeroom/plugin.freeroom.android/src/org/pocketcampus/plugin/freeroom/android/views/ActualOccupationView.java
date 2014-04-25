package org.pocketcampus.plugin.freeroom.android.views;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.plugin.freeroom.android.FreeRoomAbstractView;
import org.pocketcampus.plugin.freeroom.android.FreeRoomController;
import org.pocketcampus.plugin.freeroom.android.FreeRoomModel;
import org.pocketcampus.plugin.freeroom.android.adapter.ActualOccupationArrayAdapter;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.shared.ActualOccupation;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.ImWorkingRequest;
import org.pocketcampus.plugin.freeroom.shared.Occupancy;
import org.pocketcampus.plugin.freeroom.shared.WorkingOccupancy;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * // TODO: NEW INTERFACE as of 2014.04.04
 * <p>
 * <code>ActualOccupationView</code> is the UI that display the actual
 * occupation of a specific room.
 * <p>
 * 
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 */
public class ActualOccupationView extends FreeRoomAbstractView implements
		IFreeRoomView {

	private FreeRoomController mController;
	private FreeRoomModel mModel;

	private StandardTitledLayout mLayout;
	private LinearLayout mGlobalSubLayout;

	private ActualOccupationArrayAdapter<ActualOccupation> mAdapterOcc;

	private ListView mAutoCompleteSuggestionListView;

	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return FreeRoomController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {
		// Tracker
		Tracker.getInstance().trackPageView("freeroom/checkoccupancy/search");

		// Get and cast the controller and model
		mController = (FreeRoomController) controller;
		mModel = (FreeRoomModel) controller.getModel();

		initializeView();
	}

	@Override
	public void initializeView() {
		// Setup the layout
		mLayout = new StandardTitledLayout(this);
		mGlobalSubLayout = new LinearLayout(this);
		mGlobalSubLayout.setOrientation(LinearLayout.VERTICAL);

		mAutoCompleteSuggestionListView = new ListView(this);
		mGlobalSubLayout.addView(mAutoCompleteSuggestionListView);
		// mLayout.setTitle(getString(R.string.freeroom_title_occupancy_search));
		mLayout.hideTitle();

		// TODO: this view is useless and must be deleted
//		mAdapterOcc = new ActualOccupationArrayAdapter<ActualOccupation>(
//				getApplicationContext(), mModel.getDisplayedOccupancy()
//						.getOccupancy(), mModel);
		mAutoCompleteSuggestionListView.setAdapter(mAdapterOcc);

		final IFreeRoomView view = this;
		mAutoCompleteSuggestionListView
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						Occupancy mOccupancy = mModel.getDisplayedOccupancy();
						FRPeriod mFrPeriod = mOccupancy.getOccupancy()
								.get(arg2).getPeriod();
						WorkingOccupancy work = new WorkingOccupancy(mFrPeriod,
								mOccupancy.getRoom());
						// TODO: insert a proper hash!
						String hash = new BigInteger(130, new SecureRandom())
								.toString(32);
						ImWorkingRequest request = new ImWorkingRequest(work,
								hash);
						mController.prepareImWorking(request);
						mController.ImWorking(view);
					}
				});

		mLayout.addFillerView(mGlobalSubLayout);

		// The ActionBar is added automatically when you call setContentView

		setContentView(mLayout);
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
	}

	@Override
	public void occupancyResultsUpdated() {
		// we do nothing here
	}
}
