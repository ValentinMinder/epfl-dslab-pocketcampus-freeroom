package org.pocketcampus.plugin.food;

import java.util.ArrayList;
import java.util.Map;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.core.ui.ActionBar;
import org.pocketcampus.plugin.mainscreen.MainscreenPlugin;
import org.pocketcampus.plugin.news.NewsPreference;
import org.pocketcampus.plugin.transport.AutoCompleteEditTextPreference;
import org.pocketcampus.plugin.transport.TransportPlugin;
import org.pocketcampus.shared.plugin.food.Restaurant;
import org.pocketcampus.shared.plugin.transport.Destination;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;

public class FoodPreference extends PluginPreference {
	private SharedPreferences restoPrefs_;
	private SharedPreferences.Editor restoPrefsEditor_;
	
	private ArrayList<String> restaurants_;
	protected final static String cacheTime_ = "food_cache_time";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.mainscreen_preference);
		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.setTitle(getResources().getText(R.string.app_name));
		actionBar.addAction(new ActionBar.IntentAction(this, MainscreenPlugin.createIntent(this), R.drawable.mini_home));
		
		restaurants_ = new ArrayList<String>();
		
		restoPrefs_ = getSharedPreferences("RestoPrefs", 0);
		restoPrefsEditor_ = restoPrefs_.edit();
		
		setPreferenceScreen(createPreferenceHierarchy());
	}
	
	private PreferenceScreen createPreferenceHierarchy(){
		PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);
		
		/*Restaurants that will be displayed*/
		PreferenceCategory foodPrefCat = new PreferenceCategory(this);
		foodPrefCat.setTitle(R.string.food_preferences_title);
		root.addPreference(foodPrefCat);
		
		final FoodPreference that = this;
        OnPreferenceChangeListener listener = new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference arg0, Object arg1) {
				PreferenceManager.getDefaultSharedPreferences(that).edit().putLong(cacheTime_, 0).commit();
				return true;
			}
		};
		
//		restaurants_ = FoodPlugin.getRestaurantList();
		/*FAKE*/
		restaurants_.add("Le Parmentier");
		restaurants_.add("Cafeteria BC");
		restaurants_.add("Le Vinci");
		restaurants_.add("Le Hodler");
		restaurants_.add("L'ornythorinque");
		
		CheckBoxPreference prefBox;
		int i = 0;
		for(String resto : restaurants_) {

	        prefBox = new CheckBoxPreference(this);
	        prefBox.setKey(resto);
	        prefBox.setTitle(resto);
	        prefBox.setDefaultValue(true);
	        prefBox.setOnPreferenceChangeListener(listener);
	        
	        foodPrefCat.addPreference(prefBox);
		}
		
		return root;
	}
	
	private void showRestaurantDialog(final Restaurant resto) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(resto.getName());
		
		builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				restoPrefsEditor_.remove(resto.getName());
				restoPrefsEditor_.commit();
				dialog.dismiss();
				forceRefresh();
			}
		});

		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {				
				dialog.dismiss();
			}
		});

		AlertDialog alert = builder.create();
		alert.setCanceledOnTouchOutside(true);
		alert.show();
	}
	
	/**
	 * Forces a redisplay of the PreferenceActivity.
	 */
	private void forceRefresh() {
		Intent selfIntent = getIntent();
		selfIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivity(selfIntent);
		finish();
		overridePendingTransition(0, 0);
	}
}
