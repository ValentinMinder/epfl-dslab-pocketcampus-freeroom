package org.pocketcampus.platform.android.core;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;
import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.TServiceClientFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.pocketcampus.platform.shared.PCConstants;

import static org.pocketcampus.platform.android.core.PCAndroidConfig.PC_ANDR_CFG;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/**
 * Base controller class.
 * 
 * @author Florian <florian.laurent@epfl.ch>
 * @author Amer <amer.chamseddine@epfl.ch>
 */
public abstract class PluginController extends Service {

	/**
	 * Gets this plugin's client. May return an exception if connection to the
	 * server is impossible, but won't return null. FIXME for now we'll try to
	 * connect multiple times in a row if the model calls getCLient every
	 * time...
	 * 
	 * @return
	 * @throws TException
	 */
	protected TServiceClient getClient(
			TServiceClientFactory<? extends TServiceClient> clientFactory,
			String pluginName) {
		HttpClient httpInitialClient = getThreadSafeClient();
		return getClient(httpInitialClient, clientFactory, pluginName);
	}
	
	protected TServiceClient getClient(HttpClient httpInitialClient,
			TServiceClientFactory<? extends TServiceClient> clientFactory,
			String pluginName) {
		TServiceClient client = null;
		String url = getBackendUrl(pluginName, false);
		try {
			THttpClient httpClient = new THttpClient(url, httpInitialClient);
			httpClient.setConnectTimeout(5000);
			httpClient.setReadTimeout(60000);
			
			attachExtraHeaders(httpClient);

			TProtocol protocol = new TBinaryProtocol(httpClient);
			client = clientFactory.getClient(protocol);

		} catch (TException e) {
			e.printStackTrace();
		}

		return client;
	}
	
	protected String getBackendUrl(String pluginName, boolean raw) {
		return PC_ANDR_CFG.getString("SERVER_PROTOCOL") + "://" + PC_ANDR_CFG.getString("SERVER_ADDRESS") + ":"
				+ PC_ANDR_CFG.getInteger("SERVER_PORT") + "/" + PC_ANDR_CFG.getString("SERVER_URI") + "/" 
				+ (raw ? "raw-" : "") + pluginName;
	}
	
	protected HttpGet getHttpGet(String pluginName) { // raw
		HttpGet get = new HttpGet(getBackendUrl(pluginName, true));
		attachExtraHeaders(get);
		return get;
	}
	
	protected HttpPost getHttpPost(String pluginName) { // raw
		HttpPost post = new HttpPost(getBackendUrl(pluginName, true));
		attachExtraHeaders(post);
		return post;
	}
	
	private void attachExtraHeaders(HttpRequestBase reqObj) {
		String pushNotifToken = ((GlobalContext) getApplicationContext()).getPushNotifToken();
		if(pushNotifToken != null) {
			reqObj.setHeader(PCConstants.HTTP_HEADER_PUSHNOTIF_OS, "ANDROID");
			reqObj.setHeader(PCConstants.HTTP_HEADER_PUSHNOTIF_TOKEN, pushNotifToken);
		}
		String pcSessionId = ((GlobalContext) getApplicationContext()).getPcSessionId();
		if(pcSessionId != null) {
			reqObj.setHeader(PCConstants.HTTP_HEADER_AUTH_PCSESSID, pcSessionId);
		}
	}

	private void attachExtraHeaders(THttpClient client) {
		String pushNotifToken = ((GlobalContext) getApplicationContext()).getPushNotifToken();
		if(pushNotifToken != null) {
			client.setCustomHeader(PCConstants.HTTP_HEADER_PUSHNOTIF_OS, "ANDROID");
			client.setCustomHeader(PCConstants.HTTP_HEADER_PUSHNOTIF_TOKEN, pushNotifToken);
		}
		String pcSessionId = ((GlobalContext) getApplicationContext()).getPcSessionId();
		if(pcSessionId != null) {
			client.setCustomHeader(PCConstants.HTTP_HEADER_AUTH_PCSESSID, pcSessionId);
		}
	}

	/**
	 * From
	 * http://foo.jasonhudgins.com/2009/08/http-connection-reuse-in-android.html
	 * 
	 * @return
	 */
	public static DefaultHttpClient getThreadSafeClient() {
		DefaultHttpClient client = new DefaultHttpClient();
		ClientConnectionManager mgr = client.getConnectionManager();
		HttpParams params = client.getParams();
		// PROXY
		// one way to do it
		//params.setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost("localhost", 8080));
		// another way to do it
		//HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection(proxy);
		client = new DefaultHttpClient(new ThreadSafeClientConnManager(params,
				mgr.getSchemeRegistry()), params);

		return client;
	}

	/**
	 * Returns the Binder used the connect the Model (the <code>Activity</code>
	 * s) to the Service (which runs the Controller and contains the Model).
	 */
	public class ControllerBinder extends Binder {
		public PluginController getController() {
			return PluginController.this;
		}
	}

	/**
	 * Returns our custom <code>IBinder</code> when connecting.
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return new ControllerBinder();
	}

	/**
	 * Returns the application Model, used by the Views.
	 * 
	 * @return
	 */
	public abstract PluginModel getModel();

}
