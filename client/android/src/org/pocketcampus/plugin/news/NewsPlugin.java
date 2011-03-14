package org.pocketcampus.plugin.news;

import org.pocketcampus.core.ConfigurationBase;
import org.pocketcampus.core.DisplayBase;
import org.pocketcampus.core.Icon;
import org.pocketcampus.core.Id;
import org.pocketcampus.core.PluginBase;
import org.pocketcampus.core.VersionNumber;

public class NewsPlugin extends PluginBase {

	@Override
	public Icon getIcon() {
		return new Icon();
	}

	@Override
	public Id getId() {
		return new Id("news");
	}

	@Override
	public String getName() {
		return "News";
	}

	@Override
	public VersionNumber getVersion() {
		return new VersionNumber();
	}

	@Override
	public Class<? extends ConfigurationBase> getConfigurationClass() {
		return null;
	}

	@Override
	public Class<? extends DisplayBase> getDisplayClass() {
		return NewsDisplay.class;
	}
}
