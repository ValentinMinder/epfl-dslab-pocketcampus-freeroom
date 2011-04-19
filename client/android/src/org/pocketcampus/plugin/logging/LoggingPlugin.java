package org.pocketcampus.plugin.logging;

import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;

public class LoggingPlugin extends PluginBase {
	private LoggingPreference preferences_;
	
	@Override
	public PluginInfo getPluginInfo() {
		return new LoggingInfo();
		
	}

	@Override
	public PluginPreference getPluginPreference() {
		return preferences_;
	}

}
