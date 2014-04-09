package org.pocketcampus.plugin.freeroom.android.views;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.FreeRoomAbstractView;
import org.pocketcampus.plugin.freeroom.android.FreeRoomController;
import org.pocketcampus.plugin.freeroom.android.FreeRoomManageFavoritesView;
import org.pocketcampus.plugin.freeroom.android.FreeRoomModel;
import org.pocketcampus.plugin.freeroom.android.FreeRoomSearchRoomsResultView;
import org.pocketcampus.plugin.freeroom.android.adapter.ExpandableListViewAdapter;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.shared.ActualOccupation;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomRequest;
import org.pocketcampus.plugin.freeroom.shared.ImWorkingRequest;
import org.pocketcampus.plugin.freeroom.shared.Occupancy;
import org.pocketcampus.plugin.freeroom.shared.OccupancyRequest;
import org.pocketcampus.plugin.freeroom.shared.WorkingOccupancy;
import org.pocketcampus.plugin.freeroom.shared.utils.FRTimes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.markupartist.android.widget.ActionBar.Action;

/**
 * // TODO: NEW INTERFACE as of 2014.04.04 TODO THIS MUST BE THE NEW HOME, WHICH
 * IS CURRENTLY UNDER MAINTENANCE FOR REWRITTING COMPLETELY
 * 
 * MainView is the entry of the plugin, displaying the possible menus/features
 * available.
 * <p>
 * 
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 */
public class FreeRoomHomeView extends FreeRoomAbstractView implements
		IFreeRoomView {

	private FreeRoomController mController;
	private FreeRoomModel mModel;

	private LinearLayout mLayout;

	private ExpandableListView mExpView;

	private ExpandableListViewAdapter mExpList;

	/**
	 * TODO: deprecated.
	 * <p>
	 * Request currently displayed.
	 */
	private OccupancyRequest requestDEPRECATED;

	private Action search = new Action() {
		public void performAction(View view) {
			// TODO: this is the future search
			Toast.makeText(getApplicationContext(), "search",
					Toast.LENGTH_SHORT).show();
		}

		public int getDrawable() {
			return R.drawable.magnify2x06;
		}
	};

	private Action editFavorites = new Action() {
		public void performAction(View view) {
			// TODO: new favorites edition
			Toast.makeText(getApplicationContext(), "favorites",
					Toast.LENGTH_SHORT).show();
			Intent i = new Intent(FreeRoomHomeView.this,
					FreeRoomManageFavoritesView.class);
			FreeRoomHomeView.this.startActivity(i);
		}

		public int getDrawable() {
			return R.drawable.pencil2x187;
		}
	};

	private Action gotBackMenu = new Action() {
		public void performAction(View view) {
			Calendar calendar = Calendar.getInstance();
			FreeRoomRequest req = FRTimes.convert(
					calendar.get(Calendar.DAY_OF_WEEK),
					calendar.get(Calendar.HOUR_OF_DAY),
					calendar.get(Calendar.HOUR_OF_DAY) + 1);
			// send the request to the controller
			mController.prepareSearchFreeRoom(req);
			// construct and launch the UI.
			Intent i = new Intent(FreeRoomHomeView.this,
					FreeRoomSearchRoomsResultView.class);
			FreeRoomHomeView.this.startActivity(i);
		}

		public int getDrawable() {
			return R.drawable.wheelchair48;
		}
	};

	private Action refresh = new Action() {
		public void performAction(View view) {
			refresh();
		}

		public int getDrawable() {
			return R.drawable.refresh2x01;
		}
	};

	private Action hideUnhideAllResults = new Action() {
		public void performAction(View view) {
			hideUnHideAllResults();
		}

		public int getDrawable() {
			return R.drawable.freeroom_filter;
		}
	};

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
		mLayout = new LinearLayout(getApplicationContext());

		// The ActionBar is added automatically when you call setContentView
		setContentView(mLayout);
		// mLayout.setTitle(getString(R.string.freeroom_title_main_title));
		// mLayout.hideTitle();

		mExpView = new ExpandableListView(getApplicationContext());
		mLayout.addView(mExpView);
		initializeView();

		init();
		refresh();
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

	@Override
	public void initializeView() {
		mExpList = new ExpandableListViewAdapter<Occupancy>(
				getApplicationContext(), mModel.getOccupancyResults(), mModel);
		mExpView.setAdapter(mExpList);
		addActionToActionBar(hideUnhideAllResults);
		// addActionToActionBar(refresh);
		addActionToActionBar(editFavorites);
		// addActionToActionBar(search);
		addActionToActionBar(gotBackMenu);

		/**
		 * If you click on a completely free room, it will indicate that you're
		 * going to work there.
		 */
		final IFreeRoomView view = this;
		mExpView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {

				Occupancy mOccupancy = mExpList.getChildObject(groupPosition,
						childPosition);

				List<ActualOccupation> list = mOccupancy.getOccupancy();
				if (list.size() > 0) {
					long tss = list.get(0).getPeriod().getTimeStampStart();
					long tse = list.get(list.size() - 1).getPeriod()
							.getTimeStampEnd();
					FRPeriod mPeriod = new FRPeriod(tss, tse, false);
					WorkingOccupancy work = new WorkingOccupancy(mPeriod,
							mOccupancy.getRoom());
					ImWorkingRequest request = new ImWorkingRequest(work);
					mController.prepareImWorking(request);
					mController.ImWorking(view);
					return true;
				}
				return false;
			}
		});

	}

	/**
	 * Inits the request to the current next valid period.
	 */
	private void init() {
		ArrayList<String> array = new ArrayList<String>();
		array.addAll(mModel.getAllRoomMapFavorites().keySet());
		// TODO: deprecated
//		requestDEPRECATED = new OccupancyRequest(array,
//				FRTimes.getNextValidPeriod());
		// new interface
		mModel.setFRRequest(new FRRequest(FRTimes.getNextValidPeriod(), false,
				array));
	}

	/**
	 * Send the set request to the controller.
	 */
	private void refresh() {
		// TODO: deprecated
//		mController.prepareCheckOccupancy(requestDEPRECATED);
//		mController.checkOccupancy(this);

		// new interface
		mController.sendFRRequest(this);
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

		// TODO: deprecated
//		occupancyResultsUpdated();
	}

	private void hideUnHideAllResults() {
		mModel.switchAvailable();
		mExpList.notifyDataSetChanged();
	}

	@Override
	public void occupancyResultsUpdated() {
		mExpList.notifyDataSetChanged();
	}
}