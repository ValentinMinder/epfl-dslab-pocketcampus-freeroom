package org.pocketcampus.plugin.food;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.ConfigurationBase;
import org.pocketcampus.core.plugin.DisplayBase;
import org.pocketcampus.core.plugin.Icon;
import org.pocketcampus.core.plugin.Id;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.VersionNumber;

public class FoodPlugin extends PluginBase {

	@Override
	public Class<? extends ConfigurationBase> getConfigurationClass() {
		return FoodConfiguration.class;
		//return null;
	}

	@Override
	public Class<? extends DisplayBase> getDisplayClass() {
		return FoodDisplay.class;
		//		return null;
	}

	@Override
	public Icon getIcon() {
		return new Icon(R.drawable.menu_food);
	}

	@Override
	public Id getId() {
		return new Id("food");
	}

	@Override
	public String getName() {
		return "Food Service";
	}

	@Override
	public VersionNumber getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

}
