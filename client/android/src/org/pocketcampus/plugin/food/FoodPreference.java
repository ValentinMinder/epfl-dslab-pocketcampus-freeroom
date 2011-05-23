package org.pocketcampus.plugin.food;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

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

public class FoodPreference extends PluginPreference {
	private SharedPreferences restoPrefs_;
	private Editor restoPrefsEditor_;

	private final String RESTO_PREFS_NAME = "RestoPrefs";
	private File menuFile_;

	private ArrayList<String> restaurants_;
	private ArrayList<String> displayedRestaurants_;
	protected final static String cacheTime_ = "food_cache_time";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainscreen_preference);
		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.setTitle(getResources().getText(R.string.app_name));
		actionBar.addAction(new ActionBar.IntentAction(this, MainscreenPlugin
				.createIntent(this), R.drawable.mini_home));

		restaurants_ = new ArrayList<String>();
		restaurants_ = getRestaurants();
		displayedRestaurants_ = new ArrayList<String>();

		restoPrefs_ = getSharedPreferences(RESTO_PREFS_NAME, 0);
		restoPrefsEditor_ = restoPrefs_.edit();
		
		if(restoPrefs_.getAll().isEmpty()){
			Log.d("PREFERENCES","First time instanciatation.");
			for(String r : restaurants_){
				restoPrefsEditor_.putBoolean(r, true);
			}
			restoPrefsEditor_.commit();
		}

		setPreferenceScreen(createPreferenceHierarchy());
	}

	private PreferenceScreen createPreferenceHierarchy() {
		PreferenceScreen root = getPreferenceManager().createPreferenceScreen(
				this);

		PreferenceCategory foodPrefCat = new PreferenceCategory(this);
		foodPrefCat.setTitle(R.string.food_preferences_title);
		root.addPreference(foodPrefCat);

		final FoodPreference that = this;
		CheckBoxPreference prefBox;
		
		/* Change to get the list from a permanent txt file. 
		restaurants_ = getRestaurants();

		Map<String, String> restos = (Map<String, String>) restoPrefs_.getAll();
		
		for(String resto : restaurants_){			
			prefBox = new CheckBoxPreference(this);
			prefBox.setKey(resto);
			prefBox.setTitle(resto);
			prefBox.setDefaultValue(true);

			prefBox.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

				@Override
				public boolean onPreferenceChange(Preference preference,
						Object newValue) {
					String r = preference.getKey();

					restoPrefsEditor_.putBoolean(r, (Boolean)newValue);
					restoPrefsEditor_.commit();
					
					return true;
				}

			});

			foodPrefCat.addPreference(prefBox);
		}
		
		*/
		
		
//		/* Change to get the list from a permanent txt file. */
//		restaurants_ = getRestaurants();
		
//		displayedRestaurants_ = readFromFile();
		
		for(String r : restaurants_){	
			if(restoPrefs_.getBoolean(r, false)){
				displayedRestaurants_.add(r);
			}
		}

		if (displayedRestaurants_.isEmpty()) {
			Log.d("PREFERENCES",
					"displayedRestaurants_ was null or empty from file!");
			displayedRestaurants_ = restaurants_;
		}else{
			
			for (String resto : restaurants_) {

				prefBox = new CheckBoxPreference(this);
				prefBox.setKey(resto);
				prefBox.setTitle(resto);
				prefBox.setDefaultValue(true);

				prefBox.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						String r = preference.getKey();

						if ((Boolean) newValue) {
							Log.d("PREFERENCES", "If " + newValue.toString());
							displayedRestaurants_.add(r);
							restoPrefsEditor_.putBoolean(r, (Boolean)newValue);
							restoPrefsEditor_.commit();
						} else {
							Log.d("PREFERENCES", "Else " + newValue.toString());
							displayedRestaurants_.remove(r);
							restoPrefsEditor_.putBoolean(r, false);
							restoPrefsEditor_.commit();
						}
//						writeToFile();
						PreferenceManager.getDefaultSharedPreferences(that)
								.edit().putLong(cacheTime_, 0).commit();
						return true;
					}

				});

				foodPrefCat.addPreference(prefBox);
			}
		}

		return root;
	}

	public void writeToFile() {
		String filename = "RestaurantsPref";

		menuFile_ = new File(this.getDir("preferences", 0), filename);

		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try {
			fos = new FileOutputStream(menuFile_);
			out = new ObjectOutputStream(fos);
			out.writeObject(displayedRestaurants_);
			out.close();
		} catch (IOException ex) {
			Log.d("PREFERENCES", "Writing IO Exception");
		}
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> readFromFile() {
		String filename = "RestaurantsPref";
		ArrayList<String> restosDisplayed = null;
		File toGet = new File(this.getDir("preferences", 0), filename);
		FileInputStream fis = null;
		ObjectInputStream in = null;

		try {
			fis = new FileInputStream(toGet);
			in = new ObjectInputStream(fis);

			restosDisplayed = (ArrayList<String>) in.readObject();

			in.close();
		} catch (IOException ex) {
		} catch (ClassNotFoundException ex) {
		} catch (ClassCastException cce) {
		}

		return restosDisplayed;
	}

	private ArrayList<String> getRestaurants() {
		ArrayList<String> list = new ArrayList<String>();

		try {
			InputStream instream = this.getClass().getResourceAsStream(
					"restaurants_names.txt");

			if (instream != null) {

				InputStreamReader inputreader = new InputStreamReader(instream);
				BufferedReader input = new BufferedReader(inputreader);

				try {
					String line = null; // not declared within while loop

					while ((line = input.readLine()) != null) {
						list.add(line);
					}
				} finally {
					input.close();
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return list;
	}
}
