package org.pocketcampus.plugin.social;

import org.pocketcampus.core.ui.ActionBar;
import org.pocketcampus.plugin.mainscreen.MainscreenPlugin;
//import org.pocketcampus.gui.mainscreen.InternetUtilities;
//import org.pocketcampus.gui.mainscreen.InternetUtilities.ConnexionStatus;
//import org.pocketcampus.gui.preferences.Preferences;
//import org.pocketcampus.map.Position;
//import org.pocketcampus.positioning.Locator;
//import org.pocketcampus.serverapi.ServerAPI;
//import org.pocketcampus.shared.accounts.AuthToken;
//import org.pocketcampus.shared.accounts.Encryption;
//import org.pocketcampus.shared.accounts.Password;
//import org.pocketcampus.shared.accounts.Username;
//import org.pocketcampus.shared.exceptions.ServerException;
//import org.pocketcampus.shared.map.MapPersonData;
//import org.pocketcampus.shared.map.PositionData;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import org.pocketcampus.R;
public class SocialLogin extends Activity {
//	private ServerAPI serverAPI_;
	private SocialLogin thisActivity_;
	private SharedPreferences sharedPreferences_;
	private int MAX_USERNAME_LENGTH = 50;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.social_login);
		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.setTitle("PocketCampus EPFL");
		actionBar.addAction(new ActionBar.IntentAction(this, MainscreenPlugin
				.createIntent(this), R.drawable.mini_home));

//		serverAPI_ = new ServerAPI();
		thisActivity_ = this;
		sharedPreferences_ = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

		Button buttonLogin = (Button) findViewById(R.id.socialLoginButton);
		buttonLogin.setOnClickListener(new OnClickListener() {
			EditText username = (EditText) findViewById(R.id.socialLoginUsernameField);
			EditText password = (EditText) findViewById(R.id.socialLoginPasswordField);

			@Override
			public void onClick(View v) {
				
				Intent FriendsIntent = new Intent(thisActivity_, FriendsList.class);
	        	startActivity(FriendsIntent);
	        	thisActivity_.finish();
				
//				if(!username.getText().toString().equals("") && !password.getText().toString().equals("") && username.getText().toString().length() < MAX_USERNAME_LENGTH) {
//					if(!(new InternetUtilities()).checkInternetStatus(thisActivity_, false).equals(ConnexionStatus.INTERNET_CONNEXION_UNAVAILABLE)) {
//						try {
//							AuthToken authToken = serverAPI_.login(new Username(username.getText().toString()), new Password(password.getText().toString(), Encryption.NONE));
//
//							if(authToken == null) {
//								new NotConnectedAlert(thisActivity_, thisActivity_.getString(R.string.social_unable_to_login));
//							} else {
//								sharedPreferences_.edit()
//								.putString("preferences_keyUsername", authToken.getUsername().toString())
//								.putString("preferences_keySessionId", authToken.getSessionId().toString())
//								.commit();
//
//								System.out.println("Starting service...");
//								startService(new Intent(thisActivity_, PositionUpdaterService.class));
//								
//								
//								thisActivity_.finish();
//							}
//						} catch(ServerException e) {
//							e.printStackTrace();
//							new NotConnectedAlert(thisActivity_, thisActivity_.getString(R.string.social_unable_to_login));
//						}
//					} else {
//						new NotConnectedAlert(thisActivity_, thisActivity_.getString(R.string.social_no_internet));
//					}
//				}
			}
		});
	}
	
	@Override
		public void onBackPressed() {
			// TODO Auto-generated method stub
			this.startActivity(new Intent(this, MainscreenPlugin.class));
			this.finish();
		}
}
