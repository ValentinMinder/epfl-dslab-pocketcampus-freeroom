package org.pocketcampus.plugin.freeroom.android;

import java.util.ArrayList;
import java.util.Calendar;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.android.utils.Converter;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.FRTimeStamp;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomRequest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * FreeRoomMainView - Main view that shows FreeRoom courses.
 * 
 * This is the main view in the FreeRoom Plugin. It checks if the user is logged
 * in, if not it pings the Authentication Plugin. When it gets back a valid
 * SessionId it fetches the user's FreeRoom data.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class FreeRoomMainView extends FreeRoomAbstractView implements IFreeRoomView {

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
		mLayout.hideTitle();

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
		
		mListValues.add("Find me a free room now !");
		mListValues.add("Search for a free room");
		mListValues.add("Favorites / History");
				
		mList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent i = null;
				switch (arg2) {
				case 0:
					Calendar calendar = Calendar.getInstance();
					mController.searchFreeRoom(FreeRoomMainView.this, 
							Converter.convert(calendar.get(Calendar.DAY_OF_WEEK), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.HOUR_OF_DAY) + 1));
					i = new Intent(FreeRoomMainView.this, FreeRoomResultView.class);
					break;
				case 1:
					i = new Intent(FreeRoomMainView.this, FreeRoomSearchView.class);
					break;
				case 2:
					break;
				default:
					break;
				}
				
				FreeRoomMainView.this.startActivity(i);
			}
			
		});
		mLayout.addView(mList);
	}

	@Override
	public void freeRoomResultsUpdated() {
		// we do nothing here
	}
}
