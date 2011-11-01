package org.pocketcampus.plugin.directory.android;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.directory.android.iface.IDirectoryController;
import org.pocketcampus.plugin.directory.android.req.*;
import org.pocketcampus.plugin.directory.android.DirectoryModel;
import org.pocketcampus.plugin.directory.shared.DirectoryService.Client;
import org.pocketcampus.plugin.directory.shared.DirectoryService.Iface;
import org.pocketcampus.plugin.directory.shared.Person;

import android.content.Intent;
import android.widget.Toast;


public class DirectoryController extends PluginController implements IDirectoryController{

	private DirectoryModel mModel;
	private Iface mClient;
	
	/**
	 *  This name must match given in the Server.java file in plugin.launcher.server.
	 *  It's used to route the request to the right server implementation.
	 */
	private String mPluginName = "directory";
	
	private HashSet<String> ouToKeep;
	
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

	

	public HashSet<String> getOuToKeep() {
		return ouToKeep;
	}

	public void setOuToKeep(HashSet<String> ouToKeep) {
		this.ouToKeep = ouToKeep;
	}
	
	public void addOuToKeep(String ou){
		ouToKeep.add(ou);
	}

	@Override
	public void search(String name) {
		new DirectorySearchNameRequest().start(this, mClient, name );
		
	}

	@Override
	public void setResults(List<Person> res) {
		mModel.setResults(res);
		
	}

	public List<String> getOUTags() {
		return mModel.getOUList();
	}

	public void filterResults() {
		collectAllOU();
		ouToKeep = new HashSet<String>();
		startActivity(new Intent(this, DirectoryFilterOUView.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)); //TODO remove ugly
		RemoveUnwantedResults();
		
	}
	
	private void collectAllOU() {
		HashSet<String> allOU = new HashSet<String>();
		//String toast ="";
		for(Person p : mModel.getResults()){
			allOU.add(p.ou);
			//toast += " " + p.ou;
		}
		
		List<String> tmp = new ArrayList<String>();
		for(String ou : allOU)
			tmp.add(ou);
		mModel.setOUList(tmp);
	}

	private void RemoveUnwantedResults() {
		Iterator<Person> i = mModel.getResults().iterator();
		int j=0;
		while(i.hasNext()){
			if(!ouToKeep.contains( i.next().ou)){
				i.remove();
				j++;
			}
		}
		
		Toast.makeText(this,j+ " removed", Toast.LENGTH_SHORT).show();
		setResults(mModel.getResults());
	}

	



}
