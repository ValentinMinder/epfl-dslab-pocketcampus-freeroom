package org.pocketcampus.plugin.social;

import org.pocketcampus.core.plugin.Icon;
import org.pocketcampus.core.plugin.Id;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.VersionNumber;
import org.pocketcampus.R;
public class SocialInfo extends PluginInfo {
	
	Id id_ = new Id("social.Social");
	String name_ = "Social";
	
	@Override
	public Icon getIcon() {
		return new Icon(R.drawable.social_menu);
	}

	@Override
	public Id getId() {
		return id_;
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
		return true;
	}

	@Override
	public Icon getMiniIcon() {
		return new Icon(R.drawable.social_normal_mini);
	}

}
