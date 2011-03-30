package org.pocketcampus.core.router;

public interface IServerBase {
	
	/**
	 * Give the name of the default method when calling the servlet without arguments
	 * @return default method name
	 */
	public String getDefaultMethod();
}
