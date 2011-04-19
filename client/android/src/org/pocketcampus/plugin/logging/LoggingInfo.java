package org.pocketcampus.plugin.logging;

import org.pocketcampus.core.plugin.Icon;
import org.pocketcampus.core.plugin.Id;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.VersionNumber;

public class LoggingInfo extends PluginInfo {
	private final Id id_ = new Id("logging");
	private final String name_ = "Logging";
	
	@Override
	public Icon getIcon() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Id getId() {
		return id_;
	}
	@Override
	public Icon getMiniIcon() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getName() {
		return name_;
	}
	@Override
	public VersionNumber getVersion() {
		return new VersionNumber();
	}
	@Override
	public boolean hasMenuIcon() {
		// TODO Auto-generated method stub
		return false;
	}
}
