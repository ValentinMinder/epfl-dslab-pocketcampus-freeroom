package org.pocketcampus.plugin.isacademia.android;

import java.net.URI;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.RedirectHandler;
import org.apache.http.protocol.HttpContext;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.isacademia.android.iface.IIsacademiaController;
import org.pocketcampus.plugin.isacademia.android.req.GetIsacademiaSessionRequest;
import org.pocketcampus.plugin.isacademia.android.req.GetTequilaTokenRequest;
import org.pocketcampus.plugin.isacademia.android.req.GetUserCoursesRequest;
import org.pocketcampus.plugin.isacademia.android.req.GetUserExamsRequest;
import org.pocketcampus.plugin.isacademia.android.req.GetUserScheduleRequest;
import org.pocketcampus.plugin.isacademia.shared.IsaRequest;
import org.pocketcampus.plugin.isacademia.shared.IsaSession;
import org.pocketcampus.plugin.isacademia.shared.IsacademiaService.Iface;
import org.pocketcampus.plugin.isacademia.shared.IsacademiaService.Client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

/**
 * IsacademiaController - Main logic for the Isacademia Plugin.
 * 
 * This class issues requests to the Isacademia PocketCampus
 * server to get the Isacademia data of the logged in user.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class IsacademiaController extends PluginController implements IIsacademiaController{

	public static class Logouter extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.v("DEBUG", "IsacademiaController$Logouter logging out");
			Intent authIntent = new Intent("org.pocketcampus.plugin.authentication.LOGOUT",
					Uri.parse("pocketcampus://isacademia.plugin.pocketcampus.org/logout"));
			context.startService(authIntent);
		}
	};

	final public static RedirectHandler redirectNoFollow = new RedirectHandler() {
		public boolean isRedirectRequested(HttpResponse response, HttpContext context) {
			return false;
		}
		public URI getLocationURI(HttpResponse response, HttpContext context) throws org.apache.http.ProtocolException {
			return null;
		}
	};

	private String mPluginName = "isacademia";
	
	private IsacademiaModel mModel;
	private Iface mClient;
	private Iface mClientC;
	private Iface mClientE;
	private Iface mClientS;
	
	@Override
	public void onCreate() {
		mModel = new IsacademiaModel(getApplicationContext());
		mClient = (Iface) getClient(new Client.Factory(), mPluginName);
		mClientC = (Iface) getClient(new Client.Factory(), mPluginName);
		mClientE = (Iface) getClient(new Client.Factory(), mPluginName);
		mClientS = (Iface) getClient(new Client.Factory(), mPluginName);
	}
	
	@Override
	public int onStartCommand(Intent aIntent, int flags, int startId) {
		if("org.pocketcampus.plugin.authentication.AUTHENTICATION_FINISHED".equals(aIntent.getAction())) {
			Bundle extras = aIntent.getExtras();
			if(extras != null && extras.getInt("usercancelled") != 0) {
				Log.v("DEBUG", "IsacademiaController::onStartCommand user cancelled");
				mModel.getListenersToNotify().userCancelledAuthentication();
			} else if(extras != null && extras.getString("tequilatoken") != null) {
				Log.v("DEBUG", "IsacademiaController::onStartCommand auth succ");
				if(extras.getInt("forcereauth") != 0)
					mModel.setForceReauth(true);
				tokenAuthenticationFinished();
			} else {
				Log.v("DEBUG", "IsacademiaController::onStartCommand auth failed");
				mModel.getListenersToNotify().authenticationFailed();
			}
		}
		if("org.pocketcampus.plugin.authentication.LOGOUT".equals(aIntent.getAction())) {
			Log.v("DEBUG", "IsacademiaController::onStartCommand logout");
			mModel.setIsacademiaCookie(null);
		}
		stopSelf();
		return START_NOT_STICKY;
	}
	
	@Override
	public PluginModel getModel() {
		return mModel;
	}

	public void getTequilaToken() {
		new GetTequilaTokenRequest().start(this, mClient, null);
	}
	
	public void getIsacademiaSession() {
		new GetIsacademiaSessionRequest().start(this, mClient, mModel.getTequilaToken());
	}
	
	public void refreshCourses() {
		if(mModel.getIsacademiaCookie() == null)
			getTequilaToken();
		else
			new GetUserCoursesRequest().start(this, mClientC, buildSessionId());
	}
	
	public void refreshExams() {
		if(mModel.getIsacademiaCookie() == null)
			getTequilaToken();
		else
			new GetUserExamsRequest().start(this, mClientE, buildSessionId());
	}
	
	public void refreshSchedule() {
		if(mModel.getIsacademiaCookie() == null)
			getTequilaToken();
		else
			new GetUserScheduleRequest().start(this, mClientS, buildSessionId());
	}
	
	private IsaRequest buildSessionId() {
		IsaRequest ir = new IsaRequest();
		ir.setIsaSession(new IsaSession(mModel.getIsacademiaCookie()));
		ir.setILanguage(Locale.getDefault().getLanguage());
		return ir;
	}
	
	public void gotTequilaToken() {
		pingAuthPlugin(getApplicationContext(), mModel.getTequilaToken().getITequilaKey());
	}

	public void tokenAuthenticationFinished() {
		getIsacademiaSession();
	}

	public void notLoggedIn() {
		mModel.setIsacademiaCookie(null);
		getTequilaToken();
	}
	
	public static void pingAuthPlugin(Context context, String tequilaToken) {
		Intent authIntent = new Intent("org.pocketcampus.plugin.authentication.ACTION_AUTHENTICATE",
				Uri.parse("pocketcampus://authentication.plugin.pocketcampus.org/authenticatetoken"));
		authIntent.putExtra("tequilatoken", tequilaToken);
		authIntent.putExtra("callbackurl", "pocketcampus://isacademia.plugin.pocketcampus.org/tokenauthenticated");
		authIntent.putExtra("shortname", "isacademia");
		authIntent.putExtra("longname", "IS-Academia");
		context.startService(authIntent);
	}
	
}
