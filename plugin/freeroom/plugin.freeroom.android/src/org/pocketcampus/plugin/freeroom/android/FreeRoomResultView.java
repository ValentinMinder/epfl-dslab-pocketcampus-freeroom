package org.pocketcampus.plugin.freeroom.android;

import java.util.ArrayList;
import java.util.Set;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;

import com.markupartist.android.widget.ActionBar.Action;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class FreeRoomResultView extends FreeRoomAbstractView implements IFreeRoomView {

	private FreeRoomController mController;
	private FreeRoomModel mModel;

	private StandardTitledLayout mLayout;
	private LinearLayout subLayout;
	
	private Button resetButton;
	
	private ListView mList;
	private ArrayList<String> mListValues;
	private ArrayAdapter<String> mAdapter;
	
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return FreeRoomController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {
		// Tracker
		Tracker.getInstance().trackPageView("freeroom/search/viewresult");

		// Get and cast the controller and model
		mController = (FreeRoomController) controller;
		mModel = (FreeRoomModel) controller.getModel();

		// Setup the layout
		mLayout = new StandardTitledLayout(this);
		
		subLayout = new LinearLayout(this);
		subLayout.setOrientation(LinearLayout.VERTICAL);
	
		mLayout.addFillerView(subLayout);
		
		// The ActionBar is added automatically when you call setContentView
		setContentView(mLayout);
		mLayout.setTitle(getString(R.string.freeroom_title_FRresult));

		initializeResultView();

	}
	
	private void initializeResultView() {
		resetButton = new Button(this);	
		resetButton.setEnabled(false);
		resetButton.setText(R.string.freeroom_resetbutton); 
		resetButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				System.out.println("reset!");
				// TODO action reset/ return
			}
		});
		subLayout.addView(resetButton);
		
		mList = new ListView(this);
		LayoutParams p = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		mList.setLayoutParams(p);

		mListValues = new ArrayList<String>();
		mListValues.add("CO123fake");
		mAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line,
				android.R.id.text1, mListValues);
		mList.setAdapter(mAdapter);
				
		mList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String s = mAdapter.getItem(arg2);
				// String s = mListValues.get(arg2); // TODO check which one to keep
				System.out.println("selected " + s);
				mController.getModel();
				//TODO: display map!
				Uri mUri = Uri.parse("pocketcampus://map.plugin.pocketcampus.org/search");
				Uri.Builder mbuild = mUri.buildUpon().appendQueryParameter("q", "CO1");
				Intent i = new Intent(Intent.ACTION_VIEW, mbuild.build());
				startActivity(i);
			}
			
		});
		subLayout.addView(mList);
	}

	@Override
	public void freeRoomResultsUpdated() {
		mAdapter.clear();
		mListValues.clear();
		mAdapter.notifyDataSetChanged();
		Set<FRRoom> res = mModel.getFreeRoomResults();
		for (FRRoom frRoom : res) {
			mListValues.add(frRoom.getBuilding() + " " + frRoom.getNumber()); 
		}
		if (res.isEmpty()) {
			Toast.makeText(
					getApplicationContext(),
					getString(R.string.freeroom_no_room_available), Toast.LENGTH_LONG)
					.show();
		}
		Log.v("freeroom_result", "data_updated");
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void autoCompletedUpdated() {
		// we do nothing here
	}

	@Override
	public void occupancyResultUpdated() {
		// we do nothing here
		Log.v("fr-freeroom-result", "listener to occupancyResultUpdated called" );
	}

}
