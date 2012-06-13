package org.pocketcampus.plugin.moodle.android;

import java.io.File;
import java.net.URI;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.RedirectHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.authentication.shared.SessionId;
import org.pocketcampus.plugin.authentication.shared.TypeOfService;
import org.pocketcampus.plugin.moodle.android.iface.IMoodleController;
import org.pocketcampus.plugin.moodle.android.req.CoursesListRequest;
import org.pocketcampus.plugin.moodle.android.req.EventsListRequest;
import org.pocketcampus.plugin.moodle.android.req.FetchMoodleResourceRequest;
import org.pocketcampus.plugin.moodle.android.req.GetMoodleSessionRequest;
import org.pocketcampus.plugin.moodle.android.req.GetTequilaTokenRequest;
import org.pocketcampus.plugin.moodle.android.req.SectionsListRequest;
import org.pocketcampus.plugin.moodle.android.MoodleModel;
import org.pocketcampus.plugin.moodle.android.MoodleModel.ResourceCookieComplex;
import org.pocketcampus.plugin.moodle.shared.MoodleRequest;
import org.pocketcampus.plugin.moodle.shared.MoodleService.Client;
import org.pocketcampus.plugin.moodle.shared.MoodleService.Iface;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

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

	final public static RedirectHandler redirectNoFollow = new RedirectHandler() {
		public boolean isRedirectRequested(HttpResponse response, HttpContext context) {
			return false;
		}
		public URI getLocationURI(HttpResponse response, HttpContext context) throws org.apache.http.ProtocolException {
			return null;
		}
	};

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
	private Iface mClientCL;
	private Iface mClientEL;
	private Iface mClientSL;
	
	@Override
	public void onCreate() {
		mModel = new MoodleModel(getApplicationContext());
		mClient = (Iface) getClient(new Client.Factory(), mPluginName);
		mClientCL = (Iface) getClient(new Client.Factory(), mPluginName);
		mClientEL = (Iface) getClient(new Client.Factory(), mPluginName);
		mClientSL = (Iface) getClient(new Client.Factory(), mPluginName);
		threadSafeClient = getThreadSafeClient();
		threadSafeClient.setRedirectHandler(redirectNoFollow);
	}
	
	@Override
	public int onStartCommand(Intent aIntent, int flags, int startId) {
		if("org.pocketcampus.plugin.authentication.ACTION_AUTHENTICATE".equals(aIntent.getAction())) {
			Uri intentUri = aIntent.getData();
			if(intentUri != null && "pocketcampus-authenticated".equals(intentUri.getScheme())) {
				Bundle extras = aIntent.getExtras();
				if(extras != null && extras.getString("tequilatoken") != null) {
					mModel.getListenersToNotify().tokenAuthenticationFinished();
				} else {
					// TODO figure out what to do
				}
			}
		}
		stopSelf();
		return START_NOT_STICKY;
	}
	
	@Override
	public PluginModel getModel() {
		return mModel;
	}
	
	public static String getLocalPath(String fileName) {
		final String filePHP = "/file.php/";
		fileName = fileName.substring(fileName.indexOf(filePHP) + filePHP.length());
		String extStr = Environment.getExternalStorageDirectory().getAbsolutePath();
		fileName = extStr + "/PCMoodle/" + fileName;
		File fileDir = new File(fileName.substring(0, fileName.lastIndexOf("/")));
		fileDir.mkdirs();
		return fileName;
	}

	public void getTequilaToken() {
		new GetTequilaTokenRequest().start(this, mClient, null);
	}
	
	public void getMoodleSession() {
		new GetMoodleSessionRequest().start(this, mClient, mModel.getTequilaToken());
	}
	
	public void refreshCoursesList(boolean skipCache) {
		if(mModel.getMoodleCookie() == null)
			getTequilaToken();
		else
			new CoursesListRequest().setBypassCache(skipCache).start(this, mClientCL, buildSessionId(null));
	}
	
	public void refreshEventsList(boolean skipCache) {
		if(mModel.getMoodleCookie() == null)
			getTequilaToken();
		else
			new EventsListRequest().setBypassCache(skipCache).start(this, mClientEL, buildSessionId(null));
	}
	
	public void refreshSectionsList(boolean skipCache, Integer courseId) {
		if(courseId == null)
			return;
		if(mModel.getMoodleCookie() == null)
			getTequilaToken();
		else
			new SectionsListRequest().setBypassCache(skipCache).start(this, mClientSL, buildSessionId(courseId));
	}
	
	public void fetchFileResource(String mr) {
		if(mModel.getMoodleCookie() == null)
			return;
		if(mr == null)
			return;
		ResourceCookieComplex rc = new ResourceCookieComplex();
		rc.cookie = mModel.getMoodleCookie();
		rc.resource = mr;
		new FetchMoodleResourceRequest().start(this, threadSafeClient, rc);
	}
	
	private MoodleRequest buildSessionId(Integer courseId) {
		SessionId sessId = new SessionId(TypeOfService.SERVICE_MOODLE);
		sessId.setMoodleCookie(mModel.getMoodleCookie());
		MoodleRequest cr = new MoodleRequest();
		cr.setILanguage(Locale.getDefault().getLanguage());
		cr.setISessionId(sessId);
		if(courseId != null)
			cr.setICourseId(courseId);
		return cr;
	}
	
}
