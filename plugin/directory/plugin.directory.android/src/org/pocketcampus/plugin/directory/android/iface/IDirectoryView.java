package org.pocketcampus.plugin.directory.android.iface;

import org.pocketcampus.android.platform.sdk.core.IView;

/**
 * Interface for the public methods of the DirectoryViews
 * @author Pascal <pascal.scheiben@gmail.com>
 *
 */
public interface IDirectoryView extends IView {
	/**
	 * Notifies the view that the autocomplete has been updated
	 */
	void autoCompletedUpdated();
	
	/**
	 * Notifies the view that the results of the search request has been updated
	 */
	void resultsUpdated();

	/**
	 * Notifies the view that there is too many results
	 * @param nb Number of maximum results that the server is allowed to return
	 */
	void tooManyResults(int nb);
	
	/**
	 * Notifies the view that the picture has been updated
	 */
	void pictureUpdated();
}
