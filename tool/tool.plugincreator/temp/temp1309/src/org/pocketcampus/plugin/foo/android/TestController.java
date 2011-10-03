package org.pocketcampus.plugin.test.android;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginModel;

public class TestController extends PluginController {

	@Override
	public PluginModel getModel() {
		return new TestModel();
	}

}
