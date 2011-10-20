package org.pocketcampus.plugin.camipro.android;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardLayout;
import org.pocketcampus.plugin.camipro.android.iface.ICamiproModel;
import org.pocketcampus.plugin.camipro.android.iface.ICamiproView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

public class CamiproMainView extends PluginView implements ICamiproView {

	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return CamiproController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		// Get and cast the controller and model
		mController = (CamiproController) controller;
		mModel = (CamiproModel) controller.getModel();

		// The StandardLayout is a RelativeLayout with a TextView in its center.
		mLayout = new StandardLayout(this);

		// The ActionBar is added automatically when you call setContentView
		setContentView(mLayout);

		mLayout.setText("Loading");
		mController.refreshBalance();
		
	}

	@Override
	public void transactionsUpdated() {
		displayData();
	}

	@Override
	public void balanceUpdated() {
		displayData();
	}

	@Override
	public void ebankingUpdated() {
		displayData();
	}

	private void displayData() {
		mLayout.setText("Balance is " + mModel.getBalance());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.camipro_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		
		if(item.getItemId() == R.id.camipro_refresh) {			
			mController.refreshBalance();
		}
		

		return true;
	}
	
	@Override
	public void networkErrorHappened() {
		Toast.makeText(getApplicationContext(), "Network error!", Toast.LENGTH_SHORT).show();
	}

	private CamiproController mController;
	private ICamiproModel mModel;

	private StandardLayout mLayout;

}
