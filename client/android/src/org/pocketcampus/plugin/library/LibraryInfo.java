package org.pocketcampus.plugin.library;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.Icon;
import org.pocketcampus.core.plugin.Id;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.VersionNumber;

/**
 * 
 * 
 * @status WIP
 * @author Florian
 *
 */
public class LibraryInfo extends PluginInfo {

	@Override
	public Icon getIcon() {
		return new Icon(R.drawable.library_menu);
	}

	@Override
	public Id getId() {
		return new Id("library");
	}

	@Override
	public Icon getMiniIcon() {
		return new Icon(R.drawable.library_normal_mini);
	}
	
	@Override
	public int getNameResource() {
		return R.string.library_plugin_title;
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
