package org.pocketcampus.plugin.authentication;

import org.pocketcampus.R;
import org.pocketcampus.core.communication.DataRequest;
import org.pocketcampus.core.communication.RequestHandler;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

	private static String sessionId_ = null;
	private static String username = null;
	private static String password = null;
	private final Activity thisActivity_ = this;

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
		//		Tracker.getInstance().trackPageView("auth");

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
			sessionId_ = null;
		} else {
			RequestParameters parameters = new RequestParameters();
			parameters.addParameter("username", username);
			parameters.addParameter("password", password);

			requestHandler_ = getRequestHandler();
			requestHandler_.execute(new LoginRequest(), "login", parameters);
		
		}
	}

	public static void logout(final Context context, final Class<?> toStartAfterLogout) {
		class LogoutRequest extends DataRequest {
			@Override
			protected void doInUiThread(String result) {
				if(toStartAfterLogout != null)
					context.startActivity(new Intent(context, toStartAfterLogout));
			}
		}

		String username = PreferenceManager.getDefaultSharedPreferences(context).getString("username", null);
		String sessionId = PreferenceManager.getDefaultSharedPreferences(context).getString("sessionId", null);

		try {
			if(username != null && sessionId != null && requestHandler_ != null) {
				RequestParameters parameters = new RequestParameters();
				parameters.addParameter("username", username.toString());
				parameters.addParameter("sessionId", sessionId.toString());

				requestHandler_.execute(new LogoutRequest(), "logout", parameters);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			requestHandler_ = null;
			PreferenceManager.getDefaultSharedPreferences(context).edit().putString("username", null).putString("sessionId", null).commit();
		}
	}
	
	public static void logout(final Context context) {
		logout(context, null);
	}

	public static void authenticate(final Context context, final Class<?> toStartIfSuccess) {
		class AuthRequest extends DataRequest {
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
				} else {
					status = false;
				}
				
				
				/*
				 * If successful login, we launch toLaunchIfSuccess activity. Otherwise we open a login window.
				 */
				if(status) {
					context.startActivity(new Intent(context, toStartIfSuccess));
				} else {
					context.startActivity(new Intent(context, AuthenticationPlugin.class));
				}
			}
		}
		
		String username = PreferenceManager.getDefaultSharedPreferences(context).getString("username", null);
		String sessionId = PreferenceManager.getDefaultSharedPreferences(context).getString("sessionId", null);

		if(username == null || sessionId == null || requestHandler_ == null) {
			context.startActivity(new Intent(context, AuthenticationPlugin.class));
		} else {
			RequestParameters parameters = new RequestParameters();
			parameters.addParameter("username", username.toString());
			parameters.addParameter("sessionId", sessionId.toString());

			requestHandler_.execute(new AuthRequest(), "authenticate", parameters);
		}
	}

	class LoginRequest extends DataRequest {
		@Override
		protected void doInUiThread(String result) {
			if(result != null) {
				Gson gson = new Gson();
				
				try{
					sessionId_ = gson.fromJson(result, new TypeToken<String>(){}.getType());
				} catch (JsonSyntaxException e) {
					sessionId_ = null;
					e.printStackTrace();
				}
			} else {
				sessionId_ = null;
			}

			if(sessionId_ != null) {
				//update session data
				PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putString("username", username).putString("sessionId", sessionId_).commit();

				Toast.makeText(thisActivity_, thisActivity_.getString(R.string.authentication_hitosomeone) + " " + username, Toast.LENGTH_LONG).show();
				
				//close login activity
				thisActivity_.finish();
			} else {
				alert();

				((EditText) thisActivity_.findViewById(R.id.socialLoginPasswordField)).setText("");
			}
		}

		private void alert() {
			AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity_);
			AlertDialog alert = builder.setMessage(thisActivity_.getString(R.string.authentication_wrong_password))
			.setCancelable(true).create();
			alert.setCanceledOnTouchOutside(true);
			alert.show();
		}
	}
}
