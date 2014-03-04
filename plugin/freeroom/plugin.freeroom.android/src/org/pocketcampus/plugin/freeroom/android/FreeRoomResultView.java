package org.pocketcampus.plugin.freeroom.android;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class FreeRoomResultView extends PluginView implements IFreeRoomView {

	private FreeRoomController mController;
	private FreeRoomModel mModel;

	private StandardTitledLayout mLayout;
	private LinearLayout subLayout;
	
	private Button resetButton;
	
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
		// TODO Auto-generated method stub
		resetButton = new Button(this);	
		resetButton.setEnabled(false);
		resetButton.setText(R.string.freeroom_searchbutton); //TODO: change
		resetButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				if (auditSearchButton() == 0) {
//					//TODO action
//				}
			}
		});
		
		
	}
	
	
	@Override
	public void networkErrorHappened() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void freeRoomServersDown() {
		// TODO Auto-generated method stub
		
	}

}
