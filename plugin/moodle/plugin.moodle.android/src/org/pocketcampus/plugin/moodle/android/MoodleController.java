package org.pocketcampus.plugin.moodle.android;

import static org.pocketcampus.android.platform.sdk.core.PCAndroidConfig.PC_ANDR_CFG;

import java.io.File;

import org.apache.http.impl.client.DefaultHttpClient;
import org.pocketcampus.android.platform.sdk.cache.RequestCache;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.moodle.android.iface.IMoodleController;
import org.pocketcampus.plugin.moodle.android.iface.IMoodleView;
import org.pocketcampus.plugin.moodle.android.req.CoursesListRequest;
import org.pocketcampus.plugin.moodle.android.req.FetchMoodleResourceRequest;
import org.pocketcampus.plugin.moodle.android.req.SectionsListRequest;
import org.pocketcampus.plugin.moodle.android.MoodleModel;
import org.pocketcampus.plugin.moodle.shared.MoodleService.Client;
import org.pocketcampus.plugin.moodle.shared.MoodleService.Iface;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

/**
 * MoodleController - Main logic for the Moodle Plugin.
 * 
 * This class issues requests to the Moodle PocketCampus
 * server to get the Moodle data of the logged in user.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class MoodleController extends PluginController implements IMoodleController{

	public static class Logouter extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.v("DEBUG", "MoodleController$Logouter logging out");
			Intent authIntent = new Intent("org.pocketcampus.plugin.authentication.LOGOUT",
					Uri.parse("pocketcampus://moodle.plugin.pocketcampus.org/logout"));
			context.startService(authIntent);
		}
	};

//	final public static RedirectHandler redirectNoFollow = new RedirectHandler() {
//		public boolean isRedirectRequested(HttpResponse response, HttpContext context) {
//			return false;
//		}
//		public URI getLocationURI(HttpResponse response, HttpContext context) throws org.apache.http.ProtocolException {
//			return null;
//		}
//	};

	/**
	 *  This name must match given in the Server.java file in plugin.launcher.server.
	 *  It's used to route the request to the right server implementation.
	 */
	private String mPluginName = "moodle";
	
	/**
	 * HTTP Client used to communicate directly with servers.
	 * Used to communicate with Tequila Server, ISA Server, etc.
	 */
	private DefaultHttpClient threadSafeClient = null;
	
	/**
	 * Stores reference to the Model associated with this plugin.
	 */
	private MoodleModel mModel;
	
	/**
	 * HTTP Clients used to communicate with the PocketCampus server.
	 * Use thrift to transport the data.
	 */
	private Iface mClient;
	
	@Override
	public void onCreate() {
		mModel = new MoodleModel(getApplicationContext());
		mClient = (Iface) getClient(new Client.Factory(), mPluginName);
		threadSafeClient = getThreadSafeClient();
//		threadSafeClient.setRedirectHandler(redirectNoFollow);
	}
	
	@Override
	public int onStartCommand(Intent aIntent, int flags, int startId) {
		if("org.pocketcampus.plugin.authentication.AUTHENTICATION_FINISHED".equals(aIntent.getAction())) {
			Bundle extras = aIntent.getExtras();
			if(extras != null && extras.getInt("selfauthok") != 0) {
				Log.v("DEBUG", "MoodleController::onStartCommand auth succ");
				mModel.getListenersToNotify().authenticationFinished();
			} else if(extras != null && extras.getInt("usercancelled") != 0) {
				Log.v("DEBUG", "MoodleController::onStartCommand user cancelled");
				mModel.getListenersToNotify().userCancelledAuthentication();
			} else {
				Log.v("DEBUG", "MoodleController::onStartCommand auth failed");
				mModel.getListenersToNotify().authenticationFailed();
			}
		}
		if("org.pocketcampus.plugin.authentication.LOGOUT".equals(aIntent.getAction())) {
			Log.v("DEBUG", "MoodleController::onStartCommand logout");
			RequestCache.invalidateCache(this, CoursesListRequest.class.getCanonicalName());
			RequestCache.invalidateCache(this, SectionsListRequest.class.getCanonicalName());
		}
		stopSelf();
		return START_NOT_STICKY;
	}
	
	@Override
	public PluginModel getModel() {
		return mModel;
	}
	
	public static String getLocalPath(String fileName) {
		final String filePHP = "/pluginfile.php/";
		fileName = fileName.substring(fileName.indexOf(filePHP) + filePHP.length());
		String extStr = Environment.getExternalStorageDirectory().getAbsolutePath();
		fileName = extStr + "/" + PC_ANDR_CFG.getString("SDCARD_FILES_PATH") + "/moodle/" + fileName;
		File fileDir = new File(fileName.substring(0, fileName.lastIndexOf("/")));
		fileDir.mkdirs();
		return fileName;
	}

	public void refreshCourseList(IMoodleView caller, boolean useCache) {
		new CoursesListRequest(caller).setBypassCache(!useCache).start(this, mClient, null);
	}

	public void refreshCourseSections(IMoodleView caller, String courseId, boolean useCache) {
		new SectionsListRequest(caller).setBypassCache(!useCache).start(this, mClient, courseId);
	}
	
	public void fetchFileResource(IMoodleView caller, String filePath) {
		new FetchMoodleResourceRequest(caller, getHttpPost(mPluginName)).start(this, threadSafeClient, filePath);
	}
	
	public static void pingAuthPlugin(Context context) {
		Intent authIntent = new Intent("org.pocketcampus.plugin.authentication.ACTION_AUTHENTICATE",
				Uri.parse("pocketcampus://authentication.plugin.pocketcampus.org/authenticatetoken"));
		authIntent.putExtra("callbackurl", "pocketcampus://moodle.plugin.pocketcampus.org/tokenauthenticated");
		authIntent.putExtra("selfauth", true);
		context.startService(authIntent);
	}
	
}
