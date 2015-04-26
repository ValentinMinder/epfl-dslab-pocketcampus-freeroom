package org.pocketcampus.plugin.directory.android.iface;

import org.pocketcampus.platform.android.core.IView;
import org.pocketcampus.plugin.directory.shared.Person;

/**
 * Interface for the public methods of the DirectoryViews
 * @author Pascal <pascal.scheiben@gmail.com>
 *
 */
public interface IDirectoryView extends IView {
	
	/**
	 * Update display when we get data.
	 * Called from Model
	 * Called on ALL listeners
	 */
	void resultListUpdated();
	
	/**
	 * Display errors and notices.
	 * Called from Request
	 * Called on the particular object that issued the request
	 */
	void networkErrorHappened();
	void ldapServersDown();
	
	void gotPerson(Person p);
	void ambiguousQuery();

}
