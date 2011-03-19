package org.pocketcampus.plugin.test;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.Icon;
import org.pocketcampus.core.plugin.Id;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.VersionNumber;

public class TestInfo extends PluginInfo {

	@Override
	public Icon getIcon() {
		return new Icon(R.drawable.test_menu);
	}

	@Override
	public Id getId() {
		return new Id("test");
	}

	@Override
	public String getName() {
		return "Test Server";
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
