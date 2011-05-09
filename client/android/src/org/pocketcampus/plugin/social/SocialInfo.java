package org.pocketcampus.plugin.social;

import org.pocketcampus.core.plugin.Icon;
import org.pocketcampus.core.plugin.Id;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.VersionNumber;
import org.pocketcampus.R;
public class SocialInfo extends PluginInfo {
	
	private final Id id_ = new Id("social");
	
	@Override
	public Icon getIcon() {
		return new Icon(R.drawable.social_menu);
	}

	@Override
	public Id getId() {
		return id_;
	}
	
	@Override
	public int getNameResource() {
		return R.string.social_plugin_title;
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
		return new Icon(R.drawable.social_normal_mini);
	}

}
