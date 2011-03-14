package org.pocketcampus.core.service.infoprovider;

/**
 * Service interface. Allows a plugin to provide informations to the platform.
 * Eg the News plugin can give the last news item, the Menu plugin the closest menus available...
 * 
 * @status incomplete
 * @author florian
 * @license
 *
 */

public interface IInfoProviderService {
	// refresh rate
	public RefreshRate refreshRate();
	
	// what to display
	public Informations getInformations(int maxNb);
}
