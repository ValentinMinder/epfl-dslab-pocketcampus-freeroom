package org.pocketcampus.core;

public interface IInfoProviderService {
	// refresh rate
	public RefreshRate refreshRate();
	
	// what to display
	public Informations getInformations(int maxNb);
}
