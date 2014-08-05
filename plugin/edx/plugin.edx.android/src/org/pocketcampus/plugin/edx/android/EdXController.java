package org.pocketcampus.plugin.edx.android;

import java.util.List;

import org.apache.http.impl.client.DefaultHttpClient;
import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginModel;
import org.pocketcampus.plugin.edx.android.iface.IEdXController;
import org.pocketcampus.plugin.edx.android.iface.IEdXView;
import org.pocketcampus.plugin.edx.android.req.CourseSectionsRequest;
import org.pocketcampus.plugin.edx.android.req.DoLoginRequest;
import org.pocketcampus.plugin.edx.android.req.GetActiveRoomsRequest;
import org.pocketcampus.plugin.edx.android.req.ModuleDetailsRequest;
import org.pocketcampus.plugin.edx.android.req.ReceiveBroadcastRequest;
import org.pocketcampus.plugin.edx.android.req.SendBroadcastRequest;
import org.pocketcampus.plugin.edx.android.req.UserCoursesRequest;
import org.pocketcampus.plugin.edx.shared.EdXService.Client;
import org.pocketcampus.plugin.edx.shared.EdXService.Iface;
import org.pocketcampus.plugin.edx.shared.EdxItemType;
import org.pocketcampus.plugin.edx.shared.EdxReq;
import org.pocketcampus.plugin.edx.shared.MsgPsgMessage;
import org.pocketcampus.plugin.edx.shared.MsgPsgMessageType;
import org.pocketcampus.plugin.edx.shared.MsgPsgReceiveBroadcastReq;
import org.pocketcampus.plugin.edx.shared.MsgPsgSendBroadcastReq;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.EditText;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;


/**
 * EdXController - Main logic for the EdX Plugin.
 * 
 * This class issues requests to the EdX PocketCampus
 * server to get the EdX data of the logged in user.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class EdXController extends PluginController implements IEdXController{

	/**
	 *  This name must match given in the Server.java file in plugin.launcher.server.
	 *  It's used to route the request to the right server implementation.
	 */
	private String mPluginName = "edx";
	

	/**
	 * Stores reference to the Model associated with this plugin.
	 */
	private EdXModel mModel;
	
	/**
	 * HTTP Clients used to communicate with the PocketCampus server.
	 * Use thrift to transport the data.
	 */
	private Iface mClient;
	private Iface bcsClient;
	private Iface bcsaClient;
	private Iface bcrClient;
	
	/**
	 * HTTP Client used to communicate directly with servers.
	 * Used to communicate with Sync Server.
	 */
	private DefaultHttpClient threadSafeClient = null;
	
	/**
	 * Android ID
	 */
	//public static String ANDROID_ID;
	

	@Override
	public void onCreate() {
		mModel = new EdXModel(getApplicationContext());
		mClient = (Iface) getClient(new Client.Factory(), mPluginName);
		bcsClient = (Iface) getClient(new Client.Factory(), mPluginName);
		bcsaClient = (Iface) getClient(new Client.Factory(), mPluginName);
		bcrClient = (Iface) getClient(new Client.Factory(), mPluginName);
		threadSafeClient = getThreadSafeClient();
		
		//ANDROID_ID = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);

		// initialize ImageLoader
		ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
	}
	
	
	

	public void openLoginDialog() {
		Intent intenteye = new Intent(this, EdXLoginView.class);
		intenteye.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intenteye);
	}
	
	
	

	/**
	 * Initiates a request to the server 
	 */
	public void performLogin(IEdXView caller) {
		new DoLoginRequest(caller).start(this, mClient, mModel.getCredentials());
	}
	public void refreshUserCourses(IEdXView caller, boolean useCache) {
		new UserCoursesRequest(caller).setBypassCache(!useCache).start(this, mClient, new EdxReq(mModel.getSession()));
	}
	public void refreshCourseSections(IEdXView caller, String courseId, boolean useCache) {
		EdxReq req = new EdxReq(mModel.getSession());
		req.setCourseId(courseId);
		new CourseSectionsRequest(caller).setBypassCache(!useCache).start(this, mClient, req);
	}
	public void refreshModuleDetails(IEdXView caller, String courseId, String moduleId, boolean useCache) {
		EdxReq req = new EdxReq(mModel.getSession());
		req.setCourseId(courseId);
		req.setModuleUrl(moduleId);
		new ModuleDetailsRequest(caller).setBypassCache(!useCache).start(this, mClient, req);
	}
	public void refreshActiveRooms(IEdXView caller, String videoId) {
		new GetActiveRoomsRequest(caller).start(this, threadSafeClient, videoId);
	}


	
	
	
	
	
	@Override
	public PluginModel getModel() {
		return mModel;
	}
	

	
	/***
	 * HELPER CLASSES
	 */
	
	public static class EdxGenericItem {
		EdxItemType type;
		int index;
		public EdxGenericItem(EdxItemType type, int index) {
			this.type = type; 
			this.index = index;
		}
	}
	
	public static class Stopper {
		boolean running = true;
		public boolean isStopped() {
			return !running;
		}
		public void stop() {
			running = false;
		}
	}
	
	public static void prompt (final Context context, final String title, final String message, final String prefilled, final Callback<String> callback) {
		AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setTitle(title);
        alert.setMessage(message);

        // Set an EditText view to get user input
        final EditText input = new EditText(context);
        input.setText(prefilled);
        alert.setView(input);

        alert.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                            int whichButton) {
                        callback.callback(input.getText().toString());
                    }
                });

        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                            int whichButton) {
                        // Canceled.
                    }
                });

        alert.show();
	}
	
	


	/*
	public void pushMessage1(final String roomRef, final String message) {
		new AsyncTask<String, Integer, String>() {
			protected String doInBackground(String... params) {
				try {
					HttpPost post = new HttpPost("http://pocketcampus.epfl.ch/backend/message_passing.php");
					List<NameValuePair> posted = new LinkedList<NameValuePair>();
					posted.add(new BasicNameValuePair("action", "sendbc"));
					posted.add(new BasicNameValuePair("myref", ANDROID_ID));
					posted.add(new BasicNameValuePair("roomref", roomRef));
					posted.add(new BasicNameValuePair("message", message));
					post.setEntity(new UrlEncodedFormEntity(posted));
					HttpResponse resp = threadSafeClient.execute(post);
					return IOUtils.toString(resp.getEntity().getContent(), "UTF-8");
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			}
		}.execute();
	}
	*/
	String skipMessage = null;
	public void setSkipMessage(String header, String message) {
		skipMessage = header + message;
	}
	public void pushMessage2(IEdXView caller, String roomRef, String header, String message) {
		String skipMessageRef = skipMessage;
		skipMessage = null;
		if(skipMessageRef != null && skipMessageRef.equals(header + message))
			return;
		MsgPsgSendBroadcastReq req = new MsgPsgSendBroadcastReq(mModel.getSession(), roomRef, header, MsgPsgMessageType.MESSAGE);
		req.setTextBody(message);
		new SendBroadcastRequest(caller).start(this, bcsClient, req);
	}
	public void pushMessage2(IEdXView caller, String roomRef, byte [] body) {
		MsgPsgSendBroadcastReq req = new MsgPsgSendBroadcastReq(mModel.getSession(), roomRef, "", MsgPsgMessageType.AUDIO);
		req.setBinaryBody(body);
		new SendBroadcastRequest(caller).start(this, bcsaClient, req);
	}

	/*
	public MessagePoller startPolling1(String roomRef, Callback<String> cb) {
		MessagePoller mp = new MessagePoller(roomRef, cb);
		mp.poll();
		return mp;
	}
	*/
	public Stopper startPolling2(IEdXView caller, String roomRef, Callback<List<MsgPsgMessage>> cb) {
		Stopper stopper = new Stopper();
		MsgPsgReceiveBroadcastReq req = new MsgPsgReceiveBroadcastReq(mModel.getSession(), roomRef, 0, 0);
		new ReceiveBroadcastRequest(caller, stopper, cb, this, bcrClient, req).go();
		return stopper;
	}
	/*
	public class MessagePoller {
		String roomRef;
		Callback<String> cb;
		boolean destroyed = false;
		public MessagePoller(String roomRef, Callback<String> cb) {
			this.roomRef = roomRef;
			this.cb = cb;
		}
		public void poll() {
			new AsyncTask<String, Integer, String>() {
				protected String doInBackground(String... params) {
					try {
						HttpPost post = new HttpPost("http://pocketcampus.epfl.ch/backend/message_passing.php");
						List<NameValuePair> posted = new LinkedList<NameValuePair>();
						posted.add(new BasicNameValuePair("action", "recvbc"));
						posted.add(new BasicNameValuePair("myref", ANDROID_ID));
						posted.add(new BasicNameValuePair("roomref", roomRef));
						post.setEntity(new UrlEncodedFormEntity(posted));
						HttpResponse resp = threadSafeClient.execute(post);
						return IOUtils.toString(resp.getEntity().getContent(), "UTF-8");
					} catch (IOException e) {
						e.printStackTrace();
						return null;
					}
				}
				protected void onPostExecute(String result){
					if(destroyed) 
						return;
					if(result == null) {
						Toast.makeText(getApplicationContext(), "Network error", Toast.LENGTH_SHORT).show();
						poll();
						return;
					}
					try {
						JSONObject json = new JSONObject(result);
						JSONArray arr = json.getJSONArray("payload");
						if(arr != null) {
					        for(int i = 0; i < arr.length(); i++) {
					        	if(ANDROID_ID.equals(arr.getJSONObject(i).getString("from")))
					        		continue;
					        	cb.callback(arr.getJSONObject(i).getString("message"));
					        }
						}
					} catch (JSONException e) {
						Toast.makeText(getApplicationContext(), "Bad reply", Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}
					poll();
				}
			}.execute();
		}
		public void stop () {
			destroyed = true;
		}	
	}

	*/
	

	

}
