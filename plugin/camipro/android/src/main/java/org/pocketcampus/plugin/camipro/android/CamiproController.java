package org.pocketcampus.plugin.camipro.android;

import java.util.Locale;

import org.pocketcampus.platform.android.core.AuthenticationListener;
import org.pocketcampus.platform.android.core.GlobalContext;
import org.pocketcampus.platform.android.core.LogoutListener;
import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginModel;
import org.pocketcampus.plugin.camipro.android.iface.ICamiproController;
import org.pocketcampus.plugin.camipro.android.iface.ICamiproView;
import org.pocketcampus.plugin.camipro.android.req.BalanceAndTransactionsRequest;
import org.pocketcampus.plugin.camipro.android.req.SendLoadingInfoByEmailRequest;
import org.pocketcampus.plugin.camipro.android.req.StatsAndLoadingInfoRequest;
import org.pocketcampus.plugin.camipro.shared.CamiproRequest;
import org.pocketcampus.plugin.camipro.shared.CamiproService.Client;
import org.pocketcampus.plugin.camipro.shared.CamiproService.Iface;
import org.pocketcampus.plugin.camipro.shared.SessionId;

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
	
	public static class Logouter extends LogoutListener {
		@Override
		public void onReceive(Context context, Intent intent) {
			super.onReceive(context, intent);
			Log.v("DEBUG", "CamiproController$Logouter logging out");
			Intent authIntent = new Intent("org.pocketcampus.plugin.authentication.LOGOUT",
					Uri.parse("pocketcampus://camipro.plugin.pocketcampus.org/logout"));
			authIntent.setClassName(context.getApplicationContext(), CamiproController.class.getName());
			context.startService(authIntent);
		}
	};
	
	
	public static class AuthListener extends AuthenticationListener {
		@Override
		public void onReceive(Context context, Intent intent) {
			super.onReceive(context, intent);
			Log.v("DEBUG", "CamiproController$AuthListener auth finished");
			Intent intenteye = new Intent("org.pocketcampus.plugin.authentication.AUTHENTICATION_FINISHED", 
					Uri.parse("pocketcampus://camipro.plugin.pocketcampus.org/auth_finished"));
			if(intent.getIntExtra("selfauthok", 0) != 0)
				intenteye.putExtra("selfauthok", 1);
			if(intent.getIntExtra("usercancelled", 0) != 0)
				intenteye.putExtra("usercancelled", 1);
			intenteye.setClassName(context.getApplicationContext(), CamiproController.class.getName());
			context.startService(intenteye);
		}
	};


	private String mPluginName = "camipro";
	
	private CamiproModel mModel;
	private Iface mClientBT;
	private Iface mClientSL;
	private Iface mClientLE;
	
	@Override
	public void onCreate() {
		mModel = new CamiproModel(getApplicationContext());
		createThriftClients();
	}
	
	private void createThriftClients() {
		mClientBT = (Iface) getClient(new Client.Factory(), mPluginName);
		mClientSL = (Iface) getClient(new Client.Factory(), mPluginName);
		mClientLE = (Iface) getClient(new Client.Factory(), mPluginName);
	}
	
	@Override
	public int onStartCommand(Intent aIntent, int flags, int startId) {
		if("org.pocketcampus.plugin.authentication.AUTHENTICATION_FINISHED".equals(aIntent.getAction())) {
			Bundle extras = aIntent.getExtras();
			if(extras != null && extras.getInt("selfauthok") != 0) {
				Log.v("DEBUG", "CamiproController::onStartCommand auth succ");
				createThriftClients(); // need to recreate thrift client coz old one will not have the sessId http header attached
				mModel.getListenersToNotify().gotCamiproCookie();
			} else if(extras != null && extras.getInt("usercancelled") != 0) {
				Log.v("DEBUG", "CamiproController::onStartCommand user cancelled");
				mModel.getListenersToNotify().userCancelledAuthentication();
			} else {
				Log.v("DEBUG", "CamiproController::onStartCommand auth failed");
				mModel.getListenersToNotify().authenticationFailed();
			}
		}
		if("org.pocketcampus.plugin.authentication.LOGOUT".equals(aIntent.getAction())) {
			Log.v("DEBUG", "CamiproController::onStartCommand logout");
			createThriftClients();
		}
		stopSelf();
		return START_NOT_STICKY;
	}
	

	
	
	@Override
	public PluginModel getModel() {
		return mModel;
	}

	public void refreshBalanceAndTransactions() {
		new BalanceAndTransactionsRequest().start(this, mClientBT, buildCamiproRequest());
	}
	
	public void refreshStatsAndLoadingInfo() {
		new StatsAndLoadingInfoRequest().start(this, mClientSL, buildCamiproRequest());
	}
	
	public void sendEmailWithLoadingDetails(CamiproCardRechargeView caller) {
		new SendLoadingInfoByEmailRequest(caller).start(this, mClientLE, buildCamiproRequest());
	}
	
	private CamiproRequest buildCamiproRequest() {
		SessionId sessId = new SessionId(0);
		sessId.setCamiproCookie("");
		CamiproRequest cr = new CamiproRequest();
		cr.setILanguage(Locale.getDefault().getLanguage());
		cr.setISessionId(sessId);
		return cr;
	}
	

//	public void gotTequilaToken() {
//		pingAuthPlugin(getApplicationContext(), mModel.getTequilaToken().getITequilaKey());
//	}
//
//	public void tokenAuthenticationFinished() {
//		getCamiproSession();
//	}
//
	public void notLoggedIn() {
		pingAuthPlugin(this);
//		mModel.setCamiproCookie(null);
//		getTequilaToken();
	}
	
//	public static void pingAuthPlugin(Context context, String tequilaToken) {
//		Intent authIntent = new Intent("org.pocketcampus.plugin.authentication.ACTION_AUTHENTICATE",
//				Uri.parse("pocketcampus://authentication.plugin.pocketcampus.org/authenticatetoken"));
//		authIntent.putExtra("tequilatoken", tequilaToken);
//		authIntent.putExtra("callbackurl", "pocketcampus://camipro.plugin.pocketcampus.org/tokenauthenticated");
//		authIntent.putExtra("shortname", "camipro");
//		authIntent.putExtra("longname", "Camipro");
//		context.startService(authIntent);
//	}
	
	

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
	
}
