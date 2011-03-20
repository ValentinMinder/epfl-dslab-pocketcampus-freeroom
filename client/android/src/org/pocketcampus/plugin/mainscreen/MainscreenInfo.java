package org.pocketcampus.plugin.mainscreen;

import org.pocketcampus.core.plugin.Icon;
import org.pocketcampus.core.plugin.Id;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.VersionNumber;

public class MainscreenInfo extends PluginInfo {

	@Override
	public Icon getIcon() {
		return null;
	}

	@Override
	public Id getId() {
		return new Id("mainscreen");
	}

	@Override
	public String getName() {
		return "Mainscreen";
	}

	@Override
	public VersionNumber getVersion() {
		return new VersionNumber();
	}

	@Override
	public boolean hasMenuIcon() {
		return false;
	}

	@Override
	public Icon getMiniIcon() {
		// TODO Auto-generated method stub
		return null;
	}

}
