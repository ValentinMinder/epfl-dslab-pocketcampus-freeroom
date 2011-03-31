package org.pocketcampus.plugin.bikes;

import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;

import android.view.View;
import android.view.View.OnClickListener;

public class BikesPlugin extends PluginBase implements OnClickListener{

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PluginInfo getPluginInfo() {
		// TODO Auto-generated method stub
		return new BikesInfo();
	}

	@Override
	public PluginPreference getPluginPreference() {
		// TODO Auto-generated method stub
		return null;
	}




}
