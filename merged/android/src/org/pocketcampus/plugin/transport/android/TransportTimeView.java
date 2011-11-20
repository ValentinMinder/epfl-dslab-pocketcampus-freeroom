package org.pocketcampus.plugin.transport.android;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.plugin.transport.android.iface.ITransportView;

import android.os.Bundle;
import android.widget.Toast;

public class TransportTimeView extends PluginView implements ITransportView {
	private TransportController mController;
	private TransportModel mModel;

	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return TransportController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		mController = (TransportController)controller;
		mModel = (TransportModel) mController.getModel();
		
		
	}

	@Override
	public void networkErrorHappened() {
		Toast toast = Toast.makeText(getApplicationContext(), "Network error!", Toast.LENGTH_SHORT);
		toast.show();
	}

	@Override
	public void preferredDestinationsUpdated() {}
}
