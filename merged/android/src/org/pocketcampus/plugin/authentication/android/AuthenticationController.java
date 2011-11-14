package org.pocketcampus.plugin.authentication.android;

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
	public void authenticateUserForService(TypeOfService tos) {
		new GetTequilaKeyForServiceRequest().start(this, mClient, tos);
	}
	public void gotTequilaKeyForService(TequilaKey key) {
		mModel.setTequilaKey(key);
		openBrowserWithUrl(String.format(tequilaUrl, key.getITequilaKey()));
	}
	public void forwardTequilaKeyForService(Uri aData) {
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
	}

	
	private void openBrowserWithUrl(String url) {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(browserIntent);
	}

}
