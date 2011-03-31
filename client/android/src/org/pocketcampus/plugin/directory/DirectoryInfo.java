package org.pocketcampus.plugin.directory;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.Icon;
import org.pocketcampus.core.plugin.Id;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.VersionNumber;

public class DirectoryInfo extends PluginInfo {

	@Override
	public String getName() {
		return "Directory";
	}

	@Override
	public Id getId() {
		return new Id("directory");
	}

	@Override
	public VersionNumber getVersion() {
		return new VersionNumber();
	}

	@Override
	public Icon getIcon() {
		return new Icon(R.drawable.directory_normal_menu);
	}

	@Override
	public Icon getMiniIcon() {
		return new Icon(R.drawable.directory_mini1);
	}

	@Override
	public boolean hasMenuIcon() {
		return true;
	}

}
