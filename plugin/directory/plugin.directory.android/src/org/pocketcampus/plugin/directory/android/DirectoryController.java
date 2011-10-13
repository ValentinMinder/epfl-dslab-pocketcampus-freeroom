package org.pocketcampus.plugin.directory.android;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginModel;


public class DirectoryController extends PluginController {

	private DirectoryModel mModel;
	
	/**
	 *  This name must match given in the Server.java file in plugin.launcher.server.
	 *  It's used to route the request to the right server implementation.
	 */
	private String mPluginName = "directory";
	
	@Override
	public PluginModel getModel() {
		return mModel;
	}

}
