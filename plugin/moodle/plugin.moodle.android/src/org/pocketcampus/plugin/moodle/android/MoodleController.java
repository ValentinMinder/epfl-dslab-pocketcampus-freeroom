package org.pocketcampus.plugin.moodle.android;

import static org.pocketcampus.platform.android.core.PCAndroidConfig.PC_ANDR_CFG;

import java.io.File;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.Comparator;
import java.util.Locale;

import org.apache.http.impl.client.DefaultHttpClient;
import org.pocketcampus.platform.android.cache.RequestCache;
import org.pocketcampus.platform.android.core.AuthenticationListener;
import org.pocketcampus.platform.android.core.GlobalContext;
import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginModel;
import org.pocketcampus.plugin.moodle.R;
import org.pocketcampus.plugin.moodle.android.iface.IMoodleController;
import org.pocketcampus.plugin.moodle.android.iface.IMoodleView;
import org.pocketcampus.plugin.moodle.android.req.CoursesListRequest;
import org.pocketcampus.plugin.moodle.android.req.DownloadMoodleFileRequest;
import org.pocketcampus.plugin.moodle.android.req.PrintFileRequest;
import org.pocketcampus.plugin.moodle.android.req.SectionsListRequest;
import org.pocketcampus.plugin.moodle.shared.MoodleCourse2;
import org.pocketcampus.plugin.moodle.shared.MoodleCourseSectionsRequest2;
import org.pocketcampus.plugin.moodle.shared.MoodleCoursesRequest2;
import org.pocketcampus.plugin.moodle.shared.MoodlePrintFileRequest2;
import org.pocketcampus.plugin.moodle.shared.MoodleService.Client;
import org.pocketcampus.plugin.moodle.shared.MoodleService.Iface;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

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
			authIntent.setClassName(context.getApplicationContext(), MoodleController.class.getName());
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
			intenteye.setClassName(context.getApplicationContext(), MoodleController.class.getName());
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

		// initialize ImageLoader
		ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
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
	
	public void refreshCourseList(IMoodleView caller, boolean useCache) {
		MoodleCoursesRequest2 req = new MoodleCoursesRequest2(Locale.getDefault().getLanguage());
		new CoursesListRequest(caller).setBypassCache(!useCache).start(this, mClient, req);
	}

	public void refreshCourseSections(IMoodleView caller, int courseId, boolean useCache) {
		MoodleCourseSectionsRequest2 req = new MoodleCourseSectionsRequest2(Locale.getDefault().getLanguage(), courseId);
		new SectionsListRequest(caller).setBypassCache(!useCache).start(this, mClient, req);
	}
	
	public void fetchFileResource(IMoodleView caller, String filePath) {
		new DownloadMoodleFileRequest(caller, getHttpPost(mPluginName)).start(this, threadSafeClient, filePath);
	}
	
	public void printFileResource(IMoodleView caller, String filePath) {
		new PrintFileRequest(caller).start(this, mClient, new MoodlePrintFileRequest2(filePath));
	}
	

	/*****
	 * HELPER CLASSES AND FUNCTIONS
	 */
	
	public static Comparator<MoodleCourse2> getMoodleCourseItemComp4sort() {
		return new Comparator<MoodleCourse2>() {
			public int compare(MoodleCourse2 lhs, MoodleCourse2 rhs) {
				return rhs.getName().compareTo(lhs.getName());
			}
		};
	}
	

	public static void openFile(Context c, File file) {
		Uri uri = Uri.fromFile(file);
		Intent viewFileIntent = new Intent(Intent.ACTION_VIEW);
		String guessedContentType = URLConnection.guessContentTypeFromName(file.getName());
		if(guessedContentType == null) {
			Toast.makeText(c.getApplicationContext(), c.getResources().getString(
					R.string.moodle_no_app_to_handle_filetype), Toast.LENGTH_SHORT).show();
			return;
		}
		viewFileIntent.setDataAndType(uri, guessedContentType);
		viewFileIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		try {
			c.startActivity(viewFileIntent);
		} catch(Exception e) {
			Toast.makeText(c.getApplicationContext(), c.getResources().getString(
					R.string.moodle_no_app_to_handle_filetype), Toast.LENGTH_SHORT).show();
		}
	}

	public static void shareFile(Context c, File file) {
		Uri uri = Uri.fromFile(file);
		Intent shareFileIntent = new Intent(Intent.ACTION_SEND);
		String guessedContentType = URLConnection.guessContentTypeFromName(file.getName());
		if(guessedContentType == null)
			guessedContentType = "*/*";
		shareFileIntent.setType(guessedContentType);
		shareFileIntent.putExtra(Intent.EXTRA_STREAM, uri);
		try {
			c.startActivity(shareFileIntent);
		} catch(Exception e) {
			Toast.makeText(c.getApplicationContext(), c.getResources().getString(
					R.string.moodle_no_app_to_handle_filetype), Toast.LENGTH_SHORT).show();
		}
	}
	
	public static void openPrintDialog(Context c, long printJobId, String fileName) {
		Uri.Builder builder = new Uri.Builder();
		builder.scheme("pocketcampus").authority("cloudprint.plugin.pocketcampus.org").appendPath("print");
		Intent i = new Intent(Intent.ACTION_VIEW, builder.build());
		i.putExtra("JOB_ID", printJobId);
		i.putExtra("FILE_NAME", fileName);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		c.startActivity(i);
	}
	

	public static String getPrettyName(String url) {
		if(url == null) return null;
		if(url.length() < 1) return "";
		url = url.split("[?]")[0];
		if(url.length() < 1) return "";
		url = url.substring(url.lastIndexOf("/") + 1);
		return URLDecoder.decode(url);
	}
	

	public static String getLocalPath(String url, boolean prepareFolder) {
		url = url.split("[?]")[0];
		String filePHP = "/pluginfile.php/";
		url = url.substring(url.indexOf(filePHP) + filePHP.length());
		url = URLDecoder.decode(url);
		url = getMoodleFilesPath() + url;
		File fileDir = new File(url.substring(0, url.lastIndexOf("/")));
		if(prepareFolder)
			fileDir.mkdirs();
		return url;
	}


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
