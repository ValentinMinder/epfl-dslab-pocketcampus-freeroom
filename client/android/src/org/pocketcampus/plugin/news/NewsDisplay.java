package org.pocketcampus.plugin.news;

import org.pocketcampus.core.DisplayBase;
import org.pocketcampus.core.IInfoProviderService;
import org.pocketcampus.core.Informations;
import org.pocketcampus.core.RefreshRate;

public class NewsDisplay extends DisplayBase implements IInfoProviderService {

	@Override
	public Informations getInformations(int maxNb) {
		return null;
	}

	@Override
	public RefreshRate refreshRate() {
		return null;
	}
	
}
