package org.pocketcampus.plugin.labs;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.Icon;
import org.pocketcampus.core.plugin.Id;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.VersionNumber;

public class LabsInfo extends PluginInfo {

	@Override
	public Id getId() {
		return new Id("labs");
	}

	@Override
	public VersionNumber getVersion() {
		return new VersionNumber();
	}

	@Override
	public Icon getIcon() {
		return new Icon(R.drawable.labs_menu);
	}

	@Override
	public Icon getMiniIcon() {
		return new Icon(R.drawable.labs_normal_mini);
	}

	@Override
	public boolean hasMenuIcon() {
		return true;
	}

	@Override
	public int getNameResource() {
		return R.string.labs_plugin_title;
	}

}
