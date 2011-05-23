package org.pocketcampus.plugin.social;

import org.pocketcampus.core.communication.DataRequest;
import org.pocketcampus.core.communication.RequestHandler;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.plugin.authentication.AuthenticationPlugin;
import org.pocketcampus.plugin.positioning.IUserLocationListener;
import org.pocketcampus.plugin.positioning.UserPosition;
import org.pocketcampus.shared.plugin.authentication.AuthToken;

import android.content.Context;
import android.location.Location;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class SocialPositionUpdater {

	private static PositionUpdater updater_;
	private static Context context_;
	private static Location location_;

	private final static int UPDATE_PERIOD = 1 * 30 * 1000;
	private final static int POSITION_TIMEOUT = 10 * 1000;
	private final static int POSITION_ACURACY = 20;

	public static void startPositionUpdater(Context context) {
		if(context == null) throw new IllegalArgumentException();

		context_ = context;
		updater_ = new PositionUpdater(AuthenticationPlugin.getAuthToken(context_));
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
					if(location_ != null) {RequestParameters rp = new RequestParameters();
						rp.addParameter("username", token_.getUsername());
						rp.addParameter("sessionId", token_.getSessionId());
						rp.addParameter("longitude", location_.getLongitude()+"");
						rp.addParameter("latitude", location_.getLatitude()+"");
						rp.addParameter("altitude", location_.getAltitude()+"");

						RequestHandler handler = AuthenticationPlugin.getAuthenticationRequestHandler();
						if(handler != null) {
							handler.execute(new UpdatePositionRequest(), "updatePosition", rp);
						} else {
							on_ = false;
						}
					}

					new UserPosition(context_, new IUserLocationListener() {
						@Override
						public void userLocationReceived(Location location) {
							location_ = location;
						}
					}, POSITION_TIMEOUT, POSITION_ACURACY);

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
//					If request fails, we close connection.
					AuthenticationPlugin.logout(context_);
				}
			}
		}
	}

}
