package org.pocketcampus.plugin.social;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SocialLogout extends Activity {
	Activity thisActivity_ = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//do the logout
		thisActivity_.startActivity(new Intent(thisActivity_, SocialLogin.class));
		thisActivity_.finish();
	}
	
	public static void logout(Activity parentActivity) {
		parentActivity.startActivity(new Intent(parentActivity, SocialLogout.class));
		parentActivity.finish();
	}
}
