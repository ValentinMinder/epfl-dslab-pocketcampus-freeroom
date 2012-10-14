package org.pocketcampus.plugin.camipro.android;

import java.util.Locale;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.camipro.shared.SessionId;
import org.pocketcampus.plugin.camipro.android.iface.ICamiproController;
import org.pocketcampus.plugin.camipro.android.req.BalanceAndTransactionsRequest;
import org.pocketcampus.plugin.camipro.android.req.GetCamiproSessionRequest;
import org.pocketcampus.plugin.camipro.android.req.GetTequilaTokenRequest;
import org.pocketcampus.plugin.camipro.android.req.SendLoadingInfoByEmailRequest;
import org.pocketcampus.plugin.camipro.android.req.StatsAndLoadingInfoRequest;
import org.pocketcampus.plugin.camipro.android.CamiproModel;
import org.pocketcampus.plugin.camipro.shared.CamiproRequest;
import org.pocketcampus.plugin.camipro.shared.CamiproService.Client;
import org.pocketcampus.plugin.camipro.shared.CamiproService.Iface;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

/**
 * CamiproController - Main logic for the Camipro Plugin.
 * 
 * This class issues requests to the Camipro PocketCampus
 * server to get the Camipro data of the logged in user.
 * It also allows the users to get the e-banking information
 * via email.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class CamiproController extends PluginController implements ICamiproController{
	
	public static class Logouter extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.v("DEBUG", "CamiproController$Logouter logging out");
			Intent authIntent = new Intent("org.pocketcampus.plugin.authentication.LOGOUT",
					Uri.parse("pocketcampus://camipro.plugin.pocketcampus.org/logout"));
			context.startService(authIntent);
		}
	};

	private String mPluginName = "camipro";
	
	private CamiproModel mModel;
	private Iface mClient;
	private Iface mClientBT;
	private Iface mClientSL;
	private Iface mClientLE;
	
	@Override
	public void onCreate() {
		mModel = new CamiproModel(getApplicationContext());
		mClient = (Iface) getClient(new Client.Factory(), mPluginName);
		mClientBT = (Iface) getClient(new Client.Factory(), mPluginName);
		mClientSL = (Iface) getClient(new Client.Factory(), mPluginName);
		mClientLE = (Iface) getClient(new Client.Factory(), mPluginName);
	}
	
	@Override
	public int onStartCommand(Intent aIntent, int flags, int startId) {
		if("org.pocketcampus.plugin.authentication.AUTHENTICATION_FINISHED".equals(aIntent.getAction())) {
			Bundle extras = aIntent.getExtras();
			if(extras != null && extras.getInt("usercancelled") != 0) {
				Log.v("DEBUG", "CamiproController::onStartCommand user cancelled");
				mModel.getListenersToNotify().userCancelledAuthentication();
			} else if(extras != null && extras.getString("tequilatoken") != null) {
				Log.v("DEBUG", "CamiproController::onStartCommand auth succ");
				if(extras.getInt("forcereauth") != 0)
					mModel.setForceReauth(true);
				tokenAuthenticationFinished();
			} else {
				Log.v("DEBUG", "CamiproController::onStartCommand auth failed");
				mModel.getListenersToNotify().authenticationFailed();
			}
		}
		if("org.pocketcampus.plugin.authentication.LOGOUT".equals(aIntent.getAction())) {
			Log.v("DEBUG", "CamiproController::onStartCommand logout");
			mModel.setCamiproCookie(null);
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
	
	public void getCamiproSession() {
		new GetCamiproSessionRequest().start(this, mClient, mModel.getTequilaToken());
	}
	
	public void refreshBalanceAndTransactions() {
		if(mModel.getCamiproCookie() == null)
			return;
		new BalanceAndTransactionsRequest().start(this, mClientBT, buildSessionId());
	}
	
	public void refreshStatsAndLoadingInfo() {
		if(mModel.getCamiproCookie() == null)
			return;
		new StatsAndLoadingInfoRequest().start(this, mClientSL, buildSessionId());
	}
	
	public void sendEmailWithLoadingDetails() {
		if(mModel.getCamiproCookie() == null)
			return;
		new SendLoadingInfoByEmailRequest().start(this, mClientLE, buildSessionId());
	}
	
	private CamiproRequest buildSessionId() {
		SessionId sessId = new SessionId(0);
		sessId.setCamiproCookie(mModel.getCamiproCookie());
		CamiproRequest cr = new CamiproRequest();
		cr.setILanguage(Locale.getDefault().getLanguage());
		cr.setISessionId(sessId);
		return cr;
	}
	

	public void gotTequilaToken() {
		pingAuthPlugin(getApplicationContext(), mModel.getTequilaToken().getITequilaKey());
	}

	public void tokenAuthenticationFinished() {
		getCamiproSession();
	}

	public void notLoggedIn() {
		mModel.setCamiproCookie(null);
		getTequilaToken();
	}
	
	public static void pingAuthPlugin(Context context, String tequilaToken) {
		Intent authIntent = new Intent("org.pocketcampus.plugin.authentication.ACTION_AUTHENTICATE",
				Uri.parse("pocketcampus://authentication.plugin.pocketcampus.org/authenticatetoken"));
		authIntent.putExtra("tequilatoken", tequilaToken);
		authIntent.putExtra("callbackurl", "pocketcampus://camipro.plugin.pocketcampus.org/tokenauthenticated");
		authIntent.putExtra("shortname", "camipro");
		authIntent.putExtra("longname", "Camipro");
		context.startService(authIntent);
	}
	
}
