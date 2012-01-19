package org.pocketcampus.plugin.authentication.android;

import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.RedirectHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.authentication.android.iface.IAuthenticationController;
import org.pocketcampus.plugin.authentication.android.req.AuthenticateTokenWithTequilaRequest;
import org.pocketcampus.plugin.authentication.android.req.GetSessionIdDirectlyFromProviderRequest;
import org.pocketcampus.plugin.authentication.android.req.GetSessionIdForServiceRequest;
import org.pocketcampus.plugin.authentication.android.req.GetTequilaKeyForServiceRequest;
import org.pocketcampus.plugin.authentication.android.req.LoginToTequilaRequest;
import org.pocketcampus.plugin.authentication.shared.AuthenticationService.Client;
import org.pocketcampus.plugin.authentication.shared.AuthenticationService.Iface;
import org.pocketcampus.plugin.authentication.shared.TequilaKey;
import org.pocketcampus.plugin.authentication.shared.TypeOfService;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

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

	/**
	 * Some utility classes.
	 */
	public class LocalCredentials {
		public String username;
		public String password;
	}
	public class TOSCredentialsComplex {
		public TypeOfService tos;
		public LocalCredentials credentials;
	}
	public class TokenCookieComplex {
		public TequilaKey token;
		public String cookie;
	}
	
	/**
	 * Stores reference to the Model associated with this plugin.
	 */
	private AuthenticationModel mModel;
	
	/**
	 * HTTP Client used to communicate with the PocketCampus server.
	 * Uses thrift to transport the data.
	 */
	private Iface mClient;
	
	/**
	 *  This name must match given in the Server.java file in plugin.launcher.server.
	 *  It's used to route the request to the right server implementation.
	 */
	private String mPluginName = "authentication";

	/**
	 * HTTP Client used to communicate directly with servers.
	 * Used to communicate with Tequila Server, ISA Server, etc.
	 */
	private DefaultHttpClient threadSafeClient = null;
	
	/**
	 * Temporarily stores the credentials of the user
	 * in order to authenticate them.
	 */
	private LocalCredentials iLocalCredentials = new LocalCredentials();
	
	/**
	 * Keeps track of the service that is requesting authentication.
	 */
	private TypeOfService iTypeOfService;

	/**
	 * Builds the Controller.
	 * Here we instantiate the Model.
	 * We also initialize the HTTP clients.
	 */
	@Override
	public void onCreate() {
		// Initializing the model is part of the controller's job...
		mModel = new AuthenticationModel(getApplicationContext());
		
		// ...as well as initializing the client.
		// The "client" is the connection we use to access the service.
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
		if("org.pocketcampus.plugin.authentication.ACTION_AUTHENTICATE".equals(aIntent.getAction())) {
			Uri intentUri = aIntent.getData();
			if(intentUri != null && "pocketcampus-logout".equals(intentUri.getScheme())) {
				Log.v("DEBUG", "AuthenticationController::onStartCommand {Logging out}");
				mModel.destroyTequilaCookie();
			}
		}
		stopSelf();
		return START_NOT_STICKY;
	}
	
	/**
	 * Returns the Model associated with this plugin.
	 */
	@Override
	public PluginModel getModel() {
		return mModel;
	}
	
	/**
	 * Helper function that maps host to TypeOfService.
	 * 
	 * It reads the host part of the URL that Tequila
	 * redirects to after successful authentication,
	 * and maps it to the corresponding TypeOfService.
	 * 
	 * @param aData is the URL that Tequila redirected to.
	 * @return the corresponding TypeOfService.
	 */
	public static TypeOfService mapHostToTypeOfService(Uri aData) {
		if(aData == null)
			return null;
		String pcService = aData.getHost();
		if(pcService == null)
			return null;
		if("login.pocketcampus.org".equals(pcService)) {
			return TypeOfService.SERVICE_POCKETCAMPUS;
		} else if("moodle.epfl.ch".equals(pcService)) {
			return TypeOfService.SERVICE_MOODLE;
		} else if("cmp2www.epfl.ch".equals(pcService)) {
			return TypeOfService.SERVICE_CAMIPRO;
		} else {
			return null;
		}
	}
	
	/**
	 * Helper function that maps QueryStringParameter to TypeOfService.
	 * 
	 * It reads the parameter "service" from the query string of the URI
	 * of the Intent that was sent by a plugin requesting authentication,
	 * and maps it to the corresponding TypeOfService.
	 * 
	 * @param aData is the URI of the Intent that we received.
	 * @return the corresponding TypeOfService.
	 */
	public static TypeOfService mapQueryParameterToTypeOfService(Uri aData) {
		if(aData == null)
			return null;
		String pcService = aData.getQueryParameter("service");
		if(pcService == null)
			return null;
		if("moodle".equals(pcService)) {
			return TypeOfService.SERVICE_MOODLE;
		} else if("camipro".equals(pcService)) {
			return TypeOfService.SERVICE_CAMIPRO;
		} else if("isacademia".equals(pcService)) {
			return TypeOfService.SERVICE_ISA;
		} else {
			return null;
		}
	}
	
	/**
	 * Helper function that returns true if the current service is tequila enabled.
	 */
	public boolean isTequilaEnabledService() {
		switch(iTypeOfService) {
		case SERVICE_CAMIPRO:
		case SERVICE_MOODLE:
		case SERVICE_POCKETCAMPUS:
			return true;
		case SERVICE_ISA:
			return false;
		default:
			throw new RuntimeException("isTequilaEnabledService: Unknown Service");
		}
	} 

	/**
	 * Sets the internal TypeOfService.
	 */
	public void nSetTypeOfService(TypeOfService tos) {
		iTypeOfService = tos;
	}

	/**
	 * Sets the internal LocalCredentials.
	 */
	public void nSetLocalCredentials(String user, String pass) {
		iLocalCredentials.username = user;
		iLocalCredentials.password = pass;
	}
	
	/**
	 * Gets the username from the LocalCredentials.
	 * 
	 * If the user types their password incorrectly,
	 * they will not have to retype their username.
	 * 
	 * @return the user's username
	 */
	public String nGetLastUsername() {
		return iLocalCredentials.username;
	}

	/**
	 * Initiates a LoginToTequilaRequest.
	 */
	public void nGetTequilaCookie() {
		new LoginToTequilaRequest().start(this, threadSafeClient, iLocalCredentials);
	}
	
	/**
	 * Initiates a GetTequilaKeyForServiceRequest.
	 */
	public void nGetTequilaKey() {
		new GetTequilaKeyForServiceRequest().start(this, mClient, iTypeOfService);
	}
	
	/**
	 * Initiates a AuthenticateTokenWithTequilaRequest.
	 */
	public void nAuthenticateToken() {
		TokenCookieComplex tc = new TokenCookieComplex();
		tc.cookie = mModel.getTequilaCookie();
		tc.token = mModel.getTequilaKey();
		new AuthenticateTokenWithTequilaRequest().start(this, threadSafeClient, tc);
	}
	
	/**
	 * Initiates a GetSessionIdForServiceRequest.
	 */
	public void nGetSessionId() {
		new GetSessionIdForServiceRequest().start(this, mClient, mModel.getTequilaKey());
	}
	
	/**
	 * Initiates a GetSessionIdDirectlyFromProviderRequest.
	 */
	public void nGetSessionIdDirectlyFromProvider() {
		TOSCredentialsComplex tc = new TOSCredentialsComplex();
		tc.tos = iTypeOfService;
		tc.credentials = iLocalCredentials;
		new GetSessionIdDirectlyFromProviderRequest().start(this, threadSafeClient, tc);
	}
	
}
