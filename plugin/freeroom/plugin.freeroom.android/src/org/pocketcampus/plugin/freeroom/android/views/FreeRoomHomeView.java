package org.pocketcampus.plugin.freeroom.android.views;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.FreeRoomAbstractView;
import org.pocketcampus.plugin.freeroom.android.FreeRoomController;
import org.pocketcampus.plugin.freeroom.android.FreeRoomManageFavoritesView;
import org.pocketcampus.plugin.freeroom.android.FreeRoomModel;
import org.pocketcampus.plugin.freeroom.android.adapter.ActualOccupationArrayAdapter;
import org.pocketcampus.plugin.freeroom.android.adapter.ExpandableListViewAdapter;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.shared.ActualOccupation;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.FRRequest;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;
import org.pocketcampus.plugin.freeroom.shared.ImWorkingRequest;
import org.pocketcampus.plugin.freeroom.shared.Occupancy;
import org.pocketcampus.plugin.freeroom.shared.WorkingOccupancy;
import org.pocketcampus.plugin.freeroom.shared.utils.FRTimes;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.markupartist.android.widget.ActionBar.Action;

/**
 * <code>FreeRoomHomeView</code> is the main <code>View</code>, it's the entry
 * of the plugin. It displays the availabilities for the search given, and for
 * your favorites NOW at the start.
 * <p>
 * All others views are supposed to be popup windows, therefore it's always
 * visible.
 * 
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 */
public class FreeRoomHomeView extends FreeRoomAbstractView implements
		IFreeRoomView {

	/**
	 * FreeRoom controller in MVC scheme.
	 */
	private FreeRoomController mController;
	/**
	 * FreeRoom model in MVC scheme.
	 */
	private FreeRoomModel mModel;

	/**
	 * Titled layout that holds the title and the main layout.
	 */
	private StandardTitledLayout titleLayout;
	/**
	 * Main layout that hold all UI components.
	 */
	private LinearLayout mainLayout;

	/**
	 * TextView to display a short message about what is currently displayed.
	 * It's also the anchor to all the popup windows.
	 */
	private TextView mTextView;
	/**
	 * ExpandableListView to display the results of occupancies building by
	 * building.
	 */
	private ExpandableListView mExpView;

	/**
	 * Adapter for the results (to display the occupancies).
	 */
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
	 * View that holds the SEARCH popup content.
	 */
	private View popupSearchView;
	/**
	 * Window that holds the SEARCH popup. Note: popup window can be closed by:
	 * the closing button (red cross), back button, or clicking outside the
	 * popup.
	 */
	private PopupWindow popupSearchWindow;

	/**
	 * Action to perform a customized search.
	 */
	private Action search = new Action() {
		public void performAction(View view) {
			// TODO: remove the rest, only keep the popup
			popupSearchWindow.showAsDropDown(mTextView, 0, 0);
			// TODO: popup instead of new activity
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

	/**
	 * Action to edit the user's favorites.
	 */
	private Action editFavorites = new Action() {
		public void performAction(View view) {
			// TODO: popup instead of new activity
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

	/**
	 * Action to refresh the view (it sends the same stored request again).
	 * <p>
	 * TODO: useful? useless ? delete !
	 */
	private Action refresh = new Action() {
		public void performAction(View view) {
			refresh();
		}

		public int getDrawable() {
			return R.drawable.refresh2x01;
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
		titleLayout = new StandardTitledLayout(this);
		mainLayout = new LinearLayout(this);
		mainLayout.setOrientation(LinearLayout.VERTICAL);
		// The ActionBar is added automatically when you call setContentView
		setContentView(titleLayout);
		titleLayout.setTitle(getString(R.string.freeroom_title_main_title));

		mExpView = new ExpandableListView(getApplicationContext());
		mTextView = new TextView(getApplicationContext());
		mainLayout.addView(mTextView);
		setTextSummary(getString(R.string.freeroom_home_init_please_wait));
		mainLayout.addView(mExpView);
		initializeView();

		titleLayout.addFillerView(mainLayout);

		initDefaultRequest();
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
		}
	}

	@Override
	public void initializeView() {
		mExpList = new ExpandableListViewAdapter<Occupancy>(
				getApplicationContext(), mModel.getOccupancyResults(),
				mController, this);
		mExpView.setAdapter(mExpList);
		addActionToActionBar(refresh);
		addActionToActionBar(editFavorites);
		addActionToActionBar(search);
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
				.findViewById(R.id.freeroom_layout_popup_info_name);
		tv.setText("room");

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
	 * Inits the popup to diplay the information about a room.
	 */
	private void initPopupSearch() {
		// construct the popup
		// it MUST fill the parent in height, such that weight works in xml for
		// heights. Otherwise, some elements may not be displayed anymore
		LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		popupSearchView = layoutInflater.inflate(
				R.layout.freeroom_layout_popup_search, null);
		popupSearchWindow = new PopupWindow(popupSearchView,
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, true);

		// allows outside clicks to close the popup
		popupSearchWindow.setOutsideTouchable(true);
		popupSearchWindow.setBackgroundDrawable(new BitmapDrawable());

		Button tv = (Button) popupSearchView
				.findViewById(R.id.freeroom_layout_popup_search_go);
		tv.setText("Go!!");

		ImageView img = (ImageView) popupSearchView
				.findViewById(R.id.freeroom_layout_popup_search_close);
		img.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				popupSearchWindow.dismiss();
			}
		});
	}

	/**
	 * Overrides the legacy <code>onKeyDown</code> method in order to close the
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
			boolean flag = false;
			if (popupInfoWindow.isShowing()) {
				popupInfoWindow.dismiss();
				flag = true;
			}
			if (popupSearchWindow.isShowing()) {
				popupSearchWindow.dismiss();
				flag = true;
			}
			if (flag) {
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
	 * Constructs the default request (check all the favorites for the next
	 * valid period) and sets it in the model for future use. You may call
	 * <code>refresh</code> in order to send it to the server.
	 */
	private void initDefaultRequest() {
		Set<String> set = mModel.getAllRoomMapFavorites().keySet();
		ArrayList<String> array = new ArrayList<String>(set.size());
		array.addAll(set);

		initPopupInfoRoom();
		initPopupSearch();
		mModel.setFRRequest(new FRRequest(FRTimes.getNextValidPeriod(), mModel
				.getAllRoomMapFavorites().keySet().isEmpty(), array));
	}

	/**
	 * Asks the controller to send again the request which was already set in
	 * the model.
	 */
	private void refresh() {
		setTextSummary(getString(R.string.freeroom_home_please_wait));
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
	}

	@Override
	public void occupancyResultsUpdated() {
		StringBuilder build = new StringBuilder(50);
		if (mModel.getOccupancyResults().isEmpty()) {
			build.append(getString(R.string.freeroom_home_error_no_results));
		} else {
			FRRequest request = mModel.getFRRequest();

			if (request.isOnlyFreeRooms()) {
				build.append(getString(R.string.freeroom_home_info_free_rooms));
			} else {
				build.append(getString(R.string.freeroom_home_info_rooms));
			}
			FRPeriod period = request.getPeriod();
			build.append(generateTimeSummary(period));
		}

		setTextSummary(build.toString());
		mExpList.notifyDataSetChanged();
		mExpList.updateCollapse(mExpView);
	}

	/**
	 * Generates a string summary of a given period of time.
	 * <p>
	 * eg: "Wednesday Apr 24 from 9am to 12pm"
	 * 
	 * @param period
	 *            the period of time
	 * @return a string summary of a given period of time.
	 */
	private String generateTimeSummary(FRPeriod period) {
		StringBuilder build = new StringBuilder(100);
		Date endDate = new Date(period.getTimeStampEnd());
		Date startDate = new Date(period.getTimeStampStart());
		SimpleDateFormat day_month = new SimpleDateFormat(
				getString(R.string.freeroom_pattern_day_format));
		SimpleDateFormat hour_min = new SimpleDateFormat(
				getString(R.string.freeroom_pattern_hour_format));

		build.append(" ");
		// TODO: if date is today, use "today" instead of specifying date
		build.append(getString(R.string.freeroom_check_occupancy_result_onthe));
		build.append(" ");
		build.append(day_month.format(startDate));
		build.append(" ");
		build.append(getString(R.string.freeroom_check_occupancy_result_from));
		build.append(" ");
		build.append(hour_min.format(startDate));
		build.append(" ");
		build.append(getString(R.string.freeroom_check_occupancy_result_to));
		build.append(" ");
		build.append(hour_min.format(endDate));
		return build.toString();
	}

	/**
	 * Put a onClickListener on an imageView in order to share the location and
	 * time when clicking share, if available.
	 * 
	 * @param shareImageView
	 *            the view on which to put the listener
	 * @param homeView
	 *            reference to the home view
	 * @param mOccupancy
	 *            the holder of data for location and time
	 */
	public void setShareClickListener(ImageView shareImageView,
			final FreeRoomHomeView homeView, final Occupancy mOccupancy) {

		if (!mOccupancy.isIsAtLeastOccupiedOnce()
				&& mOccupancy.isIsAtLeastFreeOnce()) {
			shareImageView.setClickable(true);
			shareImageView.setImageResource(R.drawable.share);
			shareImageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// share
					List<ActualOccupation> list = mOccupancy.getOccupancy();
					long tss = list.get(0).getPeriod().getTimeStampStart();
					long tse = list.get(list.size() - 1).getPeriod()
							.getTimeStampEnd();
					FRPeriod mPeriod = new FRPeriod(tss, tse, false);
					homeView.share(mPeriod, mOccupancy.getRoom());
				}
			});
		} else {
			shareImageView.setClickable(false);
			shareImageView.setImageResource(R.drawable.share_disabled);
		}
	}

	/**
	 * Display the popup that provides more info about the occupation of the
	 * selected room.
	 */
	public void displayPopupInfo() {
		final Occupancy mOccupancy = mModel.getDisplayedOccupancy();
		if (mOccupancy != null) {
			TextView tv = (TextView) popupInfoView
					.findViewById(R.id.freeroom_layout_popup_info_name);
			final FRRoom mRoom = mOccupancy.getRoom();
			String text = mRoom.getDoorCode();
			if (mRoom.isSetDoorCodeAlias()) {
				// alias is displayed IN PLACE of the official name
				// the official name can be found in bottom of popup
				text = mRoom.getDoorCodeAlias();
			}
			tv.setText(text);

			ImageView iv = (ImageView) popupInfoView
					.findViewById(R.id.freeroom_layout_popup_info_share);
			setShareClickListener(iv, this, mOccupancy);

			ListView roomOccupancyListView = (ListView) popupInfoView
					.findViewById(R.id.freeroom_layout_popup_info_roomOccupancy);
			roomOccupancyListView
					.setAdapter(new ActualOccupationArrayAdapter<ActualOccupation>(
							getApplicationContext(), mOccupancy.getOccupancy(),
							mController, this));

			TextView detailsTextView = (TextView) popupInfoView
					.findViewById(R.id.freeroom_layout_popup_info_details);
			detailsTextView.setText(getInfoFRRoom(mOccupancy.getRoom()));
			popupInfoWindow.showAsDropDown(mTextView, 0, 0);
		}
	}

	/**
	 * Construct the Intent to share the location and time with friends. The
	 * same information is shared with the server at the same time
	 * 
	 * @param mPeriod
	 *            time period
	 * @param mRoom
	 *            location
	 */
	public void share(FRPeriod mPeriod, FRRoom mRoom) {
		WorkingOccupancy work = new WorkingOccupancy(mPeriod, mRoom);
		ImWorkingRequest request = new ImWorkingRequest(work,
				mModel.getAnonymID());
		mController.prepareImWorking(request);
		mController.ImWorking(this);

		// TODO: in case of "now" request (nextPeriodValid is now), just put
		// "i am, now, " instead of
		// time
		StringBuilder textBuilder = new StringBuilder(100);
		textBuilder.append(getString(R.string.freeroom_share_iwillbe) + " ");
		textBuilder.append(getString(R.string.freeroom_share_in_room) + " ");
		if (mRoom.isSetDoorCodeAlias()) {
			textBuilder.append(mRoom.getDoorCodeAlias() + " ");
		} else {
			textBuilder.append(mRoom.getDoorCode() + " ");
		}
		// TODO: which period to use ?
		// in case of specified in request, we should use the personalized
		// period
		textBuilder.append(generateTimeSummary(mPeriod) + ". ");
		textBuilder.append(getString(R.string.freeroom_share_please_come));

		System.out.println(textBuilder.toString());
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, textBuilder.toString()
				+ " (This was sent thru pocketcampus .org)");
		sendIntent.setType("text/plain");
		startActivity(Intent.createChooser(sendIntent,
				getString(R.string.freeroom_share_title)));
	}

	/**
	 * Converts a FRRoom to a String of only major properties, in order to
	 * display them. It includes name (with alias), type, capacity, surface and
	 * UID.
	 * <p>
	 * TODO: this method may be changed
	 * 
	 * @param mFrRoom
	 * @return
	 */
	private String getInfoFRRoom(FRRoom mFrRoom) {
		StringBuilder builder = new StringBuilder(50);
		if (mFrRoom.isSetDoorCode()) {
			if (mFrRoom.isSetDoorCodeAlias()) {
				builder.append(mFrRoom.getDoorCode() + " (alias: "
						+ mFrRoom.getDoorCodeAlias() + ")");
			} else {
				builder.append(mFrRoom.getDoorCode());
			}
		}
		if (mFrRoom.isSetType()) {
			builder.append(" / " + getString(R.string.freeroom_popup_info_type)
					+ ": " + mFrRoom.getType());
		}
		if (mFrRoom.isSetCapacity()) {
			builder.append(" / "
					+ getString(R.string.freeroom_popup_info_capacity) + ": "
					+ mFrRoom.getCapacity() + " "
					+ getString(R.string.freeroom_popup_info_places));
		}
		if (mFrRoom.isSetSurface()) {
			builder.append(" / "
					+ getString(R.string.freeroom_popup_info_surface) + ": "
					+ mFrRoom.getSurface() + " "
					+ getString(R.string.freeroom_popup_info_sqm));
		}
		// TODO: for production, remove UID (it's useful for debugging for the
		// moment)
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
			builder.append(" / "
					+ getString(R.string.freeroom_popup_info_uniqID) + ": "
					+ communUID);
		}
		return builder.toString();
	}
}