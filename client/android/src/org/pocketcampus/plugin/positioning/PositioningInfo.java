package org.pocketcampus.plugin.positioning;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.Icon;
import org.pocketcampus.core.plugin.Id;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.VersionNumber;

public class PositioningInfo extends PluginInfo {

	@Override
	public Id getId() {
		return new Id("positioning");
	}

	@Override
	public VersionNumber getVersion() {
		return new VersionNumber();
	}

	@Override
	public Icon getIcon() {
		return new Icon(R.drawable.positioning_menu);
	}

	@Override
	public Icon getMiniIcon() {
		return new Icon(R.drawable.positioning_normal_mini);
	}

	@Override
	public boolean hasMenuIcon() {
		return true;
	}

	@Override
	public int getNameResource() {
		return R.string.positioning_plugin_title;
	}

}
