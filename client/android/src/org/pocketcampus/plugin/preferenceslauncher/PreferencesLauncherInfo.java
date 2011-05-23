package org.pocketcampus.plugin.preferenceslauncher;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.Icon;
import org.pocketcampus.core.plugin.Id;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.VersionNumber;

public class PreferencesLauncherInfo extends PluginInfo {

	@Override
	public int getNameResource() {
		return R.string.preferenceslauncher_plugin_title;
	}

	@Override
	public Id getId() {
		return new Id("preferenceslauncher");
	}

	@Override
	public VersionNumber getVersion() {
		return new VersionNumber();
	}

	@Override
	public Icon getIcon() {
		return new Icon(android.R.drawable.ic_menu_manage);
	}

	@Override
	public Icon getMiniIcon() {
		return new Icon(android.R.drawable.ic_menu_manage);
	}

	@Override
	public boolean hasMenuIcon() {
		return true;
	}

}
