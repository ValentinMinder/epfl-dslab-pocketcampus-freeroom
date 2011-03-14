package org.pocketcampus.plugin.positioning;

import org.pocketcampus.core.plugin.ConfigurationBase;
import org.pocketcampus.core.plugin.DisplayBase;
import org.pocketcampus.core.plugin.Icon;
import org.pocketcampus.core.plugin.Id;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.VersionNumber;

public class PositioningPlugin extends PluginBase {

	@Override
	public Class<? extends ConfigurationBase> getConfigurationClass() {
		return null;
	}

	@Override
	public Class<? extends DisplayBase> getDisplayClass() {
		return null;
	}

	@Override
	public Icon getIcon() {
		return null;
	}

	@Override
	public Id getId() {
		return new Id("positioning");
	}

	@Override
	public String getName() {
		return "Positioning System";
	}

	@Override
	public VersionNumber getVersion() {
		return null;
	}

}
