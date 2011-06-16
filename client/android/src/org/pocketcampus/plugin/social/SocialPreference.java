package org.pocketcampus.plugin.social;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.core.ui.ActionBar;
import org.pocketcampus.plugin.mainscreen.MainscreenPlugin;
//import org.pocketcampus.shared.plugin.authentication.AuthToken;
//import org.pocketcampus.plugin.authentication.AuthenticationPlugin;
//
//import android.content.Intent;
//import android.preference.Preference;
//import android.preference.PreferenceCategory;
//import android.preference.PreferenceScreen;
//import android.preference.Preference.OnPreferenceClickListener;
import android.os.Bundle;

public class SocialPreference extends PluginPreference {
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainscreen_preference);
		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.setTitle(getResources().getText(R.string.app_name));
		actionBar.addAction(new ActionBar.IntentAction(this, MainscreenPlugin.createIntent(this), R.drawable.mini_home));
		
//		setPreferenceScreen(createPreferenceHierarchy());
            
	}
    
//	private PreferenceScreen createPreferenceHierarchy() {
//		PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);
//
//		PreferenceCategory importCategory = new PreferenceCategory(this);
//		importCategory.setTitle("[HC] Imports");
//		root.addPreference(importCategory);
//		
//		AuthToken token = AuthenticationPlugin.getAuthToken(this);
//		
//		Preference fbImport = new Preference(this);
//		fbImport.setEnabled(token != null);
//		fbImport.setTitle("[HC] Facebook");
//		fbImport.setSummary("[HC] Import your facebook friends list");
//		fbImport.setOnPreferenceClickListener(new OnPreferenceClickListener() {
//			
//			@Override
//			public boolean onPreferenceClick(Preference preference) {
//				SocialPreference.this.startActivity(new Intent(SocialPreference.this, SocialFacebookFinder.class));
//				SocialPreference.this.finish();
//				return false;
//			}
//		});
//		importCategory.addPreference(fbImport);
//		
//		Preference phoneImport = new Preference(this);
//		phoneImport.setEnabled(token != null);
//		phoneImport.setTitle("[HC] Phone");
//		phoneImport.setSummary("[HC] Import contacts from your phone");
//		phoneImport.setOnPreferenceClickListener(new OnPreferenceClickListener() {
//			
//			@Override
//			public boolean onPreferenceClick(Preference preference) {
//				
//				return false;
//			}
//		});
//		importCategory.addPreference(phoneImport);
//		
//		return root;
//	}
    
}
