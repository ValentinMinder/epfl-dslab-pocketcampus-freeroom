package org.pocketcampus.plugin.directory.android.iface;

import java.util.List;

import org.pocketcampus.plugin.directory.shared.Person;

/**
 * Interface for the getters of the model.
 *
 * The idea is to only have the methods to *access* the model in there, this way we can use it in the views
 * and we're sure we'll never accidentally modify the model from there.
 */
public interface IDirectoryModel {
	public List<Person> getResults();
	public Person getSelectedPerson();
	public void selectPerson(Person personToDisplay);
}
