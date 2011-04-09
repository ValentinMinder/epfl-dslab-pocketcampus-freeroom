package org.pocketcampus.plugin.news;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.Icon;
import org.pocketcampus.core.plugin.Id;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.VersionNumber;

/**
 * PluginInfo class for the News plugin. 
 * 
 * @status incomplete, need to implement the IInfoProviderService.
 * 
 * @author Jonas
 *
 */
public class NewsInfo extends PluginInfo {

	@Override
	public Icon getIcon() {
		return new Icon(R.drawable.news_menu);
	}

	@Override
	public Icon getMiniIcon() {
		return new Icon(R.drawable.news_normal_mini);
	}

	@Override
	public Id getId() {
		return new Id("news");
	}

	@Override
	public String getName() {
		return "News";
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
