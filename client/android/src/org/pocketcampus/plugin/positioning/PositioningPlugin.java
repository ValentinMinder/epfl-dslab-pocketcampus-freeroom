package org.pocketcampus.plugin.positioning;

import org.pocketcampus.core.ConfigurationBase;
import org.pocketcampus.core.DisplayBase;
import org.pocketcampus.core.Icon;
import org.pocketcampus.core.Id;
import org.pocketcampus.core.PluginBase;
import org.pocketcampus.core.VersionNumber;

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
