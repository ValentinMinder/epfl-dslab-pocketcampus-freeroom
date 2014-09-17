package org.pocketcampus.plugin.cloudprint.android;

import java.io.File;
import java.util.Locale;

import org.apache.http.impl.client.DefaultHttpClient;
import org.pocketcampus.platform.android.core.AuthenticationListener;
import org.pocketcampus.platform.android.core.GlobalContext;
import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginModel;
import org.pocketcampus.plugin.cloudprint.android.iface.ICloudPrintController;
import org.pocketcampus.plugin.cloudprint.android.iface.ICloudPrintView;
import org.pocketcampus.plugin.cloudprint.android.req.PrintFileRequest;
import org.pocketcampus.plugin.cloudprint.android.req.UploadFileRequest;
import org.pocketcampus.plugin.cloudprint.shared.CloudPrintColorConfig;
import org.pocketcampus.plugin.cloudprint.shared.CloudPrintDoubleSidedConfig;
import org.pocketcampus.plugin.cloudprint.shared.CloudPrintMultiPageConfig;
import org.pocketcampus.plugin.cloudprint.shared.CloudPrintMultiPageLayout;
import org.pocketcampus.plugin.cloudprint.shared.CloudPrintMultipleCopies;
import org.pocketcampus.plugin.cloudprint.shared.CloudPrintOrientation;
import org.pocketcampus.plugin.cloudprint.shared.CloudPrintPageRange;
import org.pocketcampus.plugin.cloudprint.shared.CloudPrintService.Client;
import org.pocketcampus.plugin.cloudprint.shared.CloudPrintService.Iface;
import org.pocketcampus.plugin.cloudprint.shared.PrintDocumentRequest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;


/**
 * CloudPrintController - Main logic for the CloudPrint Plugin.
 * 
 * This class issues requests to the CloudPrint PocketCampus
 * server to get the CloudPrint data of the logged in user.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class CloudPrintController extends PluginController implements ICloudPrintController{


	public static class Logouter extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.v("DEBUG", "CloudPrintController$Logouter logging out");
			Intent authIntent = new Intent("org.pocketcampus.plugin.authentication.LOGOUT",
					Uri.parse("pocketcampus://cloudprint.plugin.pocketcampus.org/logout"));
			context.startService(authIntent);
		}
	};


	public static class AuthListener extends AuthenticationListener {
		@Override
		public void onReceive(Context context, Intent intent) {
			super.onReceive(context, intent);
			Log.v("DEBUG", "CloudPrintController$AuthListener auth finished");
			Intent intenteye = new Intent("org.pocketcampus.plugin.authentication.AUTHENTICATION_FINISHED", 
					Uri.parse("pocketcampus://cloudprint.plugin.pocketcampus.org/auth_finished"));
			if(intent.getIntExtra("selfauthok", 0) != 0)
				intenteye.putExtra("selfauthok", 1);
			if(intent.getIntExtra("usercancelled", 0) != 0)
				intenteye.putExtra("usercancelled", 1);
			context.startService(intenteye);
		}
	};
	
	
	
	/**
	 *  This name must match given in the Server.java file in plugin.launcher.server.
	 *  It's used to route the request to the right server implementation.
	 */
	private String mPluginName = "cloudprint";
	
	/**
	 * Stores reference to the Model associated with this plugin.
	 */
	private CloudPrintModel mModel;
	
	/**
	 * HTTP Clients used to communicate with the PocketCampus server.
	 * Use thrift to transport the data.
	 */
	private Iface mClient;
	private DefaultHttpClient threadSafeClient = null;

	
	@Override
	public void onCreate() {
		mModel = new CloudPrintModel(getApplicationContext());
		mClient = (Iface) getClient(new Client.Factory(), mPluginName);
		threadSafeClient = getThreadSafeClient();


	}
	

	@Override
	public PluginModel getModel() {
		return mModel;
	}
	

	@Override
	public int onStartCommand(Intent aIntent, int flags, int startId) {
		if("org.pocketcampus.plugin.authentication.AUTHENTICATION_FINISHED".equals(aIntent.getAction())) {
			Bundle extras = aIntent.getExtras();
			if(extras != null && extras.getInt("selfauthok") != 0) {
				Log.v("DEBUG", "CloudPrintController::onStartCommand auth succ");
				mClient = (Iface) getClient(new Client.Factory(), mPluginName); // need to recreate thrift client coz old one will not have the sessId http header attached
				mModel.getListenersToNotify().authenticationFinished();
			} else if(extras != null && extras.getInt("usercancelled") != 0) {
				Log.v("DEBUG", "CloudPrintController::onStartCommand user cancelled");
				mModel.getListenersToNotify().userCancelledAuthentication();
			} else {
				Log.v("DEBUG", "CloudPrintController::onStartCommand auth failed");
				mModel.getListenersToNotify().authenticationFailed();
			}
		}
		if("org.pocketcampus.plugin.authentication.LOGOUT".equals(aIntent.getAction())) {
			Log.v("DEBUG", "CloudPrintController::onStartCommand logout");
			// do nothing
		}
		stopSelf();
		return START_NOT_STICKY;
	}
	
	

	public void printFileJob(ICloudPrintView caller, long documentId) {
		PrintDocumentRequest req = new PrintDocumentRequest(documentId);
		req.setPageSelection(mModel.getPageRangeList().get(mModel.getSelPageRangeList()));
		req.setMultiPageConfig(mModel.getMultiPageList().get(mModel.getSelMultiPageList()));
		req.setDoubleSided(mModel.getDoubleSidedList().get(mModel.getSelDoubleSidedList()));
		req.setOrientation(mModel.getOrientationList().get(mModel.getSelOrientationList()));
		req.setMultipleCopies(mModel.getMultipleCopiesList().get(mModel.getSelMultipleCopiesList()));
		req.setColorConfig(mModel.getColorConfigList().get(mModel.getSelColorConfigList()));
		new PrintFileRequest(caller).start(this, mClient, req);
	}
	
	public void uploadFileToPrint(ICloudPrintView caller, File file) {
		new UploadFileRequest(caller, getHttpPost(mPluginName)).start(this, threadSafeClient, file);
	}
	

	
	
	/***
	 * HELPERS
	 */


	public static void pingAuthPlugin(Context context) {
		Intent authIntent = new Intent("org.pocketcampus.plugin.authentication.ACTION_AUTHENTICATE",
				Uri.parse("pocketcampus://authentication.plugin.pocketcampus.org/authenticate"));
		authIntent.putExtra("selfauth", true);
		context.startService(authIntent);
	}
	
	public static boolean sessionExists(Context context) {
		return ((GlobalContext) context.getApplicationContext()).hasPcSessionId();
	}
	
	
	
	
	
	private static String decodeLayout(CloudPrintMultiPageLayout layout) {
		return layout.name().toLowerCase(Locale.US).replace("to_", "").replaceAll("([a-z])[a-z]+_([a-z])[a-z]+_([a-z])[a-z]+_([a-z])[a-z]+", "$1$2$3$4");
	}

	
	
	


	public String colorConfigToDisplayString(CloudPrintColorConfig s) {
		if(s == null) 
			return "Color";
		switch (s) {
		case BLACK_WHITE:
			return "Black/White";
		case COLOR:
			return "Color";
		}
		return null;
	}
	

	public String multipleCopiesToDisplayString(CloudPrintMultipleCopies s) {
		if(s == null) 
			return "1 copy";
		return s.getNumberOfCopies() + " copies" + (s.isCollate() ? ", collate" : "");
	}
	

	public String orientationToDisplayString(CloudPrintOrientation s) {
		if(s == null) 
			return "Portrait";
		switch (s) {
		case PORTRAIT:
			return "Portrait";
		case LANDSCAPE:
			return "Landscape";
		case REVERSE_PORTRAIT:
			return "Reverse portrait";
		case REVERSE_LANDSCAPE:
			return "Reverse landscape";
		}
		return null;
	}
	

	public String doubleSidedToDisplayString(CloudPrintDoubleSidedConfig s) {
		if(s == null) 
			return "1-sided";
		switch (s) {
		case LONG_EDGE:
			return "2-sided, long edge";
		case SHORT_EDGE:
			return "2-sided, short edge";
		}
		return null;
	}
	

	public String multiPageToDisplayString(CloudPrintMultiPageConfig s) {
		if(s == null) 
			return "1 page per sheet";
		return s.getNbPagesPerSheet().getValue() + " pages per sheet, " + decodeLayout(s.getLayout());
	}
	
	
	public String pageRangeToDisplayString(CloudPrintPageRange s) {
		if(s == null) 
			return "Entire document";
		return "Pages " + s.getPageFrom() + " to " + s.getPageTo();
	}

	
	
	
}
