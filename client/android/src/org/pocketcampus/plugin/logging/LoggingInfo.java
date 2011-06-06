package org.pocketcampus.plugin.logging;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.Icon;
import org.pocketcampus.core.plugin.Id;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.VersionNumber;

public class LoggingInfo extends PluginInfo {
	private final Id id_ = new Id("logging");
	
	@Override
	public Icon getIcon() {
		return null;
	}
	@Override
	public Id getId() {
		return id_;
	}
	@Override
	public Icon getMiniIcon() {
		return null;
	}
	
	@Override
	public int getNameResource() {
		return R.string.logging_plugin_title;
	}
	
	@Override
	public VersionNumber getVersion() {
		return new VersionNumber();
	}
	@Override
	public boolean hasMenuIcon() {
		return false;
	}
}
