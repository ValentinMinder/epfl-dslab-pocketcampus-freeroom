package org.pocketcampus.platform.android.io;

import android.content.Context;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.pocketcampus.platform.android.core.GlobalContext;
import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.utils.Callback;
import org.pocketcampus.platform.shared.PCConstants;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * FetchDynamicConfigRequest
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class FetchDynamicConfigRequest<ControllerType extends PluginController> extends Request<ControllerType, Void, String, Boolean> {

	GlobalContext cntxt;
	Callback<Boolean> callback;

	public FetchDynamicConfigRequest(Context cntxt, Callback<Boolean> callback) {
		this.cntxt = (GlobalContext) cntxt.getApplicationContext();
		this.callback = callback;
	}

	@Override
	protected Boolean runInBackground(Void v1, String sessId) throws IOException {
		String appVersion = cntxt.getAppVersion();
		HttpGet get = new HttpGet("https://pocketcampus.epfl.ch/backend/get_config.php?" +
				"app_version=" + appVersion + "&platform=android");
		if(sessId != null) {
			get.setHeader(PCConstants.HTTP_HEADER_AUTH_PCSESSID, sessId);
		}

		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 2000);      // aggressive
		HttpConnectionParams.setSoTimeout(httpParams, 5000);              // timeouts
		HttpClient httpClient = new DefaultHttpClient(httpParams);

		HttpResponse resp = httpClient.execute(get);
		InputStream in = resp.getEntity().getContent();
		if(resp.getStatusLine().getStatusCode() != 200) {
			return false;
		}
		String configFile = (sessId == null ? "pocketcampus_common.config" : "pocketcampus_authenticated.config");
		FileOutputStream fos = cntxt.openFileOutput(configFile, Context.MODE_PRIVATE);
		byte[] buffer = new byte[4096];
		int length; 
		while((length = in.read(buffer)) > 0) {
			fos.write(buffer, 0, length);
		}
		fos.close();
		return true;
	}

	@Override
	protected void onResult(ControllerType controller, Boolean result) {
		if(result) {
			cntxt.refresh();
			callback.callback(true);
		} else {
			System.out.println("Could not fetch config file: Bad server response");
			callback.callback(false);
		}
	}
	
	@Override
	protected void onError(ControllerType controller, Exception e) {
		e.printStackTrace();
		callback.callback(false);
	}
	
}
