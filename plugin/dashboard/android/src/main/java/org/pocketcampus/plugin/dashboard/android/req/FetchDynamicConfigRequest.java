package org.pocketcampus.plugin.dashboard.android.req;

import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.pocketcampus.platform.android.core.GlobalContext;
import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.dashboard.android.DashboardController;
import org.pocketcampus.plugin.dashboard.android.DashboardView;

import android.content.Context;

/**
 * FetchDynamicConfigRequest
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class FetchDynamicConfigRequest extends Request<DashboardController, DefaultHttpClient, Context, Boolean> {
	
	DashboardView view;
	
	public FetchDynamicConfigRequest(DashboardView view) {
		this.view = view;
	}
	
	@Override
	protected Boolean runInBackground(DefaultHttpClient client, Context param) throws Exception {
		String appVersion = DashboardController.getAppVersion(param);
		HttpGet get = new HttpGet("https://pocketcampus.epfl.ch/backend/get_config.php?" +
				"app_version=" + appVersion + "&platform=android");
		HttpResponse resp = client.execute(get);
		InputStream in = resp.getEntity().getContent();
		FileOutputStream fos = param.openFileOutput("pocketcampus.config", Context.MODE_PRIVATE);
		byte[] buffer = new byte[4096];
		int length; 
		while((length = in.read(buffer)) > 0) {
			fos.write(buffer, 0, length);
		}
		fos.close();
		return resp.getStatusLine().getStatusCode() == 200;
	}

	@Override
	protected void onResult(DashboardController controller, Boolean result) {
		if(result) {
			((GlobalContext) controller.getApplicationContext()).refresh();
			view.displayPlugins();
		} else {
			System.out.println("Could not fetch config file: Bad server response");
		}
	}
	
	@Override
	protected void onError(DashboardController controller, Exception e) {
		e.printStackTrace();
	}
	
}
