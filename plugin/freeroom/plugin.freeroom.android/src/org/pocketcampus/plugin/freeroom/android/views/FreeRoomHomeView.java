package org.pocketcampus.plugin.freeroom.android.views;

import java.util.ArrayList;
import java.util.Calendar;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.FreeRoomAbstractView;
import org.pocketcampus.plugin.freeroom.android.FreeRoomCheckOccupancySearchView;
import org.pocketcampus.plugin.freeroom.android.FreeRoomController;
import org.pocketcampus.plugin.freeroom.android.FreeRoomManageFavoritesView;
import org.pocketcampus.plugin.freeroom.android.FreeRoomModel;
import org.pocketcampus.plugin.freeroom.android.FreeRoomSearchRoomsResultView;
import org.pocketcampus.plugin.freeroom.android.FreeRoomSearchRoomsView;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.android.layout.FreeRoomTabLayout;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomRequest;
import org.pocketcampus.plugin.freeroom.shared.utils.FRTimes;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.markupartist.android.widget.ActionBar.Action;

/**
 * TODO THIS MUST BE TEH NEW HOME, WHICH IS CURRENTLY UNDER MAINTENANCE FOR
 * REWRITTING COMPLETELY
 * 
 * MainView is the entry of the plugin, displaying the possible menus/features
 * available.
 * <p>
 * 
 * @author FreeRoom Project Team - Julien WEBER <julien.weber@epfl.ch> and
 *         Valentin MINDER <valentin.minder@epfl.ch>
 * 
 */
public class FreeRoomHomeView extends FreeRoomAbstractView implements
		IFreeRoomView {

	private FreeRoomController mController;
	private FreeRoomModel mModel;

	private LinearLayout mLayout;

	private ListView mList;
	private ArrayList<String> mListValues;

	private Action search = new Action() {
		public void performAction(View view) {
			System.out.println("search clicked");
			Intent i = new Intent(FreeRoomHomeView.this,
					FreeRoomCheckOccupancySearchView.class);
			FreeRoomHomeView.this.startActivity(i);
		}

		public int getDrawable() {
			return R.drawable.magnify2x06;
		}
	};
	private Action search2 = new Action() {
		public void performAction(View view) {
			System.out.println("search clicked");
			Intent i = new Intent(FreeRoomHomeView.this,
					FreeRoomSearchRoomsView.class);
			FreeRoomHomeView.this.startActivity(i);
		}

		public int getDrawable() {
			return R.drawable.magnify06;
		}
	};

	private Action editFavorites = new Action() {
		public void performAction(View view) {
			System.out.println("Edit fav clicked");
			Intent i = new Intent(FreeRoomHomeView.this,
					FreeRoomManageFavoritesView.class);
			FreeRoomHomeView.this.startActivity(i);
		}

		public int getDrawable() {
			return R.drawable.pencil2x187;
		}
	};

	private Action refresh = new Action() {
		public void performAction(View view) {
			System.out.println("refresh clicked");
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
		mLayout = new LinearLayout(getApplicationContext());

		// The ActionBar is added automatically when you call setContentView
		setContentView(mLayout);
		// mLayout.setTitle(getString(R.string.freeroom_title_main_title));
//		mLayout.hideTitle();
		initializeView();
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
		addActionToActionBar(refresh);
		addActionToActionBar(editFavorites);
		addActionToActionBar(search2);
		addActionToActionBar(search);
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
}
