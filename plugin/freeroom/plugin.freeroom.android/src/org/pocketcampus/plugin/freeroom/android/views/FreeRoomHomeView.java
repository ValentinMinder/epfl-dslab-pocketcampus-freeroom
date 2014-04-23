package org.pocketcampus.plugin.freeroom.android.views;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.FreeRoomAbstractView;
import org.pocketcampus.plugin.freeroom.android.FreeRoomController;
import org.pocketcampus.plugin.freeroom.android.FreeRoomManageFavoritesView;
import org.pocketcampus.plugin.freeroom.android.FreeRoomModel;
import org.pocketcampus.plugin.freeroom.android.FreeRoomSearchRoomsResultView;
import org.pocketcampus.plugin.freeroom.android.adapter.ActualOccupationArrayAdapter;
import org.pocketcampus.plugin.freeroom.android.adapter.ExpandableListViewAdapter;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.android.utils.OrderMapList;
import org.pocketcampus.plugin.freeroom.shared.ActualOccupation;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.FRRequest;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomRequest;
import org.pocketcampus.plugin.freeroom.shared.Occupancy;
import org.pocketcampus.plugin.freeroom.shared.OccupancyRequest;
import org.pocketcampus.plugin.freeroom.shared.utils.FRTimes;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
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

	private TextView mTextView;
	private ExpandableListView mExpView;

	private ExpandableListViewAdapter<Occupancy> mExpList;

	/**
	 * View that holds the INFO popup content.
	 */
	private View popupInfoView;
	/**
	 * Window that holds the INFO popup. Note: popup window can be closed by:
	 * the closing button (red cross), back button, or clicking outside the
	 * popup.
	 */
	private PopupWindow popupInfoWindow;

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
			Intent i = new Intent(FreeRoomHomeView.this,
					FreeRoomSearchView.class);
			FreeRoomHomeView.this.startActivity(i);
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
		mLayout.setOrientation(LinearLayout.VERTICAL);
		// The ActionBar is added automatically when you call setContentView
		setContentView(mLayout);
		// mLayout.setTitle(getString(R.string.freeroom_title_main_title));
		// mLayout.hideTitle();

		mExpView = new ExpandableListView(getApplicationContext());
		mTextView = new TextView(getApplicationContext());
		mLayout.addView(mTextView);
		setTextSummary(getString(R.string.freeroom_home_init_please_wait));
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

		if (mController != null) {
			mController.sendFRRequest(this);
			// mController.checkOccupancy(this);
		}
	}

	@Override
	public void initializeView() {
		mExpList = new ExpandableListViewAdapter<Occupancy>(
				getApplicationContext(), mModel.getOccupancyResults(),
				mController, this);
		mExpView.setAdapter(mExpList);
		addActionToActionBar(hideUnhideAllResults);
		addActionToActionBar(refresh);
		addActionToActionBar(editFavorites);
		addActionToActionBar(search);
		addActionToActionBar(gotBackMenu);
	}

	/**
	 * Inits the popup to diplay the information about a room.
	 */
	private void initPopupInfoRoom() {
		// construct the popup
		// it MUST fill the parent in height, such that weight works in xml for
		// heights. Otherwise, some elements may not be displayed anymore
		LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		popupInfoView = layoutInflater.inflate(
				R.layout.freeroom_layout_popup_info, null);
		popupInfoWindow = new PopupWindow(popupInfoView,
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, true);

		// allows outside clicks to close the popup
		popupInfoWindow.setOutsideTouchable(true);
		popupInfoWindow.setBackgroundDrawable(new BitmapDrawable());

		TextView tv = (TextView) popupInfoView
				.findViewById(R.id.reeroom_layout_popup_info_title);
		// TODO: string + customized title + bigger/black
		tv.setText("Room detailled informations");

		ImageView img = (ImageView) popupInfoView
				.findViewById(R.id.freeroom_layout_popup_info_close);
		img.setOnClickListener(new ImageView.OnClickListener() {

			@Override 
			public void onClick(View v) {
				popupInfoWindow.dismiss();
			}
		});
	}

	/**
	 * Overides the legacy <code>onKeyDown</code> method in order to close the
	 * popupWindow if one was opened.
	 * 
	 * @param keyCode
	 * @param event
	 * @return
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		// Override back button
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (popupInfoWindow.isShowing()) {
				popupInfoWindow.dismiss();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void anyError() {
		setTextSummary(getString(R.string.freeroom_home_error_sorry));
	}

	/**
	 * Sets the summary text box to the specified text.
	 * 
	 * @param text
	 *            the new summary to be displayed.
	 */
	private void setTextSummary(String text) {
		mTextView.setText(text);
	}

	/**
	 * Inits the request to the current next valid period.
	 */
	private void init() {
		ArrayList<String> array = new ArrayList<String>();
		array.addAll(mModel.getAllRoomMapFavorites().keySet());
		// TODO: deprecated
		// requestDEPRECATED = new OccupancyRequest(array,
		// FRTimes.getNextValidPeriod());
		// new interface

		initPopupInfoRoom();
		mModel.setFRRequest(new FRRequest(FRTimes.getNextValidPeriod(), mModel
				.getAllRoomMapFavorites().keySet().isEmpty(), array));
	}

	/**
	 * Send the set request to the controller.
	 */
	private void refresh() {
		setTextSummary(getString(R.string.freeroom_home_please_wait));
		// TODO: deprecated
		// mController.prepareCheckOccupancy(requestDEPRECATED);
		// mController.checkOccupancy(this);

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
		// occupancyResultsUpdated();
	}

	private void hideUnHideAllResults() {
		mModel.switchAvailable();
		mExpList.notifyDataSetChanged();
		sysoAlloccuResult();
	}

	private void sysoAlloccuResult() {
		OrderMapList<String, List<?>, Occupancy> map = mModel
				.getOccupancyResults();
		for (String head : map.keySetOrdered()) {
			List<Occupancy> listOcc = (List<Occupancy>) map.get(head);
			for (Occupancy mOccupancy : listOcc) {
				Log.v("test", "room: " + mOccupancy.getRoom().getDoorCode());
				for (ActualOccupation mActualOccupation : mOccupancy
						.getOccupancy()) {
					FRPeriod period = mActualOccupation.getPeriod();
					Date end = new Date(period.getTimeStampEnd());
					Date start = new Date(period.getTimeStampStart());
					Log.v("test", "ActualOccupation! Available :"
							+ mActualOccupation.isAvailable());
					Log.v("test",
							"From: " + start + " / "
									+ period.getTimeStampStart());
					Log.v("test",
							"To: " + end + " / " + period.getTimeStampEnd());
					Log.v("test",
							"Pronostics: "
									+ mActualOccupation.getProbableOccupation()
									+ " / ratio:"
									+ mActualOccupation.getRatioOccupation());
				}
			}
		}
	}

	@Override
	public void occupancyResultsUpdated() {
		FRRequest request = mModel.getFRRequest();
		String s = "";
		if (request.isOnlyFreeRooms()) {
			s += "Free rooms ";
		} else {
			s += "Rooms ";
		}
		FRPeriod period = request.getPeriod();
		Date endDate = new Date(period.getTimeStampEnd());
		Date startDate = new Date(period.getTimeStampStart());
		SimpleDateFormat day_month = new SimpleDateFormat(
				getString(R.string.freeroom_pattern_day_format));
		SimpleDateFormat hour_min = new SimpleDateFormat(
				getString(R.string.freeroom_pattern_hour_format));

		s += getString(R.string.freeroom_check_occupancy_result_onthe) + " ";
		s += day_month.format(startDate);
		s += " " + getString(R.string.freeroom_check_occupancy_result_from)
				+ " ";
		s += hour_min.format(startDate);
		s += " " + getString(R.string.freeroom_check_occupancy_result_to) + " ";
		s += hour_min.format(endDate);
		// TODO: remove empty
		if (mModel.getOccupancyResults().isEmpty()) {
			s += "Sorry no results";
		}
		setTextSummary(s);
		mExpList.notifyDataSetChanged();
	}

	public void displayPopupInfo() {
		Occupancy mOccupancy = mModel.getDisplayedOccupancy();
		if (mOccupancy != null) {
			ListView roomOccupancyListView = (ListView) popupInfoView
					.findViewById(R.id.freeroom_layout_popup_info_roomOccupancy);
			roomOccupancyListView
					.setAdapter(new ActualOccupationArrayAdapter<ActualOccupation>(
							getApplicationContext(), mOccupancy.getOccupancy(),
							mModel));

			ListView infoRoomListView = (ListView) popupInfoView
					.findViewById(R.id.freeroom_layout_popup_info_infoRoom);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					R.layout.sdk_list_entry, R.id.sdk_list_entry_text,
					getInfoFRRoom(mOccupancy.getRoom()));
			infoRoomListView.setAdapter(adapter);
			popupInfoWindow.showAsDropDown(mTextView, 10, 10);
		}
	}

	/**
	 * Converts a FRRoom to an arrayList of properties, in order to display
	 * them.
	 * <p>
	 * TODO: this method is to be perfectionned (and put in R.string)
	 * 
	 * @param mFrRoom
	 * @return
	 */
	private ArrayList<String> getInfoFRRoom(FRRoom mFrRoom) {
		ArrayList<String> array = new ArrayList<String>(20);
		if (mFrRoom.isSetDoorCode()) {
			if (mFrRoom.isSetDoorCodeAlias()) {
				array.add(mFrRoom.getDoorCodeAlias() + " ("
						+ mFrRoom.getDoorCode() + ")");
			} else {
				array.add(mFrRoom.getDoorCode());
			}
		}
		if (mFrRoom.isSetType()) {
			array.add("type: " + mFrRoom.getType());
		}
		if (mFrRoom.isSetCapacity()) {
			array.add(mFrRoom.getCapacity() + " places");
		}
		if (mFrRoom.isSetSurface()) {
			array.add(mFrRoom.getSurface() + " sqm");
		}
		if (mFrRoom.isSetUid()) {
			// uniq UID must be 1201XXUID, with XX filled with 0 such that
			// it has 10 digit
			// the prefix "1201" indiquates that it's a EPFL room (not a phone,
			// a computer)
			String communUID = "1201";
			String roomUID = mFrRoom.getUid();
			for (int i = roomUID.length() + 1; i <= 6; i++) {
				communUID += "0";
			}
			communUID += roomUID;
			array.add("uniq UID : " + communUID);
		}
		return array;
	}
}