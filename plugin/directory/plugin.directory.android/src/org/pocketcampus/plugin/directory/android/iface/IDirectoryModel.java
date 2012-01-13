package org.pocketcampus.plugin.directory.android.iface;

import java.util.List;

import org.pocketcampus.plugin.directory.shared.Person;

/**
 *  Interface for the getters of the model of the Directory plugin
 * @author Pascal <pascal.scheiben@gmail.com>
 *
 */
public interface IDirectoryModel {
	/**
	 * Get the results contained in the model
	 * @return a list of <code>Person</code>
	 */
	public List<Person> getResults();
	
	/**
	 * Set the result in the model
	 * @param results List of <code>Person</code> to be stored.
	 */
	public void setResults(List<Person> results);
	
	/**
	 * Get the currently selected <code>Person</code>
	 * @return the currently selected <code>Person</code>
	 */
	public Person getSelectedPerson();
	
	/**
	 * Set the <code>Person</code> that is currently selected
	 * @param personToDisplay <code>Person</code> to select
	 */
	public void selectPerson(Person personToDisplay);
	
	/**
	 * Get the suggestions for the autocomplete
	 * @return List of suggested names.
	 */
	public List<String> getAutocompleteSuggestions();
	
	/**
	 * Set the suggestions for the autocomplete
	 * @param suggestions List of suggested names
	 */
	public void setAutocompleteSuggestions(List<String> suggestions);
}
