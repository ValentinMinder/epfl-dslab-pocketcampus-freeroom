package org.pocketcampus.plugin.food;

import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;

public class FoodPlugin extends PluginBase {

	@Override
	public PluginInfo getPluginInfo() {
		// TODO Auto-generated method stub
		return new FoodInfo();
	}

	@Override
	public PluginPreference getPluginPreference() {
		// TODO Auto-generated method stub
		return null;
	}

}
