package org.pocketcampus.plugin.transport;

import java.util.ArrayList;
import java.util.Vector;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.Core;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.core.ui.ActionBar;
import org.pocketcampus.plugin.mainscreen.MainscreenPlugin;
import org.pocketcampus.shared.plugin.transport.Connection;
import org.pocketcampus.shared.plugin.transport.Destination;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceClickListener;
import android.view.LayoutInflater;
import android.view.View;

public class TransportPreference extends PluginPreference {
	private Context ctx_;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainscreen_preference);
		ctx_ = this;

		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.setTitle(getResources().getText(R.string.app_name));
		actionBar.addAction(new ActionBar.IntentAction(this, MainscreenPlugin.createIntent(this), R.drawable.mini_home));

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
		Destination referenceDestination = new Destination("Ecublens VD, EPFL");
		Preference refDestPref = new Preference(this);
		refDestPref.setTitle(referenceDestination.getDestination());
		refDestPref.setSummary("Reference destination.");
		refDestPref.setEnabled(false);
		pluginPrefCat.addPreference(refDestPref);
		
		// Common destinations
		// TODO make user editable
		ArrayList<Destination> destinations = new ArrayList<Destination>();
		destinations.add(new Destination("Lausanne, Flon"));
		destinations.add(new Destination("Lausanne, Vigie"));
		
		for (final Destination destination : destinations) {
			Preference commonDestPref = new Preference(this);
			commonDestPref.setTitle(destination.getDestination());
			//commonDestPref.setSummary("Not displayed on the frontpage.");
			
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
		Preference addNewPref = new Preference(this);
		addNewPref.setTitle("Add a destination");
		
		OnPreferenceClickListener onNewDestClickListener = new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				return false;
			}
		};
		
		addNewPref.setOnPreferenceClickListener(onNewDestClickListener);
		pluginPrefCat.addPreference(addNewPref);
		
		
		///////////
		// OTHER //
		///////////
		
		PreferenceCategory otherCat = new PreferenceCategory(this);
		otherCat.setTitle("Other options");
		root.addPreference(otherCat);
		
		// frontpage
		Preference frontpagePref = new Preference(this);
		frontpagePref.setTitle("Frontpage");
		frontpagePref.setSummary("Destinations shown on the frontpage.");
		
		OnPreferenceClickListener onFrontpageOptionsClickListener = new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				return false;
			}
		};
		
		frontpagePref.setOnPreferenceClickListener(onFrontpageOptionsClickListener);
		otherCat.addPreference(frontpagePref);
		
		return root;
	}
	
	private void showDestinationDialog(Destination destination) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(destination.getDestination());

		builder.setPositiveButton("Move up", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {				
				dialog.dismiss();
			}
		});
		
		builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {				
				dialog.dismiss();
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
}
