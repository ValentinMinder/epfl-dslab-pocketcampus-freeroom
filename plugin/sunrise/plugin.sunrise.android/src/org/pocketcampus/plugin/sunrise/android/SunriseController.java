package org.pocketcampus.plugin.sunrise.android;

import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.RedirectHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.sunrise.android.iface.ISunriseController;
import org.pocketcampus.plugin.sunrise.android.req.SendSunriseSmsRequest;
import org.pocketcampus.plugin.sunrise.android.SunriseModel;

/**
 * SunriseController - Main logic for the Sunrise Plugin.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class SunriseController extends PluginController implements ISunriseController{

	final public static RedirectHandler redirectNoFollow = new RedirectHandler() {
		public boolean isRedirectRequested(HttpResponse response, HttpContext context) {
			return false;
		}
		public URI getLocationURI(HttpResponse response, HttpContext context) throws org.apache.http.ProtocolException {
			return null;
		}
	};

	/**
	 * Utility class ResourceCookieComplex
	 */
	public static class LocalCredentials {
		public String username;
		public String password;
	}
	public static class SunriseSmsSend {
		public LocalCredentials credentials;
		public String recipient;
		public String message;
		public boolean loginOnly;
	}
	public static class SunriseSmsSendResult {
		public LocalCredentials credentials;
		public int remainingFreeSms;
		public int status;
	}
	
	/**
	 * HTTP Client used to communicate directly with servers.
	 * Used to communicate with Tequila Server, ISA Server, etc.
	 */
	private DefaultHttpClient threadSafeClient = null;
	
	/**
	 * Stores reference to the Model associated with this plugin.
	 */
	private SunriseModel mModel;
	
	
	@Override
	public void onCreate() {
		mModel = new SunriseModel(getApplicationContext());
		threadSafeClient = getThreadSafeClient();
		threadSafeClient.setRedirectHandler(redirectNoFollow);
	}
	
	@Override
	public PluginModel getModel() {
		return mModel;
	}
	
	public void loginToSunrise(String user, String pass) {
		SunriseSmsSend sss = new SunriseSmsSend();
		sss.credentials = new LocalCredentials();
		sss.credentials.username = user;
		sss.credentials.password = pass;
		sss.loginOnly = true;
		new SendSunriseSmsRequest().start(this, threadSafeClient, sss);
	}
	
	public void sendSMS(String recipient, String message) {
		LocalCredentials lc = mModel.getSunriseCredentials();
		if(lc == null)
			return;
		SunriseSmsSend sss = new SunriseSmsSend();
		sss.recipient = recipient;
		sss.message = message;
		sss.credentials = lc;
		new SendSunriseSmsRequest().start(this, threadSafeClient, sss);
	}
	
}
