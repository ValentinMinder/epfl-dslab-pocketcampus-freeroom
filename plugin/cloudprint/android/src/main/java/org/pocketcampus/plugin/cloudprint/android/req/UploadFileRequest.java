package org.pocketcampus.plugin.cloudprint.android.req;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.cloudprint.android.CloudPrintController;
import org.pocketcampus.plugin.cloudprint.android.iface.ICloudPrintView;

/**
 * UploadFileRequest
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class UploadFileRequest extends Request<CloudPrintController, DefaultHttpClient, File, Integer> {
	
	private static final String FILE_ID_KEY = "file_id";
	
	private ICloudPrintView caller;
	private HttpPost post;
	
	private long printJobId;
	
	public UploadFileRequest(ICloudPrintView caller, HttpPost post) {
		this.caller = caller;
		this.post = post;
	}
	
	@Override
	protected Integer runInBackground(DefaultHttpClient client, File param) throws Exception {
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();        
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		FileBody fileBody = new FileBody(param);
		builder.addPart("file", fileBody); 
        post.setEntity(builder.build());
		HttpResponse resp = client.execute(post);
		InputStream in = resp.getEntity().getContent();
		if(resp.getStatusLine().getStatusCode() != 200)
			return resp.getStatusLine().getStatusCode();
		JSONObject reply = new JSONObject(IOUtils.toString(in, "UTF-8"));
		if(!reply.has(FILE_ID_KEY))
			return 500;
		printJobId = reply.getLong(FILE_ID_KEY);
		return 200;
	}

	@Override
	protected void onResult(CloudPrintController controller, Integer result) {
		if(result == 200) {
			caller.uploadComplete(printJobId);
		} else if(result == 407) {
			caller.notLoggedIn();
		} else {
			caller.printServerError();
		}
	}
	
	@Override
	protected void onError(CloudPrintController controller, Exception e) {
		caller.networkErrorHappened();
		e.printStackTrace();
	}
	
}
