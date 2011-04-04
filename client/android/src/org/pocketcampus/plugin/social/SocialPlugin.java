package org.pocketcampus.plugin.social;

import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.plugin.logging.Tracker;

import android.content.Intent;
import android.os.Bundle;

public class SocialPlugin extends PluginBase {

	SocialPreference preferences_;
	boolean logged_ = true;
	SocialPlugin thisActivity_ = this;
	Tracker tracker_;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		tracker_ = Tracker.getInstance();
		
		if(logged_) {
			Intent socialLoginIntent = new Intent(thisActivity_, SocialLogin.class);
        	startActivity(socialLoginIntent);
        	thisActivity_.finish();
		} else {
			
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