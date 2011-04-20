package org.pocketcampus.plugin.social;

import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.plugin.authentication.AuthenticationPlugin;
import org.pocketcampus.plugin.logging.Tracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class SocialPlugin extends PluginBase {

	SocialPreference preferences_;
	boolean logged_ = true;
	SocialPlugin thisActivity_ = this;
	Tracker tracker_;
	private SharedPreferences sharedPreferences_;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		tracker_ = Tracker.getInstance();
		
		sharedPreferences_ = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		
		String username = sharedPreferences_.getString("username", null);
		String sessionId = sharedPreferences_.getString("sessionId", null);
		
		if(AuthenticationPlugin.authenticate(username, sessionId)) {
			Intent socialLoginIntent = new Intent(thisActivity_, SocialLogin.class);
        	startActivity(socialLoginIntent);
        	thisActivity_.finish();
		} else {
			//not logged in
		}
	}
	
	@Override
	public PluginInfo getPluginInfo() {
		return new SocialInfo();
	}
	
	@Override
	public PluginPreference getPluginPreference() {
		return preferences_;
	}
}