package org.pocketcampus.plugin.moodle.android;

import static org.pocketcampus.platform.android.core.PCAndroidConfig.PC_ANDR_CFG;

import java.io.File;

import org.apache.http.impl.client.DefaultHttpClient;
import org.pocketcampus.platform.android.cache.RequestCache;
import org.pocketcampus.platform.android.core.AuthenticationListener;
import org.pocketcampus.platform.android.core.GlobalContext;
import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginModel;
import org.pocketcampus.plugin.moodle.android.iface.IMoodleController;
import org.pocketcampus.plugin.moodle.android.iface.IMoodleView;
import org.pocketcampus.plugin.moodle.android.req.CoursesListRequest;
import org.pocketcampus.plugin.moodle.android.req.FetchMoodleResourceRequest;
import org.pocketcampus.plugin.moodle.android.req.SectionsListRequest;
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


	public static class AuthListener extends AuthenticationListener {
		@Override
		public void onReceive(Context context, Intent intent) {
			super.onReceive(context, intent);
			Log.v("DEBUG", "MoodleController$AuthListener auth finished");
			Intent intenteye = new Intent("org.pocketcampus.plugin.authentication.AUTHENTICATION_FINISHED", 
					Uri.parse("pocketcampus://moodle.plugin.pocketcampus.org/auth_finished"));
			if(intent.getIntExtra("selfauthok", 0) != 0)
				intenteye.putExtra("selfauthok", 1);
			if(intent.getIntExtra("usercancelled", 0) != 0)
				intenteye.putExtra("usercancelled", 1);
			context.startService(intenteye);
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
				mClient = (Iface) getClient(new Client.Factory(), mPluginName); // need to recreate thrift client coz old one will not have the sessId http header attached
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
			deleteRecursive(new File(getMoodleFilesPath()));
		}
		stopSelf();
		return START_NOT_STICKY;
	}
	
	@Override
	public PluginModel getModel() {
		return mModel;
	}
	
	public static String getLocalPath(String fileName, boolean prepareFolder) {
		final String filePHP = "/pluginfile.php/";
		fileName = fileName.substring(fileName.indexOf(filePHP) + filePHP.length());
		fileName = getMoodleFilesPath() + fileName;
		File fileDir = new File(fileName.substring(0, fileName.lastIndexOf("/")));
		if(prepareFolder)
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
				Uri.parse("pocketcampus://authentication.plugin.pocketcampus.org/authenticate"));
		authIntent.putExtra("selfauth", true);
		context.startService(authIntent);
	}
	
	public static boolean sessionExists(Context context) {
		return ((GlobalContext) context.getApplicationContext()).hasPcSessionId();
	}
	
	public static void deleteRecursive(File fileOrDirectory) {
	    if (fileOrDirectory.isDirectory())
	        for (File child : fileOrDirectory.listFiles())
	            deleteRecursive(child);

	    fileOrDirectory.delete();
	}
	
	public static String getMoodleFilesPath() {
		String extStr = Environment.getExternalStorageDirectory().getAbsolutePath();
		return  extStr + "/" + PC_ANDR_CFG.getString("SDCARD_FILES_PATH") + "/moodle/files/";

	}
	
}
