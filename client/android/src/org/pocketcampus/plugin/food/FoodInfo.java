package org.pocketcampus.plugin.food;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.Icon;
import org.pocketcampus.core.plugin.Id;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.VersionNumber;

public class FoodInfo extends PluginInfo {

	@Override
	public Icon getIcon() {
		return new Icon(R.drawable.food_icon);
	}

	@Override
	public Id getId() {
		return new Id("food");
	}

	@Override
	public String getName() {
		return "Food";
	}

	@Override
	public VersionNumber getVersion() {
		return new VersionNumber();
	}

	@Override
	public boolean hasMenuIcon() {
		return true;
	}

}
