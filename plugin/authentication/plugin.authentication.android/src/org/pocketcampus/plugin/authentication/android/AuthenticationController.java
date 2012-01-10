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

public class AuthenticationController extends PluginController implements IAuthenticationController {
	
	final static public String tequilaAuthTokenUrl = "https://tequila.epfl.ch/cgi-bin/tequila/requestauth?requestkey=%s";
	final static public String tequilaLoginUrl = "https://tequila.epfl.ch/cgi-bin/tequila/login";
	final static public String isaLoginUrl = "https://isa.epfl.ch/imoniteur_ISAP/!logins.tryToConnect";
	final static public String tequilaCookieName = "tequila_key";
	
	public static final boolean AUTHENTICATE_TEQUILAENABLEDSERVICES_LOCALLY = true;
	
	public static final RedirectHandler redirectNoFollow = new RedirectHandler() {
		public boolean isRedirectRequested(HttpResponse response, HttpContext context) {
			return false;
		}
		public URI getLocationURI(HttpResponse response, HttpContext context) throws org.apache.http.ProtocolException {
			return null;
		}
	};

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
	
	private AuthenticationModel mModel;
	private Iface mClient;
	
	/**
	 *  This name must match given in the Server.java file in plugin.launcher.server.
	 *  It's used to route the request to the right server implementation.
	 */
	private String mPluginName = "authentication";

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
	
	@Override
	public int onStartCommand(Intent aIntent, int flags, int startId) {
		if(aIntent == null)
			return START_NOT_STICKY;
		
		Log.v("DEBUG", "AuthenticationController::onStartCommand {act=" + aIntent.getAction() + "}");
		
		if(!"org.pocketcampus.plugin.authentication.ACTION_AUTHENTICATE".equals(aIntent.getAction()))
			return START_NOT_STICKY;
		Uri intentUri = aIntent.getData();
		if(intentUri == null)
			return START_NOT_STICKY;
		Log.v("DEBUG", "AuthenticationController::onStartCommand {uri=" + intentUri.toString() + "}");
		if("pocketcampus-logout".equals(intentUri.getScheme())) {
			Log.v("DEBUG", "AuthenticationController::onStartCommand {Logging out}");
			mModel.setTequilaCookie(null);
		}
		return START_NOT_STICKY;
	}
	
	@Override
	public PluginModel getModel() {
		return mModel;
	}
	
	//////////////////////

	public static TypeOfService mapHostToTypeOfService(Uri aData) {
		// This is the host part of the URL that Tequila redirects to after successful authentication
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
	
	public static TypeOfService mapQueryParameterToTypeOfService(Uri aData) {
		// This is the QueryParameter "service" that is set by a plugin calling us and asking for authentication
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
	
	//////////////////
	
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

	//////////////////

	public void nSetTypeOfService(TypeOfService tos) {
		iTypeOfService = tos;
	}
	
	public void nSetLocalCredentials(String user, String pass) {
		iLocalCredentials.username = user;
		iLocalCredentials.password = pass;
	}

	//////////////////////////
	
	public void nGetTequilaCookie() {
		new LoginToTequilaRequest().start(this, threadSafeClient, iLocalCredentials);
	}
	
	public void nGetTequilaKey() {
		new GetTequilaKeyForServiceRequest().start(this, mClient, iTypeOfService);
	}
	
	public void nAuthenticateToken() {
		TokenCookieComplex tc = new TokenCookieComplex();
		tc.cookie = mModel.getTequilaCookie();
		tc.token = mModel.getTequilaKey();
		new AuthenticateTokenWithTequilaRequest().start(this, threadSafeClient, tc);
	}
	
	public void nGetSessionId() {
		new GetSessionIdForServiceRequest().start(this, mClient, mModel.getTequilaKey());
	}
	
	public void nGetSessionIdDirectlyFromProvider() {
		TOSCredentialsComplex tc = new TOSCredentialsComplex();
		tc.tos = iTypeOfService;
		tc.credentials = iLocalCredentials;
		new GetSessionIdDirectlyFromProviderRequest().start(this, threadSafeClient, tc);
	}
	
	//////////////////////////
	
	private DefaultHttpClient threadSafeClient = null;
	
	private LocalCredentials iLocalCredentials = new LocalCredentials();
	private TypeOfService iTypeOfService;

}
