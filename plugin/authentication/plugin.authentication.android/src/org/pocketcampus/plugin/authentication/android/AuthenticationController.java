package org.pocketcampus.plugin.authentication.android;

import java.net.URI;
import java.util.Arrays;

import org.apache.http.HttpResponse;
import org.apache.http.client.RedirectHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.pocketcampus.platform.android.core.AuthenticationListener;
import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginModel;
import org.pocketcampus.plugin.authentication.R;
import org.pocketcampus.plugin.authentication.android.AuthenticationModel.LocalCredentials;
import org.pocketcampus.plugin.authentication.android.AuthenticationModel.TokenCredentialsComplex;
import org.pocketcampus.plugin.authentication.android.iface.IAuthenticationController;
import org.pocketcampus.plugin.authentication.android.iface.IAuthenticationView;
import org.pocketcampus.plugin.authentication.android.req.AuthenticateTokenWithTequilaRequest;
import org.pocketcampus.plugin.authentication.android.req.FetchUserAttributes;
import org.pocketcampus.plugin.authentication.android.req.GetPcSessionFromTequilaReq;
import org.pocketcampus.plugin.authentication.android.req.GetPcTokenFromTequilaReq;
import org.pocketcampus.plugin.authentication.android.req.GetServiceDetailsRequest;
import org.pocketcampus.plugin.authentication.android.req.LoginToTequilaRequest;
import org.pocketcampus.plugin.authentication.shared.AuthenticationService.Client;
import org.pocketcampus.plugin.authentication.shared.AuthenticationService.Iface;
import org.pocketcampus.plugin.authentication.shared.UserAttributesRequest;
import org.pocketcampus.plugin.authentication.shared.authenticationConstants;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

/**
 * <b>AuthenticationController - Main logic for the Authentication Plugin.</b>
 * <br><br>
 * 
 * This file contains the main logic behind the successful functioning
 * of the Authentication Plugin.
 * Here is how this Plugin works:
 * 
 * <ol type="1">
 *   <li>We receive an Intent/Request to authenticate the user
 *   for a certain service.</li>
 *   <li>We do the authentication magic.<br>
 *   In this step there are two cases:
 *   <ol type="I">
 *     <li>Case I: The service is not Tequila enabled. e.g. ISA.<br>
 *     We follow the below steps.
 *     <ol type="a">
 *       <li>We ask the user for credentials.</li>
 *       <li>We send a GetSessionIdDirectlyFromProviderRequest.</li>
 *       <li>We either getSessionId or notifyBadCredentials or notifyNetworkError.<br>
 *       If we get a sessionId we forward it to the specific plugin.<br>
 *       In the other cases we let the user try again.</li>
 *     </ol>
 *     </li>
 *     <li>Case II: The service is Tequila enabled. e.g. Camipro.<br>
 *     We check if we have a TequilaCookie then we skip to step (d).
 *     <ol type="a">
 *       <li>We ask the user for credentials.</li>
 *       <li>We send a LoginToTequilaRequest.</li>
 *       <li>We either getTequilaCookie or notifyBadCredentials or notifyNetworkError.<br>
 *       If we get a tequilaCookie we continue, otherwise we let the user try again.</li>
 *       <li>We send a GetTequilaKeyForServiceRequest.</li>
 *       <li>We either getToken or notifyNetworkError. If we getToken we continue.</li>
 *       <li>We send an AuthenticateTokenWithTequilaRequest.</li>
 *       <li>We either getAuthenticatedToken or notifyExpiredCookie or notifyNetworkError.<br>
 *       If we getAuthenticatedToken we continue.<br>
 *       If we notifyExpiredCookie we go back to (a).</li>
 *       <li>We send a GetSessionIdForServiceRequest.</li>
 *       <li>We either getSessionId or notifyNetworkError.<br>
 *       If we getSessionId we forward it to the specific plugin.</li>
 *     </ol>
 *     </li>
 *   </ol>
 *   </li>
 *   <li>We callback the requesting plugin and provide it with
 *   a valid sessionId (using Intents too).</li>
 * </ol>
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class AuthenticationController extends PluginController implements IAuthenticationController {
	
	/**
	 * Some constants.
	 */
	final public static String tequilaAuthTokenUrl = "https://tequila.epfl.ch/cgi-bin/tequila/requestauth?requestkey=%s";
	final public static String tequilaLoginUrl = "https://tequila.epfl.ch/cgi-bin/tequila/login";
	final public static String isaLoginUrl = "https://isa.epfl.ch/imoniteur_ISAP/!logins.tryToConnect";
	final public static String tequilaCookieName = "tequila_key";
	final public static String callBackIntent = "pocketcampus-authenticate://%s.plugin.pocketcampus.org/auth_done?sessid=%s";
	final public static RedirectHandler redirectNoFollow = new RedirectHandler() {
		public boolean isRedirectRequested(HttpResponse response, HttpContext context) {
			return false;
		}
		public URI getLocationURI(HttpResponse response, HttpContext context) throws org.apache.http.ProtocolException {
			return null;
		}
	};
	
	public static final String OAUTH2_AUTH_URL = "https://tequila.epfl.ch/cgi-bin/OAuth2IdP/auth?response_type=code&redirect_uri=https%3A%2F%2Fpocketcampus.epfl.ch%2F&client_id=1b74e3837e50e21afaf2005f%40epfl.ch&scope=" + TextUtils.join(",", authenticationConstants.OAUTH2_SCOPES);

	/**
	 *  This name must match given in the Server.java file in plugin.launcher.server.
	 *  It's used to route the request to the right server implementation.
	 */
	private String mPluginName = "authentication";
	
	/**
	 * Stores reference to the Model associated with this plugin.
	 */
	private AuthenticationModel mModel;

	/**
	 * HTTP Clients used to communicate with the PocketCampus server.
	 * Use thrift to transport the data.
	 */
	private Iface mClient;

	public Iface getThriftClient() {
		return mClient;
	}
	
	/**
	 * HTTP Client used to communicate directly with servers.
	 * Used to communicate with Tequila Server, ISA Server, etc.
	 */
	private DefaultHttpClient threadSafeClient = null;
	
	/**
	 * Builds the Controller.
	 * Here we instantiate the Model.
	 * We also initialize the HTTP clients.
	 */
	@Override
	public void onCreate() {
		// Initializing the model is part of the controller's job...
		mModel = new AuthenticationModel(getApplicationContext());

		mClient = (Iface) getClient(new Client.Factory(), mPluginName);

		threadSafeClient = getThreadSafeClient();
		threadSafeClient.setRedirectHandler(redirectNoFollow);
	}
	
	/**
	 * This takes care of the silent communication with other plugins.
	 * If other plugins need to talk to this plugin silently,
	 * they send an intent using the startService method.
	 * Here is where we receive those intents.
	 * An example of such intents is the logout intent.
	 */
	@Override
	public int onStartCommand(Intent aIntent, int flags, int startId) {
		
		boolean argsOk = false;
		// parse args
		if("org.pocketcampus.plugin.authentication.ACTION_AUTHENTICATE".equals(aIntent.getAction())) {
			Uri intentUri = aIntent.getData();
			if(intentUri != null && "pocketcampus".equals(intentUri.getScheme())) {
				Bundle extras = aIntent.getExtras();
				if(extras != null &&
						( (extras.getString("callbackurl") != null && extras.getString("tequilatoken") != null) || 
								extras.getBoolean("selfauth")) ) {
					mModel.setCallbackUrl(extras.getString("callbackurl"));
					mModel.setTequilaToken(extras.getString("tequilatoken"));
					mModel.setSelfAuth(extras.getBoolean("selfauth"));
					mModel.setFromBrowser(false);
					argsOk = true;
				}
			}
		}
		if(!argsOk) {
			stopSelf();
			return START_NOT_STICKY;
		}

		if(mModel.getSelfAuth()) {
			mModel.setTequilaToken(null);
		}

		if(mModel.getSelfAuth() && mModel.getPcSessionId() != null)
			getUserAttributes(null, true);
		else 
			startAuth();
		return START_NOT_STICKY;
	}
	
	public void getUserAttributes(IAuthenticationView caller, boolean bypassCache) {
		new FetchUserAttributes(caller).setBypassCache(bypassCache).start(this, mClient, new UserAttributesRequest(mModel.getPcSessionId(), Arrays.asList("firstname", "lastname")));
	}
	
	public void startAuth() {
		if(mModel.getSavedGasparPassword() != null) {
			// use saved password
			mModel.setTempGasparPassword(mModel.getSavedGasparPassword());
			startPreLogin();
		} else {
			// ELSE open dialog to login
			openDialog(null);
		}
	}
	

	/**
	 * Returns the Model associated with this plugin.
	 */
	@Override
	public PluginModel getModel() {
		return mModel;
	}
	
	private void openDialog(String extra) {
		Intent intenteye = new Intent(this, AuthenticationView.class);
		if(extra != null)
			intenteye.putExtra(extra, 1);
		intenteye.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intenteye);
	}
	
	private void pingBack(String tequilaToken, String extra) {
		if(mModel.getFromBrowser() && mModel.getCallbackUrl() != null) {
			Intent intenteye = new Intent(Intent.ACTION_VIEW, Uri.parse(mModel.getCallbackUrl()));
			intenteye.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intenteye);
		} else if(mModel.getSelfAuth()) {
			Intent intent = new Intent();
			intent.setAction("org.pocketcampus.plugin.authentication.AUTHENTICATION_FINISHED");
			intent.putExtra(AuthenticationListener.PC_SESSION_ID_EXTRA, mModel.getPcSessionId());
			if(extra != null)
				intent.putExtra(extra, 1);
			sendBroadcast(intent, "org.pocketcampus.permissions.AUTHENTICATE_WITH_TEQUILA"); 
		} else if(mModel.getCallbackUrl() != null) {
			Intent intenteye = new Intent("org.pocketcampus.plugin.authentication.AUTHENTICATION_FINISHED", Uri.parse(mModel.getCallbackUrl()));
			if(tequilaToken != null)
				intenteye.putExtra("tequilatoken", tequilaToken); // success
			if(extra != null)
				intenteye.putExtra(extra, 1); // user cancelled, user denied, or invalid token (in case of success this could indicate if plugin should not store session; in case of selfAuth for PC this indicates success)
			startService(intenteye);
		}
		mModel.mListeners.shouldFinish();
	}
	
	private void checkServiceAuthorized() {
		Log.v("DEBUG", "checkServiceAuthorized");
		if(mModel.getTequilaToken() == null) {
			Log.v("DEBUG", "[fatal] no tequila token {shutting down}");
			stopSelf();
			return;
		}
		if(mModel.getServiceName() == null) {
			Log.v("DEBUG", "[fatal] no short name {shutting down}");
			stopSelf();
			return;
		}
		
		if(mModel.getServiceAllowedLevel(mModel.getServiceName()) == 0) {
			// open dialog to ask for permission
			Log.v("DEBUG", "asking for user permission");
			openDialog("askpermission");
			return;
		}
		if(mModel.getServiceAllowedLevel(mModel.getServiceName()) > 0) {
			// allow silently
			Log.v("DEBUG", "authenticating tequila token");
			authenticateToken();
		} else {
			// refuse silently
			Log.v("DEBUG", "token authentication refused");
			pingBack(null, "userdenied");
			stopSelf();
		}
	}
	
	public void startActualLogin() {
		LocalCredentials lc = new LocalCredentials();
		lc.username = mModel.getGasparUsername();
		lc.password = mModel.getTempGasparPassword();
		new LoginToTequilaRequest().start(this, threadSafeClient, lc);
	}

	public void authenticateToken() {
		TokenCredentialsComplex tc = new TokenCredentialsComplex();
		tc.token = mModel.getTequilaToken();
		tc.username = mModel.getGasparUsername();
		tc.password = mModel.getTempGasparPassword();
		new AuthenticateTokenWithTequilaRequest().start(this, threadSafeClient, tc);
	}
	
	public void cancelAuth() {
		pingBack(null, "usercancelled");
	}
	
	////////////// BUTTON HANDLERS
	
	public void startPreLogin() {
		if(mModel.getTequilaToken() != null)
			new GetServiceDetailsRequest().start(this, threadSafeClient, mModel.getTequilaToken());
		else 
			//new GetPcTokenRequest().start(this, mClient, null);
			new GetPcTokenFromTequilaReq().start(this, mClient, null);
	}

	public void allowService(boolean always) {
		if(always)
			mModel.setServiceAllowedLevel(mModel.getServiceName(), 1);
		authenticateToken();
	}
	
	public void denyService(boolean always) {
		if(always)
			mModel.setServiceAllowedLevel(mModel.getServiceName(), -1);
		pingBack(null, "userdenied");
		stopSelf();
	}
	
	///////////// CALLBACKS

	public void fetchedServiceDetails() {
		Log.v("DEBUG", "fetchedServiceDetails");
		if(mModel.getNotFromEpfl())
			openDialog("doshibboleth");
		else
			startActualLogin();
	}
	
	public void tequilaLoginFinished(String tequilaCookie) {
		Log.v("DEBUG", "tequilaLoginFinished");
		mModel.setTequilaCookie(tequilaCookie);
		if(mModel.getStorePassword()) {
			mModel.setSavedGasparPassword(mModel.getTempGasparPassword());
		}
		checkServiceAuthorized();
	}
	
	public void tokenAuthenticationFinished() {
		Log.v("DEBUG", "tokenAuthenticationFinished");
		if(mModel.getSelfAuth()) {
			//AuthSessionRequest req = new AuthSessionRequest(mModel.getTequilaToken());
			//req.setRememberMe(mModel.getStorePassword());
			//new GetPcSessionRequest().start(this, mClient, req);
			new GetPcSessionFromTequilaReq().start(this, mClient, mModel.getTequilaToken());
		} else {
			pingBack(mModel.getTequilaToken(), ( mModel.getStorePassword() ? null : "forcereauth" ));
			stopSelf();
		}
	}
	
	public void pcAuthenticationFinished(String sessId) {
		Log.v("DEBUG", "pcAuthenticationFinished");
		mModel.setPcSessionId(sessId);
		getUserAttributes(null, true);
	}
	
	public void sessionIsValid() {
		pingBack(null, "selfauthok");
		stopSelf();
	}
	
	//// NACKS
	
	public void sessionIsInvalid() {
		startAuth();
	}
	
	public void notifyInvalidToken() {
		Log.v("DEBUG", "notifyInvalidToken");
		pingBack(null, "invalidtoken");
		stopSelf();
	}
	
	public void notifyNetworkError() {
		Log.v("DEBUG", "notifyNetworkError");
		Toast.makeText(getApplicationContext(), getResources().getString(R.string.authentication_connection_error_happened), Toast.LENGTH_SHORT).show();
		stopSelf();
	}
	
	public void notifyBadCredentials() {
		Log.v("DEBUG", "notifyBadCredentials");
		openDialog("badcredentials");
	}
	
}
