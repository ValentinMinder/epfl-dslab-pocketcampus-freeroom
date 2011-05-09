package org.pocketcampus.plugin.bikes;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.Icon;
import org.pocketcampus.core.plugin.Id;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.VersionNumber;

public class BikesInfo extends PluginInfo {
	
	@Override
	public int getNameResource() {
		return R.string.bikes_plugin_title;
	}

	@Override
	public Id getId() {
		return new Id("bikes");
	}

	@Override
	public VersionNumber getVersion() {
		return new VersionNumber();
	}

	@Override
	public Icon getIcon() {
		return new Icon(R.drawable.bikes_normal_menu);
	}

	@Override
	public Icon getMiniIcon() {
		return new Icon(R.drawable.bikes_normal_mini);
	}

	@Override
	public boolean hasMenuIcon() {
		return true;
	}

}
