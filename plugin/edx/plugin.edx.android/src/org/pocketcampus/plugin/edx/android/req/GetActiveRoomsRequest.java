package org.pocketcampus.plugin.edx.android.req;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.edx.android.EdXController;
import org.pocketcampus.plugin.edx.android.EdXModel;
import org.pocketcampus.plugin.edx.android.EdXModel.ActiveRoom;
import org.pocketcampus.plugin.edx.android.iface.IEdXView;

/**
 * GetActiveRoomsRequest
 * 
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class GetActiveRoomsRequest extends Request<EdXController, HttpClient, String, String> {
	
	private IEdXView caller;
	
	public GetActiveRoomsRequest(IEdXView caller) {
		this.caller = caller;
	}
	
	@Override
	protected String runInBackground(HttpClient client, String param) throws Exception {
		HttpPost post = new HttpPost("http://pocketcampus.epfl.ch/backend/message_passing.php");
		List<NameValuePair> l = new LinkedList<NameValuePair>();
		l.add(new BasicNameValuePair("action", "getactive"));
		l.add(new BasicNameValuePair("vidid", param));
		post.setEntity(new UrlEncodedFormEntity(l));
		HttpResponse resp = client.execute(post);
		return IOUtils.toString(resp.getEntity().getContent(), "UTF-8");
	}

	@Override
	protected void onResult(EdXController controller, String result) {
		
		try {
			JSONObject json = new JSONObject(result);
			JSONArray arr = json.getJSONArray("payload");
			if(arr != null) {
				List<ActiveRoom> activeRooms = new LinkedList<ActiveRoom>();
		        for(int i = 0; i < arr.length(); i++) {
		        	String n = arr.getJSONObject(i).getString("room_name");
		        	int o = arr.getJSONObject(i).getInt("occupancy");
		        	activeRooms.add(new ActiveRoom(n, o));
		        }
		        ((EdXModel) controller.getModel()).setActiveRooms(activeRooms);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			caller.serverFailure();
		}

		
	}
	
	@Override
	protected void onError(EdXController controller, Exception e) {
		e.printStackTrace();
		caller.networkErrorHappened();
	}
	
}
