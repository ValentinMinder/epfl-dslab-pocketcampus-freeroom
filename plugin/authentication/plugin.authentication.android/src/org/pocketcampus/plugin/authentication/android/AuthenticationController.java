package org.pocketcampus.plugin.authentication.android;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.authentication.android.iface.IAuthenticationController;
import org.pocketcampus.plugin.authentication.android.req.GetSessionIdForServiceRequest;
import org.pocketcampus.plugin.authentication.android.req.GetTequilaKeyForServiceRequest;
import org.pocketcampus.plugin.authentication.shared.AuthenticationService.Client;
import org.pocketcampus.plugin.authentication.shared.AuthenticationService.Iface;
import org.pocketcampus.plugin.authentication.shared.SessionId;
import org.pocketcampus.plugin.authentication.shared.TequilaKey;
import org.pocketcampus.plugin.authentication.shared.TypeOfService;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class AuthenticationController extends PluginController implements IAuthenticationController {

	public class LocalCredentials {
		public String username;
		public String password;
	}
	
	private AuthenticationModel mModel;
	private Iface mClient;
	
	final static private String tequilaUrl = "https://tequila.epfl.ch/cgi-bin/tequila/requestauth?requestkey=%s"; 
	
	/**
	 *  This name must match given in the Server.java file in plugin.launcher.server.
	 *  It's used to route the request to the right server implementation.
	 */
	private String mPluginName = "authentication";

	@Override
	public void onCreate() {
		// Initializing the model is part of the controller's job...
		mModel = new AuthenticationModel();
		
		// ...as well as initializing the client.
		// The "client" is the connection we use to access the service.
		mClient = (Iface) getClient(new Client.Factory(), mPluginName);
	}
	
	@Override
	public PluginModel getModel() {
		return mModel;
	}

	@Override
	public void setLocalCredentials(String user, String pass) {
		iLocalCredentials.username = user;
		iLocalCredentials.password = pass;
	}
	
	@Override
	public void authenticateUserForService(TypeOfService tos) {
		if(tos == TypeOfService.SERVICE_ISA) { // add all services that require local authentication here
			authenticateUserLocallyForService(tos);
		} else {
			new GetTequilaKeyForServiceRequest().start(this, mClient, tos);
		}
	}
	public void gotTequilaKeyForService(TequilaKey key) {
		mModel.setTequilaKey(key);
		openBrowserWithUrl(String.format(tequilaUrl, key.getITequilaKey()));
	}
	public void forwardTequilaKeyForService(Uri aData) {
		if(aData == null)
			return;
		String pcService = aData.getHost();
		//String teqKey = aData.getQueryParameter("key");
		
		TequilaKey storedTeqKey = mModel.getTequilaKey();
		if(storedTeqKey == null) {
			Log.e("PocketCampusAuthPlugin", "forwardTequilaKeyForService: storedTeqKey is null");
			return;
		}
		TequilaKey teqKey = new TequilaKey();
		//teqKey.setITequilaKey(key);
		if("login.pocketcampus.org".equals(pcService)) {
			teqKey.setTos(TypeOfService.SERVICE_POCKETCAMPUS);
		} else if("moodle.epfl.ch".equals(pcService)) {
			teqKey.setTos(TypeOfService.SERVICE_MOODLE);
		} else if("cmp2www.epfl.ch".equals(pcService)) {
			teqKey.setTos(TypeOfService.SERVICE_CAMIPRO);
		} else {
			Log.e("PocketCampusAuthPlugin", "forwardTequilaKeyForService: Cannot find corresponding TypeOfService");
			return;
		}
		if(storedTeqKey.getTos() != teqKey.getTos()) {
			Log.e("PocketCampusAuthPlugin", "forwardTequilaKeyForService: TypeOfService did not match with stored value");
			return;
		}
		new GetSessionIdForServiceRequest().start(this, mClient, storedTeqKey);
	}
	public void gotSessionIdForService(SessionId sessId) {
		mModel.setSessionIdForService(sessId.getTos(), sessId);
		forwardSessionIdToCaller(sessId);
	}
	
	private void openBrowserWithUrl(String url) {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(browserIntent);
	}

	private void forwardSessionIdToCaller(SessionId sessId) {
		String url = "pocketcampus-authenticate://%s.plugin.pocketcampus.org/auth_done?sessid=%s";
		switch(sessId.getTos()) {
		case SERVICE_POCKETCAMPUS:
			url = String.format(url, "pocketcampus", Uri.encode(sessId.getPocketCampusSessionId()));
			break;
		case SERVICE_MOODLE:
			url = String.format(url, "moodle", Uri.encode(sessId.getMoodleCookie()));
			break;
		case SERVICE_CAMIPRO:
			url = String.format(url, "camipro", Uri.encode(sessId.getCamiproCookie()));
			break;
		case SERVICE_ISA:
			url = String.format(url, "isa", Uri.encode(sessId.getIsaCookie()));
			break;
		default:
			// error
			return;
		}
		Intent callerIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		callerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(callerIntent);
		mModel.setMustFinish();
	}

	private void authenticateUserLocallyForService(TypeOfService tos) {
		SessionId sessId = null;
		if(tos == TypeOfService.SERVICE_ISA) {
			try {
				sessId = loginToIsa(iLocalCredentials.username, iLocalCredentials.password);
				if(sessId == null) {
					//TODO tell view that credentials are bad, let the user try again
					return;
				}
			} catch (IOException e) {
				e.printStackTrace();
				//TODO tell view that network error happened
				return;
			}
		} else {
			//TODO this is an ERROR TypeOfService is unknown
		}
		if(sessId != null) {
			forwardSessionIdToCaller(sessId);
		} else {
			//TODO this is an ERROR
		}
	}
	
	private SessionId loginToIsa(String username, String password) throws IOException {
		//HashMap<String, String> hm = new HashMap<String, String>();
		//hm.put("ww_x_username", "test-scala");
		//hm.put("ww_x_password", "potiron");
		//String reply = HttpUtils.postForm("https://isadev.epfl.ch/imoniteur_ISAN/!logins.tryToConnect", new HashMap<String, String>(), hm);

		
		//HttpParams params = new BasicHttpParams(); // setup whatever params you what
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost("https://isa.epfl.ch/imoniteur_ISAP/!logins.tryToConnect");
		
		List<NameValuePair> l = new LinkedList<NameValuePair>();
		l.add(new BasicNameValuePair("ww_x_username", username));
		l.add(new BasicNameValuePair("ww_x_password", password));
		post.setEntity(new UrlEncodedFormEntity(l)); // with list of key-value pairs
		//post.setHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-GB; rv:1.9.2.6) Gecko/20100625 Firefox/3.6.6");
		/*client.execute(post, new ResponseHandler(){
			public Object handleResponse(HttpResponse response)
					throws ClientProtocolException, IOException {
				System.out.println("reply=" + response.getLastHeader("Set-Cookie"));
				return null;
			}});*/
		HttpResponse resp = client.execute(post);
		List<Cookie> lc = client.getCookieStore().getCookies();
		for(Cookie c : lc) {
			System.out.println("cookie=" + c.getName() + ": " + c.getValue());
			//ISA-CNXKEY: 853654DF9F5454C7A80C466A113A6758
			if("ISA-CNXKEY".equals(c.getName())) {
				SessionId sessId = new SessionId(TypeOfService.SERVICE_ISA);
				sessId.setIsaCookie(c.getValue());
				return sessId;
			}
		}
		/*final String responseText =  EntityUtils.toString(resp.getEntity());
		System.out.println("reply=" + responseText);*/
		return null;
	}
	
	private LocalCredentials iLocalCredentials = new LocalCredentials();

}
