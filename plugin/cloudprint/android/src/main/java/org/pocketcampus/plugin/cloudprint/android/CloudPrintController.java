package org.pocketcampus.plugin.cloudprint.android;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.impl.client.DefaultHttpClient;
import org.pocketcampus.platform.android.core.AuthenticationListener;
import org.pocketcampus.platform.android.core.GlobalContext;
import org.pocketcampus.platform.android.core.LogoutListener;
import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginModel;
import org.pocketcampus.platform.shared.PCConstants;
import org.pocketcampus.plugin.cloudprint.R;
import org.pocketcampus.plugin.cloudprint.android.iface.ICloudPrintController;
import org.pocketcampus.plugin.cloudprint.android.iface.ICloudPrintView;
import org.pocketcampus.plugin.cloudprint.android.req.PrintFileRequest;
import org.pocketcampus.plugin.cloudprint.android.req.PrintPreviewRequest;
import org.pocketcampus.plugin.cloudprint.android.req.UploadFileRequest;
import org.pocketcampus.plugin.cloudprint.shared.CloudPrintColorConfig;
import org.pocketcampus.plugin.cloudprint.shared.CloudPrintDoubleSidedConfig;
import org.pocketcampus.plugin.cloudprint.shared.CloudPrintMultiPageConfig;
import org.pocketcampus.plugin.cloudprint.shared.CloudPrintMultipleCopies;
import org.pocketcampus.plugin.cloudprint.shared.CloudPrintOrientation;
import org.pocketcampus.plugin.cloudprint.shared.CloudPrintPageRange;
import org.pocketcampus.plugin.cloudprint.shared.CloudPrintService.Client;
import org.pocketcampus.plugin.cloudprint.shared.CloudPrintService.Iface;
import org.pocketcampus.plugin.cloudprint.shared.PrintDocumentRequest;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;


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


	public static class Logouter extends LogoutListener {
		@Override
		public void onReceive(Context context, Intent intent) {
			super.onReceive(context, intent);
			Log.v("DEBUG", "CloudPrintController$Logouter logging out");
			Intent authIntent = new Intent("org.pocketcampus.plugin.authentication.LOGOUT",
					Uri.parse("pocketcampus://cloudprint.plugin.pocketcampus.org/logout"));
			authIntent.setClassName(context.getApplicationContext(), CloudPrintController.class.getName());
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
			intenteye.setClassName(context.getApplicationContext(), CloudPrintController.class.getName());
			context.startService(intenteye);
		}
	};
	
	
	
	public static class CloudPrintImageLoader extends ImageLoader {

	    private volatile static CloudPrintImageLoader instance;

	    /** Returns singletone class instance */
	    public static CloudPrintImageLoader getInstance() {
	        if (instance == null) {
	            synchronized (CloudPrintImageLoader.class) {
	                if (instance == null) {
	                    instance = new CloudPrintImageLoader();
	                }
	            }
	        }
	        return instance;
	    }
	}
	
	
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

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext()).imageDownloader(
						new BaseImageDownloader(getApplicationContext()) {
							@Override
						    protected HttpURLConnection createConnection(String url, Object extra) throws IOException {
						        HttpURLConnection conn = super.createConnection(url, extra);
						        if(extra != null)
						        	conn.setRequestProperty(PCConstants.HTTP_HEADER_AUTH_PCSESSID, extra.toString());
						        return conn;
						    }
						} ).build();
		CloudPrintImageLoader.getInstance().init(config);
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
			mClient = (Iface) getClient(new Client.Factory(), mPluginName);
		}
		stopSelf();
		return START_NOT_STICKY;
	}
	
	

	public void printFileJob(ICloudPrintView caller, long documentId, boolean previewOnly) {
		PrintDocumentRequest req = new PrintDocumentRequest(documentId);
		req.setPageSelection(mModel.getPageRangeList().get(mModel.getSelPageRangeList()));
		req.setMultiPageConfig(mModel.getMultiPageList().get(mModel.getSelMultiPageList()));
		req.setDoubleSided(mModel.getDoubleSidedList().get(mModel.getSelDoubleSidedList()));
		req.setOrientation(mModel.getOrientationList().get(mModel.getSelOrientationList()));
		req.setMultipleCopies(mModel.getMultipleCopiesList().get(mModel.getSelMultipleCopiesList()));
		req.setColorConfig(mModel.getColorConfigList().get(mModel.getSelColorConfigList()));
		if(previewOnly)
			new PrintPreviewRequest(caller).start(this, mClient, req);
		else
			new PrintFileRequest(caller).start(this, mClient, req);
	}
	
	public void uploadFileToPrint(ICloudPrintView caller, File file) {
		new UploadFileRequest(caller, getHttpPost(mPluginName)).start(this, threadSafeClient, file);
	}
	
	public String getPageThumbnailUrl() {
		return getBackendUrl(mPluginName, true) + "?file_id=" + mModel.getPrintJobId() + "&page=" + mModel.getCurrPage();
	}

	
	
	/***
	 * HELPERS
	 */


	public static void pingAuthPlugin(Context context) {
		Intent authIntent = new Intent("org.pocketcampus.plugin.authentication.ACTION_AUTHENTICATE",
				Uri.parse("pocketcampus://authentication.plugin.pocketcampus.org/authenticate"));
		authIntent.putExtra("selfauth", true);
		authIntent.setClassName(context.getApplicationContext(), "org.pocketcampus.plugin.authentication.android.AuthenticationController");
		context.startService(authIntent);
	}
	
	public static boolean sessionExists(Context context) {
		return ((GlobalContext) context.getApplicationContext()).hasPcSessionId();
	}
	
	
	
	
	
	public String getLocalizedStringByName(String prefix, String aString) {
		String packageName = getPackageName();
		int resId = getResources().getIdentifier(prefix + aString, "string", packageName);
		if(resId == 0)
			return aString;
		return getString(resId);
	}

	
	
	


	public String colorConfigToDisplayString(CloudPrintColorConfig s) {
		if(s == null) 
			return getString(R.string.cloudprint_string_color);
		switch (s) {
		case BLACK_WHITE:
			return getString(R.string.cloudprint_string_blackwhite);
		case COLOR:
			return getString(R.string.cloudprint_string_color);
		}
		return null;
	}
	

	public String multipleCopiesToDisplayString(CloudPrintMultipleCopies s) {
		if(s == null) 
			return getString(R.string.cloudprint_string_one_copy);
		return getString(R.string.cloudprint_string_n_copies, s.getNumberOfCopies()) + getString(s.isCollate() ? R.string.cloudprint_string_comma_collate : R.string.cloudprint_string_comma_do_not_collate);
	}
	

	public String orientationToDisplayString(CloudPrintOrientation s) {
		if(s == null) 
			return getString(R.string.cloudprint_string_portrait);
		switch (s) {
		case PORTRAIT:
			return getString(R.string.cloudprint_string_portrait);
		case LANDSCAPE:
			return getString(R.string.cloudprint_string_landscape);
		case REVERSE_PORTRAIT:
			return getString(R.string.cloudprint_string_reverse_portrait);
		case REVERSE_LANDSCAPE:
			return getString(R.string.cloudprint_string_reverse_landscape);
		}
		return null;
	}
	

	public String doubleSidedToDisplayString(CloudPrintDoubleSidedConfig s) {
		if(s == null) 
			return getString(R.string.cloudprint_string_one_sided);
		switch (s) {
		case LONG_EDGE:
			return getString(R.string.cloudprint_string_two_sided_long_edge);
		case SHORT_EDGE:
			return getString(R.string.cloudprint_string_two_sided_short_edge);
		}
		return null;
	}
	

	public String multiPageToDisplayString(CloudPrintMultiPageConfig s) {
		if(s == null) 
			return getString(R.string.cloudprint_string_one_page_per_sheet);
		return getString(R.string.cloudprint_string_n_pages_per_sheet, s.getNbPagesPerSheet().getValue()) + ", " + getLocalizedStringByName("cloudprint_enum_layout_short_", s.getLayout().name());
	}
	
	
	public String pageRangeToDisplayString(CloudPrintPageRange s) {
		if(s == null) 
			return getString(R.string.cloudprint_string_entire_document);
		return getString(R.string.cloudprint_string_pages_x_to_y, s.getPageFrom(), s.getPageTo());
	}

	
	
	public static String[] generateArray(int start, int end, int step) {
		List<String> list = new LinkedList<String>();
		for(int i = start; i < end; i += step) {
			list.add("" + i);
		}
		return list.toArray(new String[list.size()]);
	}
	
}
