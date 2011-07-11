package org.pocketcampus.plugin.scanner;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.Icon;
import org.pocketcampus.core.plugin.Id;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.VersionNumber;

public class ScannerInfo extends PluginInfo {

	@Override
	public Id getId() {
		return new Id("scanner");
	}

	@Override
	public VersionNumber getVersion() {
		return new VersionNumber();
	}

	@Override
	public Icon getIcon() {
		return new Icon(R.drawable.scanner_menu);
	}

	@Override
	public Icon getMiniIcon() {
		return new Icon(R.drawable.scanner_normal_mini);
	}

	@Override
	public boolean hasMenuIcon() {
		return true;
	}

	@Override
	public int getNameResource() {
		return R.string.scanner_plugin_title;
	}

}
