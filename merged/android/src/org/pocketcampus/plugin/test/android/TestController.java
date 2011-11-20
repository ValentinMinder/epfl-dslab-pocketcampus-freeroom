package org.pocketcampus.plugin.test.android;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.test.android.iface.ITestController;
import org.pocketcampus.plugin.test.android.req.BarRequest;
import org.pocketcampus.plugin.test.shared.TestService.Client;
import org.pocketcampus.plugin.test.shared.TestService.Iface;

public class TestController extends PluginController implements ITestController {

	private TestModel mModel;
	private Iface mClient;
	
	/**
	 *  This name must match given in the Server.java file in plugin.launcher.server.
	 *  It's used to route the request to the right server implementation.
	 */
	private String mPluginName = "test";

	@Override
	public void onCreate() {
		// Initializing the model is part of the controller's job...
		mModel = new TestModel();
		
		// ...as well as initializing the client.
		// The "client" is the connection we use to access the service.
		mClient = (Iface) getClient(new Client.Factory(), mPluginName);
	}
	
	/**
	 * The view will call this in order to register in the model's listener list.
	 */
	@Override
	public PluginModel getModel() {
		return mModel;
	}

	@Override
	public void setFoo(int value) {
		mModel.setFoo(value);
	}

	@Override
	public void loadBar() {
		new BarRequest().start(this, mClient, (Object)null);
	}

}













