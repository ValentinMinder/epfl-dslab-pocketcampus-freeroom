package org.pocketcampus.plugin.moodle.android.req;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.moodle.android.MoodleController;
import org.pocketcampus.plugin.moodle.android.iface.IMoodleView;
import org.pocketcampus.plugin.moodle.shared.Constants;

/**
 * FetchMoodleResourceRequest
 * 
 * This class sends an HttpRequest to the Moodle server directly
 * in order to fetch a specific course resource/file.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class DownloadMoodleFileRequest extends Request<MoodleController, DefaultHttpClient, String, Integer> {
	
	private File localFile;
	private IMoodleView caller;
	private HttpPost post;
	
	public DownloadMoodleFileRequest(IMoodleView caller, HttpPost post) {
		this.caller = caller;
		this.post = post;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		caller.showLoading();
	}
	
	@Override
	protected Integer runInBackground(DefaultHttpClient client, String param) throws Exception {
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair(Constants.MOODLE_RAW_ACTION_KEY, Constants.MOODLE_RAW_ACTION_DOWNLOAD_FILE));
		params.add(new BasicNameValuePair(Constants.MOODLE_RAW_FILE_PATH, param));
		post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		HttpResponse resp = client.execute(post);
		InputStream in = resp.getEntity().getContent();
		if(resp.getStatusLine().getStatusCode() != 200)
			return resp.getStatusLine().getStatusCode();
		localFile = new File(MoodleController.getLocalPath(param, true));
		FileOutputStream fos = new FileOutputStream(localFile);
		IOUtils.copy(in, fos);
		return 200;
	}

	@Override
	protected void onResult(MoodleController controller, Integer result) {
		caller.hideLoading();
		if(result == 200) {
			caller.downloadComplete(localFile);
			
		} else if(result == 407) {
			caller.notLoggedIn();
			
		} else {
			caller.moodleServersDown();
			
		}
	}
	
	@Override
	protected void onError(MoodleController controller, Exception e) {
		caller.hideLoading();
		caller.networkErrorHappened();
		if(localFile != null && localFile.exists())
			localFile.delete();
		e.printStackTrace();
	}
	
}
