package org.pocketcampus.plugin.mainscreen;

import org.pocketcampus.core.Icon;
import org.pocketcampus.core.Id;
import org.pocketcampus.core.PluginBase;
import org.pocketcampus.core.VersionNumber;

public class MainscreenPlugin extends PluginBase {
	@Override
	public Icon getIcon() {
		return new Icon();
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
