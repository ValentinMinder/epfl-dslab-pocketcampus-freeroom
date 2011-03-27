package org.pocketcampus.plugin.transport;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.Icon;
import org.pocketcampus.core.plugin.Id;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.VersionNumber;

/**
 * PluginInfo class for the Camipro plugin.
 * 
 * @status complete
 * 
 * @author Jonas
 *
 */
public class TransportInfo extends PluginInfo {

	@Override
	public Icon getIcon() {
		return new Icon(R.drawable.transport_menu);
	}

	@Override
	public Id getId() {
		return new Id("transport");
	}

	@Override
	public Icon getMiniIcon() {
		return new Icon(R.drawable.transport_normal_mini);
	}

	@Override
	public String getName() {
		return "Transport";
	}

	@Override
	public VersionNumber getVersion() {
		return new VersionNumber();
	}

	@Override
	public boolean hasMenuIcon() {
		return true;
	}

}
