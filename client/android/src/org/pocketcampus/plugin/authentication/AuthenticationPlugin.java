package org.pocketcampus.plugin.authentication;

import org.pocketcampus.R;
import org.pocketcampus.core.communication.DataRequest;
import org.pocketcampus.core.communication.RequestHandler;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.plugin.logging.Tracker;
import org.pocketcampus.shared.plugin.authentication.AuthToken;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class AuthenticationPlugin extends PluginBase {
	private AuthenticationPreference preferences_;
	private static RequestHandler requestHandler_ = null;
	
	private static AuthToken authToken_ = null;
	private static boolean authed_ = false;
	private static String username = null;
	private static String password = null;
	
	@Override
	public PluginInfo getPluginInfo() {
		return new AuthenticationInfo();
	}

	@Override
	public PluginPreference getPluginPreference() {
		return preferences_;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.social_login);

		Tracker.getInstance().trackPageView("auth");
		
//		String username = getIntent().getExtras().getString("username");
//		String password = getIntent().getExtras().getString("password");

		Button button = (Button)findViewById(R.id.socialLoginButton);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				username = ((EditText) findViewById(R.id.socialLoginUsernameField)).getText().toString();
				password = ((EditText) findViewById(R.id.socialLoginPasswordField)).getText().toString();
				
				login(username, password);
			}
		});
	}

	private void login(String username, String password) {
		if(username == null || password == null) {
			authToken_ = null;
		} else {
			RequestParameters parameters = new RequestParameters();
			parameters.addParameter("username", username.toString());
			parameters.addParameter("password", password.toString());

			requestHandler_ = getRequestHandler();
			requestHandler_.execute(new LoginRequest(), "login", parameters);
		}

		//if server returns a valid session id, store it in memory
		if(authToken_ != null) {
			if(authToken_.getSessionId().toString().equals(//TEST
			"11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111")) {
				Toast.makeText(this, "YES", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(this, "NO", Toast.LENGTH_LONG).show();
			}
		}
		//			PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putString("username", username).putString("sessionId", sessionId_).commit();
	}
	
	public static boolean authenticate(String username, String sessionId) {
		class AuthRequest extends DataRequest {
			@Override
			protected void doInUiThread(String result) {
				if(result != null) {
					Gson gson = new Gson();

					try{
						authed_ = gson.fromJson(result, new TypeToken<Boolean>(){}.getType());
					} catch (JsonSyntaxException e) {
						authed_ = false;
						e.printStackTrace();
					}
				} else {
					authed_ = false;
				}
			}
		}
		
		if(username == null || sessionId == null) {
			authed_ = false;
		} else {
			RequestParameters parameters = new RequestParameters();
			parameters.addParameter("username", username.toString());
			parameters.addParameter("sessionId", sessionId.toString());

			requestHandler_.execute(new AuthRequest(), "login", parameters);
		}
		
		return authed_;
	}

	

	class LoginRequest extends DataRequest {
		//extract session id from server reply
		//DEAD_SESSION_ID if authentication failed
		@Override
		protected void doInUiThread(String result) {
			if(result != null) {
				Gson gson = new Gson();

				try{
					authToken_ = gson.fromJson(result, new TypeToken<AuthToken>(){}.getType());
				} catch (JsonSyntaxException e) {
					authToken_ = null;
					e.printStackTrace();
				}
			} else {
				authToken_ = null;
			}
		}
	}
}
