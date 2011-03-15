package org.pocketcampus.plugin.mainscreen;

import org.pocketcampus.core.plugin.Icon;
import org.pocketcampus.core.plugin.Id;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.VersionNumber;

public class MainscreenPlugin extends PluginBase {
	@Override
	public Icon getIcon() {
		return new Icon(-1);
	}

	@Override
	public Id getId() {
		return new Id("mainscreen");
	}

	@Override
	public String getName() {
		return "Main Screen";
	}

	@Override
	public VersionNumber getVersion() {
		return new VersionNumber();
	}

	@Override
	public Class<MainscreenConfiguration> getConfigurationClass() {
		return MainscreenConfiguration.class;
	}

	@Override
	public Class<MainscreenDisplay> getDisplayClass() {
		return MainscreenDisplay.class;
	}
}
