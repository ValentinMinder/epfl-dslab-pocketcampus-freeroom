package org.pocketcampus.plugin.social;

import java.util.ArrayList;

import org.pocketcampus.R;
import org.pocketcampus.shared.plugin.authentication.AuthToken;
import org.pocketcampus.shared.plugin.authentication.SessionId;
import org.pocketcampus.shared.plugin.authentication.Username;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

public class FriendsPermissions extends Activity {
//	private ServerAPI serverAPI_;
	private Activity thisActivity_;
	private ArrayList<Username> selectedUsernames_;
	private SharedPreferences sharedPreferences_;
	private AuthToken authToken_;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.social_permissions);
		
		Intent intent = getIntent();
		if (intent.hasExtra("usernames")) {
			selectedUsernames_ = (ArrayList<Username>) intent.getSerializableExtra("usernames");			
		}
		
//		serverAPI_ = new ServerAPI();
		thisActivity_ = this;
		sharedPreferences_ = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		
		String username = sharedPreferences_.getString("preferences_keyUsername", "N/a");
		String sessionId = sharedPreferences_.getString("preferences_keySessionId", "N/a");
		
		authToken_ = new AuthToken(new Username(username), new SessionId(sessionId));
		
		RadioButton isPermissionGranted = (RadioButton) findViewById(R.id.permissionGranted);
		RadioButton isPermissionDenied = (RadioButton) findViewById(R.id.permissionDenied);
		
		isPermissionGranted.setOnClickListener(new OnClickListener() {
			Button buttonOK = (Button) findViewById(R.id.socialPermissionOK);
			public void onClick(View v) {
				if(!buttonOK.isEnabled())
					buttonOK.setEnabled(true);
			}
		});
		
		isPermissionDenied.setOnClickListener(new OnClickListener() {
			Button buttonOK = (Button) findViewById(R.id.socialPermissionOK);
			public void onClick(View v) {
				if(!buttonOK.isEnabled())
					buttonOK.setEnabled(true);
			}
		});
		
		Button buttonOK = (Button) findViewById(R.id.socialPermissionOK);
		Button buttonCancel = (Button) findViewById(R.id.socialPermissionCancel);
		
		buttonOK.setOnClickListener(new OnClickListener() {
			private RadioButton isPremissionGranted = (RadioButton) findViewById(R.id.permissionGranted);
			private Spinner spinnerService = (Spinner) findViewById(R.id.socialSpinnerServices);
			
			@Override
			public void onClick(View v) {
				try {
					boolean operationStatus = false;
					
					if(spinnerService.getSelectedItem().toString().equals(thisActivity_.getString(R.string.social_permissions_gps_localization))) {
						Toast.makeText(thisActivity_, thisActivity_.getString(R.string.social_permissions_updated), Toast.LENGTH_SHORT).show();

//						operationStatus = serverAPI_.setPositionPermission(selectedUsernames_, isPremissionGranted.isChecked(), authToken_);
						
					} else {
						//other services
					}
					
//					if(!operationStatus) 
//						new NotConnectedAlert(thisActivity_);
//					else
						onBackPressed(); 
					
				} catch(Exception e) {
					e.printStackTrace();
					new NotConnectedAlert(thisActivity_, thisActivity_.getString(R.string.social_login));
				}
			}
		});
		
		buttonCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		this.finish();
	}
}
