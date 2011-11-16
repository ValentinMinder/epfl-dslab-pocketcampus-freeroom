package org.pocketcampus.plugin.food.android;

import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardLayout;
import org.pocketcampus.plugin.food.shared.Restaurant;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.util.Log;

public class FoodSettings extends PreferenceActivity{

	@Override
    protected void onCreate(Bundle savedInstanceState) {
 
        super.onCreate(savedInstanceState); 
        addPreferencesFromResource(R.layout.settings);
        StandardLayout layout = new StandardLayout(this);
        List<Restaurant> list = handleExtras();
        
        for(Restaurant o : list) {
        	Log.d("SETTINGS", "Resto : " + o.toString());
        	CheckBoxPreference c = new CheckBoxPreference(this);
        	c.setTitle(o.getName());
        	addPreferencesFromResource(c.getWidgetLayoutResource());
        }
        
        setContentView(layout);
    }
	
	private List<Restaurant> handleExtras(){
		List<Restaurant> extrasList = new ArrayList<Restaurant>();
		Bundle extras = getIntent().getExtras();
		
		if (extras != null) {
			@SuppressWarnings("unchecked")
			ArrayList<Restaurant> m = (ArrayList<Restaurant>) extras
					.getSerializable("org.pocketcampus.settings.restaurants");
			if (m != null && !m.isEmpty()) {
				for (Restaurant meal : m) {
					extrasList.add(meal);
				}
			}
		} else {
			Log.d("SETTINGS", "No extras rceived!");
		}
		
		return extrasList;
	}
}
