package org.pocketcampus.plugin.directory.android;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginModel;
import org.pocketcampus.plugin.directory.android.iface.IDirectoryController;
import org.pocketcampus.plugin.directory.android.iface.IDirectoryView;
import org.pocketcampus.plugin.directory.android.req.*;
import org.pocketcampus.plugin.directory.android.DirectoryModel;
import org.pocketcampus.plugin.directory.shared.DirectoryService.Client;
import org.pocketcampus.plugin.directory.shared.DirectoryService.Iface;
import org.pocketcampus.plugin.directory.shared.DirectoryRequest;
import org.pocketcampus.plugin.directory.shared.Person;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.text.TextUtils;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;


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
	private Iface mClientU;
	
	
	String query;
	SearchDirectoryRequest request;
	SearchBySciperRequest requestU;

		
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
		mClientU = (Iface) getClient(new Client.Factory(), mPluginName);
		
		// initialize ImageLoader
		ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getApplicationContext()));

	}
	
	/**
	 * Returns the associated model.
	 */
	@Override
	public PluginModel getModel() {
		return mModel;
	}



	/**
	 * Initiate search
	 */
	synchronized public boolean search(IDirectoryView caller, String q) {
		if(request != null && !request.getStatus().equals(AsyncTask.Status.FINISHED))
			return false;
		if(query != null && query.equals(q))
			return false;
		query = q;
		request = new SearchDirectoryRequest(caller);
		DirectoryRequest req = new DirectoryRequest(query);
		req.setLanguage(Locale.getDefault().getLanguage());
		request.start(this, mClient, req);
		return true;
	}


	/**
	 * Initiate uniquely identified person search
	 */
	synchronized public void searchBySciper(IDirectoryView caller, String q) {
		if(requestU != null && !requestU.getStatus().equals(AsyncTask.Status.FINISHED))
			return;
		requestU = new SearchBySciperRequest(caller);
		DirectoryRequest req = new DirectoryRequest(q);
		req.setLanguage(Locale.getDefault().getLanguage());
		requestU.start(this, mClientU, req);
	}

	/**
	 * Import to phone book
	 */
	public static void importContact(Context c, Person p) {
		Intent addContactIntent = new Intent(Intent.ACTION_INSERT);
		addContactIntent.setType(ContactsContract.Contacts.CONTENT_TYPE);
		addContactIntent.putExtra(ContactsContract.Intents.Insert.NAME, getFullName(p));
		addContactIntent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK);
		addContactIntent.putExtra(ContactsContract.Intents.Insert.PHONE, p.getOfficePhoneNumber());
		addContactIntent.putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME);
		addContactIntent.putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE, p.getPrivatePhoneNumber());
		addContactIntent.putExtra(ContactsContract.Intents.Insert.POSTAL_TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK);
		addContactIntent.putExtra(ContactsContract.Intents.Insert.POSTAL, p.getOffice());
		addContactIntent.putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK);
		addContactIntent.putExtra(ContactsContract.Intents.Insert.EMAIL, p.getEmail());
		if(p.isSetOrganisationalUnits() && p.getOrganisationalUnitsIterator().hasNext())
			addContactIntent.putExtra(ContactsContract.Intents.Insert.COMPANY, TextUtils.join(", ", p.getOrganisationalUnits()));
		addContactIntent.putExtra(ContactsContract.Intents.Insert.NOTES, p.getWeb());
		//addContactIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		c.startActivity(addContactIntent);
	}

	/**
	 * HELPER generate full name string outta person p
	 */
	public static String getFullName(Person p) {
		List<String> l = new LinkedList<String>();
		if(p.isSetFirstName()) l.add(p.getFirstName());
		if(p.isSetLastName()) l.add(p.getLastName());
		return TextUtils.join(" ", l);
	}


}
