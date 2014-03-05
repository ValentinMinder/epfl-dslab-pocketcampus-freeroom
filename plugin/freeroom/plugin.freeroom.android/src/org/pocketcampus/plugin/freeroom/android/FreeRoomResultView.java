package org.pocketcampus.plugin.freeroom.android;

import java.util.ArrayList;
import java.util.Set;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

public class FreeRoomResultView extends FreeRoomAbstractView implements IFreeRoomView {

	private FreeRoomController mController;
	private FreeRoomModel mModel;

	private StandardTitledLayout mLayout;
	private LinearLayout subLayout;
	
//	private Button resetButton;
	
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
		mLayout.hideTitle();

		initializeResultView();

	}
	
	private void initializeResultView() {
//		resetButton = new Button(this);	
//		resetButton.setEnabled(false);
//		resetButton.setText(R.string.freeroom_searchbutton); //TODO: change
//		resetButton.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				System.out.println("reset!");
////				if (auditSearchButton() == 0) {
////					//TODO action
////				}
//			}
//		});
//		mLayout.addView(resetButton);
		
		mList = new ListView(this);
		LayoutParams p = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		mList.setLayoutParams(p);

		mListValues = new ArrayList<String>();
		mListValues.add("fake"); // TODO delete
		mAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line,
				android.R.id.text1, mListValues);
		mList.setAdapter(mAdapter);
				
		mList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String s = mAdapter.getItem(arg2);
				// String s = mListValues.get(arg2); // TODO delete
				System.out.println("selected " + s);
				mController.getModel();
				//TODO: display map!
			}
			
		});
		mLayout.addView(mList);
		
		
	}

	@Override
	public void freeRoomResultsUpdated() {
		System.out.println("updated!"); //TODO delete
		Set<FRRoom> res = mModel.getFreeRoomResults();
		for (FRRoom frRoom : res) {
//			mListValues.add(frRoom.getBuilding() + " " + frRoom.getNumber()); // TODO delete
			mAdapter.add(frRoom.getBuilding() + " " + frRoom.getNumber());
		}
//		mListValues.add("FakeBuilding"); //TODO: delete
		mAdapter.add("fake building"); //TODO: delete
	}

}
