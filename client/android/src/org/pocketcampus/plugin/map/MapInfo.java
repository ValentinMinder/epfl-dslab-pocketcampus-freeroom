package org.pocketcampus.plugin.map;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.Icon;
import org.pocketcampus.core.plugin.Id;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.VersionNumber;

/**
 * PluginInfo class for the map plugin 
 * 
 * @status complete
 * 
 * @author Jonas, Johan
 *
 */
public class MapInfo extends PluginInfo {

	@Override
	public Icon getIcon() {
		return new Icon(R.drawable.map_menu);
	}

	@Override
	public Id getId() {
		return new Id("Map");
	}

	@Override
	public String getName() {
		return "Map";
	}

	@Override
	public VersionNumber getVersion() {
		return new VersionNumber();
	}

	@Override
	public boolean hasMenuIcon() {
		return true;
	}

	@Override
	public Icon getMiniIcon() {
		return new Icon(R.drawable.map_normal_mini);
	}

}
