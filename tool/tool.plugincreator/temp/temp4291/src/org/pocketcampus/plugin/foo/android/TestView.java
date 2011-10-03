package org.pocketcampus.plugin.test.android;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginModel.ViewMessage;
import org.pocketcampus.android.platform.sdk.core.PluginView;

import android.app.Service;
import android.os.Bundle;

public class TestView extends PluginView {

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		setContentView(R.layout.main);
		
	}

	@Override
	protected void handleMessage(ViewMessage msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Class<? extends Service> getControllerClass() {
		return TestController.class;
	}

}
