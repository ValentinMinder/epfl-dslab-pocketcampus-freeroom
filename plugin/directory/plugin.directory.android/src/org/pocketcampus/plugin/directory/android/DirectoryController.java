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


/**
 * Controller for the Directory plugin. 
 * Handles the request from the plugin to the server, i.e. the autocomplete on name, the picture url request and searching for a specific name. 
 * @author Pascal <pascal.scheiben@epfl.ch>
 *
 */
public class DirectoryController extends PluginController implements IDirectoryController{

	/** Model of this plugin **/
	private DirectoryModel mModel;
	
	/** Client for the requests**/
	private Iface mClient;
	
	/**
	 *  This name must match given in the Server.java file in plugin.launcher.server.
	 *  It's used to route the request to the right server implementation.
	 */
	private String mPluginName = "directory";
	
	/** 
	 * Initializing.
	 */
	public void onCreate() {
		// Initializing the model is part of the controller's job...
		mModel = new DirectoryModel();
		
		// ...as well as initializing the client.
		// The "client" is the connection we use to access the service.
		mClient = (Iface) getClient(new Client.Factory(), mPluginName);
	}
	
	/**
	 * Returns the associated model.
	 */
	@Override
	public PluginModel getModel() {
		return mModel;
	}

	/**
	 * Makes a request to the server to get the url of the picture of a specific user via his sciper number.
	 * @param sciper Sciper number of the researched person
	 */
	@Override
	public void getProfilePicture(String sciper) {
		new DirectoryGetPictureRequest().start(this, mClient, sciper);
		
	}
	
	/**
	 * Makes a request to the server to get a list of <code> Person</code>
	 * @param name The name you are looking for
	 */
	@Override
	public void search(String name) {
		new DirectorySearchNameRequest().start(this, mClient, name );
		
	}

	/**
	 * Makes a request to the server to get autocomplete over a partial name.
	 * @param txt The partial name you want to autocomplete
	 */
	@Override
	public void getAutoCompleted(String txt) {
		new DirectoryAutoCompleteRequest().start(this,
				(Iface) getClient(new Client.Factory(), mPluginName),
				txt);
		
	}

	/**
	 * Sets the result in the model.
	 */
	@Override
	public void setResults(List<Person> res) {
		mModel.setResults(res);
		
	}
}
