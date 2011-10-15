package org.pocketcampus.plugin.directory.android;

import java.util.List;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.directory.android.iface.IDirectoryController;
import org.pocketcampus.plugin.directory.android.req.*;
import org.pocketcampus.plugin.directory.android.DirectoryModel;
import org.pocketcampus.plugin.directory.shared.DirectoryService.Client;
import org.pocketcampus.plugin.directory.shared.DirectoryService.Iface;
import org.pocketcampus.plugin.directory.shared.Person;


public class DirectoryController extends PluginController implements IDirectoryController{

	private DirectoryModel mModel;
	private Iface mClient;
	
	/**
	 *  This name must match given in the Server.java file in plugin.launcher.server.
	 *  It's used to route the request to the right server implementation.
	 */
	private String mPluginName = "directory";
	
	public void onCreate() {
		// Initializing the model is part of the controller's job...
		mModel = new DirectoryModel();
		
		// ...as well as initializing the client.
		// The "client" is the connection we use to access the service.
		mClient = (Iface) getClient(new Client.Factory(), mPluginName);
	}
	
	@Override
	public PluginModel getModel() {
		return mModel;
	}

	

	@Override
	public void search(String name) {
		new DirectorySearchNameRequest().start(this, mClient, name );
		
	}

	@Override
	public void setResults(List<Person> res) {
		mModel.setResults(res);
		
	}

	



}
