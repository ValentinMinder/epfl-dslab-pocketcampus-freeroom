package org.pocketcampus.plugin.authentication;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.Icon;
import org.pocketcampus.core.plugin.Id;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.VersionNumber;

public class AuthenticationInfo extends PluginInfo {
	private final Id id_ = new Id("authentication");
	private final String name_ = "Authentication";
	
	@Override
	public Icon getIcon() {
		return new Icon(R.drawable.social_menu);
	}

	@Override
	public Id getId() {
		return id_;
	}

	@Override
	public Icon getMiniIcon() {
		return new Icon(R.drawable.social_normal_mini);
	}

	@Override
	public String getName() {
		return name_;
	}
	
	@Override
	public int getNameResource() {
		return R.string.authentication_plugin_title;
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
