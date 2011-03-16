package org.pocketcampus.plugin.menu;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.ConfigurationBase;
import org.pocketcampus.core.plugin.DisplayBase;
import org.pocketcampus.core.plugin.Icon;
import org.pocketcampus.core.plugin.Id;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.VersionNumber;

public class MenuPlugin extends PluginBase {

	@Override
	public Class<? extends ConfigurationBase> getConfigurationClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<? extends DisplayBase> getDisplayClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Icon getIcon() {
		return new Icon(R.drawable.menu_menu);
	}

	@Override
	public Id getId() {
		return new Id("menus");
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
