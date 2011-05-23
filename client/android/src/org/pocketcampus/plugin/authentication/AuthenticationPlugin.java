package org.pocketcampus.plugin.authentication;

import org.pocketcampus.R;
import org.pocketcampus.core.communication.DataRequest;
import org.pocketcampus.core.communication.RequestHandler;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.plugin.social.SocialPositionUpdater;
import org.pocketcampus.shared.plugin.authentication.AuthToken;
import org.pocketcampus.shared.plugin.social.User;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class AuthenticationPlugin extends PluginBase {
	private AuthenticationPreference preferences_;
	private static RequestHandler requestHandler_ = null;

	private static User user_ = null;
	
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
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.social_login);
		//		Tracker.getInstance().trackPageView("auth");

		//		String username = getIntent().getExtras().getString("username");
		//		String password = getIntent().getExtras().getString("password");

		WindowManager.LayoutParams lp = getWindow().getAttributes();  
		//lp.dimAmount = .7f;  
		getWindow().setAttributes(lp); 
		
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
			user_ = null;
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
			
			//stops position updater
			SocialPositionUpdater.stopPositionUpdater();
			
			//empty memory
			PreferenceManager.getDefaultSharedPreferences(context).edit()
				.putString("username", null)
				.putString("sessionId", null)
				.putString("first", null)
				.putString("last", null)
				.putString("sciper", null)
				.commit();
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
					user_ = gson.fromJson(result, new TypeToken<User>(){}.getType());
				} catch (JsonSyntaxException e) {
					user_ = null;
					e.printStackTrace();
				}
			} else {
				user_ = null;
			}

			if(user_ != null) {
				//login successful - update session data
				PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit()
					.putString("username", username)
					.putString("sessionId", user_.getSessionId())
					.putString("first", user_.getFirstName())
					.putString("last", user_.getLastName())
					.putString("sciper", user_.getSciper())
					.commit();
				
				//starts position updater
				SocialPositionUpdater.startPositionUpdater(thisActivity_);
				
				Toast.makeText(thisActivity_, thisActivity_.getString(R.string.authentication_hitosomeone) + " " + user_.getFirstName(), Toast.LENGTH_LONG).show();
				
				//close login activity
				thisActivity_.finish();
			} else {
				//login failed
				alert();
				((EditText) thisActivity_.findViewById(R.id.socialLoginPasswordField)).setText("");
			}
		}

		private void alert() {
			//display error message
			AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity_);
			AlertDialog alert = builder.setMessage(thisActivity_.getString(R.string.authentication_wrong_password))
			.setCancelable(true).create();
			alert.setCanceledOnTouchOutside(true);
			alert.show();
		}
	}
	
	//retrieve session data
	public static AuthToken getAuthToken(Context context) {
		if(context == null) throw new IllegalArgumentException();
		
		AuthToken token = null;
		
		String username = PreferenceManager.getDefaultSharedPreferences(context).getString("username", null);
		String sessionId = PreferenceManager.getDefaultSharedPreferences(context).getString("sessionId", null);
		
		if(username != null && sessionId != null) {
			token = new AuthToken(username, sessionId);
		}
		
		return token;
	}
	
	//returns user that is currently logged on the phone
	public static User getUser(Context context) {
		if(context == null) throw new IllegalArgumentException();
		
		User user = null;
		
		String first = PreferenceManager.getDefaultSharedPreferences(context).getString("first", null);
		String last = PreferenceManager.getDefaultSharedPreferences(context).getString("last", null);
		String sciper = PreferenceManager.getDefaultSharedPreferences(context).getString("sciper", null);
		String sessionId = PreferenceManager.getDefaultSharedPreferences(context).getString("sessionId", null);
		
		if(first != null && last != null && sciper != null && sessionId != null) {
			user = new User(first, last, sciper);
			user.setSessionId(sessionId);
		}
		
		return user;
	}
	
	public static RequestHandler getAuthenticationRequestHandler() {
		return requestHandler_;
	}
}
