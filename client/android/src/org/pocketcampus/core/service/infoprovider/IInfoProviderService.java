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
	/**
	 * Rate at which the informations should be retrieved.
	 * @return
	 */
	public RefreshRate refreshRate();
	
	/**
	 * Retrieves the informations.
	 * @param maxNb
	 * @return
	 */
	public Informations getInformations(int maxNb);
}
