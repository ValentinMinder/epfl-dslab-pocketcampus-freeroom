package org.pocketcampus.plugin.transport;

import java.util.Map;
import org.pocketcampus.R;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.core.ui.ActionBar;
import org.pocketcampus.plugin.mainscreen.MainscreenPlugin;
import org.pocketcampus.shared.plugin.transport.Destination;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceClickListener;

public class TransportPreference extends PluginPreference {
	private SharedPreferences commonDestPrefs_;
	private SharedPreferences miscPrefs_;
	private SharedPreferences.Editor commonDestPrefsEditor_;
	private SharedPreferences.Editor miscPrefsEditor_;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainscreen_preference);
		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.setTitle(getResources().getText(R.string.app_name));
		actionBar.addAction(new ActionBar.IntentAction(this, MainscreenPlugin.createIntent(this), R.drawable.mini_home));
		
		commonDestPrefs_ = getSharedPreferences("CommonDestPrefs", 0);
		miscPrefs_ = getSharedPreferences("MiscPrefs", 0);
		commonDestPrefsEditor_ = commonDestPrefs_.edit();
		miscPrefsEditor_ = miscPrefs_.edit();
		
		setPreferenceScreen(createPreferenceHierarchy());
	}

	private PreferenceScreen createPreferenceHierarchy() {
		PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);

		//////////////////
		// DESTINATIONS //
		//////////////////
		PreferenceCategory pluginPrefCat = new PreferenceCategory(this);
		pluginPrefCat.setTitle("Destinations");
		root.addPreference(pluginPrefCat);

		// Reference destination
//		Destination referenceDestination = new Destination("Ecublens VD, EPFL");
//		Preference refDestPref = new Preference(this);
//		refDestPref.setTitle(referenceDestination.getDestination());
//		refDestPref.setSummary("Reference destination.");
//		refDestPref.setEnabled(false);
//		pluginPrefCat.addPreference(refDestPref);

		// Common destinations
		Map<String, String> commonDestinations = (Map<String, String>) commonDestPrefs_.getAll();

		for(String dest : commonDestinations.values()) {
			final Destination destination = new Destination(dest);
			Preference commonDestPref = new Preference(this);
			commonDestPref.setTitle(dest);

			OnPreferenceClickListener onPreferenceClickListener = new OnPreferenceClickListener() {

				@Override
				public boolean onPreferenceClick(Preference preference) {
					showDestinationDialog(destination);
					return false;
				}
			};

			commonDestPref.setOnPreferenceClickListener(onPreferenceClickListener);
			pluginPrefCat.addPreference(commonDestPref);
		}

		// Add new
		AutoCompleteEditTextPreference editTextPref = new AutoCompleteEditTextPreference(this, TransportPlugin.getTransportRequestHandler());
        editTextPref.setTitle("Add a destination");
        
        OnPreferenceChangeListener onPreferenceChangeListener = new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				if(newValue==null || ((String)newValue).equals("")) {
					return false;
				}
				
				commonDestPrefsEditor_.putString((String)newValue, (String)newValue);
				commonDestPrefsEditor_.commit();
				forceRefresh();
				return false;
			}
		};
        
		editTextPref.setOnPreferenceChangeListener(onPreferenceChangeListener);
        
        root.addPreference(editTextPref);

		///////////
		// OTHER //
		///////////

//		PreferenceCategory otherCat = new PreferenceCategory(this);
//		otherCat.setTitle("Other options");
//		root.addPreference(otherCat);
//
//		// frontpage
//		Preference frontpagePref = new CheckBoxPreference(this);
//		frontpagePref.setTitle("Frontpage");
//		frontpagePref.setSummary("Destinations shown on the frontpage.");
//
//		OnPreferenceClickListener onFrontpageOptionsClickListener = new OnPreferenceClickListener() {
//
//			@Override
//			public boolean onPreferenceClick(Preference preference) {
//				// TODO
//				return false;
//			}
//		};
//
//		frontpagePref.setOnPreferenceClickListener(onFrontpageOptionsClickListener);
//		otherCat.addPreference(frontpagePref);
//
//		// time format
//		Preference timeFormatPref = new CheckBoxPreference(this);
//		timeFormatPref.setTitle("Time format");
//		timeFormatPref.setSummary("Select how the departures times are diplayed.");
//
//		OnPreferenceClickListener onTimeFormatOptionsClickListener = new OnPreferenceClickListener() {
//
//			@Override
//			public boolean onPreferenceClick(Preference preference) {
//				// TODO
//				return false;
//			}
//		};
//
//		timeFormatPref.setOnPreferenceClickListener(onTimeFormatOptionsClickListener);
//		otherCat.addPreference(timeFormatPref);

		return root;
	}
	
	private void showDestinationDialog(final Destination destination) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(destination.getDestination());

//		builder.setPositiveButton("Move up", new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int id) {				
//				dialog.dismiss();
//			}
//		});

		builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				commonDestPrefsEditor_.remove(destination.getDestination());
				commonDestPrefsEditor_.commit();
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












