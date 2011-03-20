package org.pocketcampus.plugin.news;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.Icon;
import org.pocketcampus.core.plugin.Id;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.VersionNumber;
import org.pocketcampus.core.service.infoprovider.IInfoProviderService;
import org.pocketcampus.core.service.infoprovider.Informations;
import org.pocketcampus.core.service.infoprovider.RefreshRate;

/**
 * PluginInfo class for the News plugin. 
 * 
 * @status incomplete, need to implement the IInfoProviderService.
 * 
 * @author Jonas
 *
 */
public class NewsInfo extends PluginInfo implements IInfoProviderService {

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
	public Informations getInformations(int maxNb) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RefreshRate refreshRate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasMenuIcon() {
		return true;
	}
}
