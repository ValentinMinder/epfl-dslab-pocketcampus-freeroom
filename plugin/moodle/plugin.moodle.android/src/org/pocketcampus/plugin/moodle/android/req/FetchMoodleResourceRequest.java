package org.pocketcampus.plugin.moodle.android.req;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.moodle.android.MoodleController;
import org.pocketcampus.plugin.moodle.android.MoodleModel.ResourceCookieComplex;
import org.pocketcampus.plugin.moodle.android.MoodleModel;

/**
 * FetchMoodleResourceRequest
 * 
 * This class sends an HttpRequest to the Moodle server directly
 * in order to fetch a specific course resource/file.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class FetchMoodleResourceRequest extends Request<MoodleController, DefaultHttpClient, ResourceCookieComplex, Integer> {
	
	File localFile;
	
	@Override
	protected Integer runInBackground(DefaultHttpClient client, ResourceCookieComplex param) throws Exception {
		HttpGet get = new HttpGet(param.resource);
		get.addHeader("Cookie", param.cookie);
		HttpResponse resp = client.execute(get);
		InputStream in = resp.getEntity().getContent();
		if(resp.getStatusLine().getStatusCode() != 200)
			return resp.getStatusLine().getStatusCode();
		localFile = new File(MoodleController.getLocalPath(param.resource));
		FileOutputStream fos = new FileOutputStream(localFile);
		byte[] buffer = new byte[4096];
		int length; 
		while((length = in.read(buffer)) > 0) {
			fos.write(buffer, 0, length);
		}
		fos.close();
		return 200;
	}

	@Override
	protected void onResult(MoodleController controller, Integer result) {
		MoodleModel am = ((MoodleModel) controller.getModel());
		if(result == 200) {
			am.getListenersToNotify().downloadComplete(localFile);
		} else if(result / 100 == 3) {
			controller.notLoggedIn();
		} else {
			am.getListenersToNotify().moodleServersDown();
		}
	}
	
	@Override
	protected void onError(MoodleController controller, Exception e) {
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}
	
}
