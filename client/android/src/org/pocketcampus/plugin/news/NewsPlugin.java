package org.pocketcampus.plugin.news;

import org.pocketcampus.core.plugin.ConfigurationBase;
import org.pocketcampus.core.plugin.DisplayBase;
import org.pocketcampus.core.plugin.Icon;
import org.pocketcampus.core.plugin.Id;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.VersionNumber;
import org.pocketcampus.core.service.infoprovider.IInfoProviderService;
import org.pocketcampus.core.service.infoprovider.Informations;
import org.pocketcampus.core.service.infoprovider.RefreshRate;

public class NewsPlugin extends PluginBase implements IInfoProviderService {

	@Override
	public Icon getIcon() {
		return new Icon();
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
	public Class<? extends ConfigurationBase> getConfigurationClass() {
		return null;
	}

	@Override
	public Class<? extends DisplayBase> getDisplayClass() {
		return NewsDisplay.class;
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
}
