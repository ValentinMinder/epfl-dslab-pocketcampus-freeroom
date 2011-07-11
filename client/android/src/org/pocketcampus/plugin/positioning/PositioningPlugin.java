package org.pocketcampus.plugin.positioning;

import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;

import android.os.Bundle;

public class PositioningPlugin extends PluginBase {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
	}
	
	@Override
	public PluginInfo getPluginInfo() {
		return new PositioningInfo();
	}

	@Override
	public PluginPreference getPluginPreference() {
		return null;
	}
}
