package org.pocketcampus.android.platform.sdk.core;

import org.apache.http.client.HttpClient;
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
import org.apache.thrift.transport.TSocket;

import static org.pocketcampus.android.platform.sdk.core.PCAndroidConfig.PC_ANDR_CFG;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/**
 * Base controller class.
 * 
 * @author Florian <florian.laurent@epfl.ch>
 */
public abstract class PluginController extends Service {
	/** Socket connection for this plugin. */
	protected TSocket mSocket;

	protected TServiceClient getClient(
			TServiceClientFactory<? extends TServiceClient> clientFactory,
			String pluginName) {
		TServiceClient client = null;
		String url = PC_ANDR_CFG.getString("SERVER_PROTO") + "://" + PC_ANDR_CFG.getString("SERVER_IP") + ":"
				+ PC_ANDR_CFG.getInteger("SERVER_PORT") + "/v3r1/" + pluginName;

		System.out.println(url);
		try {
			HttpClient httpInitialClient = getThreadSafeClient();

			THttpClient httpClient = new THttpClient(url, httpInitialClient);
			httpClient.setConnectTimeout(5000);
			httpClient.setReadTimeout(60000);

			TProtocol protocol = new TBinaryProtocol(httpClient);
			client = clientFactory.getClient(protocol);

		} catch (TException e) {
			e.printStackTrace();
		}

		return client;
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

	/**
	 * Gets this plugin's client. May return an exception if connection to the
	 * server is impossible, but won't return null. FIXME for now we'll try to
	 * connect multiple times in a row if the model calls getCLient every
	 * time...
	 * 
	 * @return
	 * @throws TException
	 */

	@Override
	public void onDestroy() {
		if (mSocket != null) {
			mSocket.close();
		}

		super.onDestroy();
	}

}
