package org.pocketcampus.plugin.authentication;

import org.pocketcampus.R;
import org.pocketcampus.core.communication.DataRequest;
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
	
	private static AuthToken authToken_ = null;
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
				
				authenticate(username, password);
			}
		});
	}

	private void authenticate(String username, String password) {
		if(username == null || password == null) {
			authToken_ = null;
		} else {
			RequestParameters parameters = new RequestParameters();
			parameters.addParameter("username", username.toString());
			parameters.addParameter("password", password.toString());

			getRequestHandler().execute(new AuthRequest(), "authenticate", parameters);
		}

		//if server returns a valid session id, store it in memory
		if(authToken_ != null) {
			if(authToken_.getSessionId().toString().equals(
			"11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111")) {
				Toast.makeText(this, "YES", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(this, "NO", Toast.LENGTH_LONG).show();
			}
		}
		//			PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putString("username", username).putString("sessionId", sessionId_).commit();
	}

	class AuthRequest extends DataRequest {
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
