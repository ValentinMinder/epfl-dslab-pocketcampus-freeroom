package org.pocketcampus.plugin.social;

import org.pocketcampus.core.communication.DataRequest;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.plugin.authentication.AuthenticationPlugin;
import org.pocketcampus.shared.plugin.authentication.AuthToken;
import org.pocketcampus.shared.plugin.map.Position;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class SocialPositionUpdater {
	
	private static PositionUpdater updater_;
	
	private final static int UPDATE_PERIOD = 5 * 60 * 1000;

	public static void startPositionUpdater(Context context) {
		updater_ = new PositionUpdater(AuthenticationPlugin.getAuthToken(context));
		new Thread(updater_).start();
	}
	
	public static void stopPositionUpdater() {
		updater_.shutDown();
	}
	
	private static class PositionUpdater implements Runnable {
		private final AuthToken token_;
		private boolean on_;
		
		public PositionUpdater(AuthToken token) {
			token_ = token;
			on_ = true;
		}
		
		@Override
		public void run() {
			do {
				if(token_ != null) {
					Position p = new Position(1.00000123, 2.00000123, 3.00000123);
					
					RequestParameters rp = new RequestParameters();
					rp.addParameter("username", token_.getUsername());
					rp.addParameter("sessionId", token_.getSessionId());
					rp.addParameter("longitude", p.getLongitude()+"");
					rp.addParameter("latitude", p.getLatitude()+"");
					rp.addParameter("altitude", p.getAltitude()+"");
					
					SocialPlugin.getSocialRequestHandler().execute(new UpdatePositionRequest(), "updatePosition", rp);
					
					try {
						Thread.sleep(UPDATE_PERIOD);
					} catch(InterruptedException e) {
						
					}
				} else {
					on_ = false;
				}
			} while(on_);
		}
		
		public void shutDown() {
			on_ = false;
		}
		
		private class UpdatePositionRequest extends DataRequest {
			@Override
			protected void doInUiThread(String result) {
				boolean status = false;
				if(result != null) {
					Gson gson = new Gson();
					try{
						status = gson.fromJson(result, new TypeToken<Boolean>(){}.getType());
					} catch (JsonSyntaxException e) {
						status = false;
						e.printStackTrace();
					}
				}
				
				if(!status) {
					//If request fails, we close connection.
//					AuthenticationPlugin.logout(context_);
				}
			}
		}
	}
	
}
