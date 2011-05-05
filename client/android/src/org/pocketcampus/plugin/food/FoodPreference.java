package org.pocketcampus.plugin.food;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import org.apache.http.entity.SerializableEntity;
import org.pocketcampus.R;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.core.ui.ActionBar;
import org.pocketcampus.plugin.mainscreen.MainscreenPlugin;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;
import android.widget.TextView;

public class FoodPreference extends PluginPreference {
	private SharedPreferences restoPrefs_;
	private Editor restoPrefsEditor_;

	private final String RESTO_PREFS_NAME = "RestoPrefs";
	
	private ArrayList<String> restaurants_;
	private ArrayList<String> displayedRestaurants_;
	protected final static String cacheTime_ = "food_cache_time";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.mainscreen_preference);
		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.setTitle(getResources().getText(R.string.app_name));
		actionBar.addAction(new ActionBar.IntentAction(this, MainscreenPlugin.createIntent(this), R.drawable.mini_home));

		restaurants_ = new ArrayList<String>();

		restoPrefs_ = getSharedPreferences(RESTO_PREFS_NAME, 0);
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
		CheckBoxPreference prefBox;
		
		restaurants_ = FoodPlugin.getRestaurantList();

		if(!restaurants_.isEmpty()){
			displayedRestaurants_ = restaurants_;
			
			for(String resto : restaurants_) {

				prefBox = new CheckBoxPreference(this);
				prefBox.setKey(resto);
				prefBox.setTitle(resto);
				prefBox.setDefaultValue(true);

				prefBox.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){

					@Override
					public boolean onPreferenceChange(Preference preference, Object newValue) {
						String r = preference.getKey();
						
						if((Boolean)newValue){
							displayedRestaurants_.add(r);
						}else{
							displayedRestaurants_.remove(r);
						}
						int i = 1;
						for(String s : displayedRestaurants_){
							Log.d("PREFERENCES",i + " : " + s);
							i++;
						}
						writeToFile();						
						PreferenceManager.getDefaultSharedPreferences(that).edit().putLong(cacheTime_, 0).commit();
						return true;
					}

				});

				foodPrefCat.addPreference(prefBox);
			}
		}else{
			Log.d("PREFERENCES","There are no Restaurant List for now.");
			TextView text = new TextView(this);
			text.setText(getResources().getString(R.string.food_preferences_warning));
			
		}

		return root;
	}
	
	public void writeToFile() {
		String filename = "RestaurantsCache";

		File menuFile = new File(this.getCacheDir(), filename);

		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try {
			fos = new FileOutputStream(menuFile);
			out = new ObjectOutputStream(fos);
			out.writeObject(displayedRestaurants_);
			out.close();
		} catch (IOException ex) {
			Log.d("PREFERENCES","Writing IO Exception");
		}
	}
}
