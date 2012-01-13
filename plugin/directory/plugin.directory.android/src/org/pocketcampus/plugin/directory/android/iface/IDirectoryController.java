package org.pocketcampus.plugin.directory.android.iface;

import org.pocketcampus.plugin.directory.shared.Person;
import java.util.List;

/**
 * Inteface for the controllers of the diretory plugin.
 * Defines the public methods.
 * @author Pascal <pascal.scheiben@gmail.com>
 *
 */
public interface IDirectoryController {
	
	/**
	 * Will set the results received by the <code>DirectorySearchNameRequest</code> request in the model
	 * @param res List of <code>Person</code> to be set in the model
	 */
	public void setResults(List<Person> res);
	
	/**
	 * Initiates a request to server with a specific name
	 * @param name The name (or sciper) of the person you are looking for
	 */
	public void search(String name);
	
	/**
	 * Initiates a resquest to get the url of the picture for a specific Person
	 * @param sciper EPFL id for people
	 */
	public void getProfilePicture(String sciper);
	
	/**
	 * Initiates a request to autocomplete the name.
	 * @param txt Name to autocomplete
	 */
	public void getAutoCompleted(String txt);
}
