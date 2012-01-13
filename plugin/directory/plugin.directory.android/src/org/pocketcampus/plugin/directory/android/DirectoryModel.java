package org.pocketcampus.plugin.directory.android;

import java.util.List;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.directory.android.iface.IDirectoryModel;
import org.pocketcampus.plugin.directory.android.iface.IDirectoryView;
import org.pocketcampus.plugin.directory.shared.Person;

import android.util.Log;

/**
 * The model for the Directory plugin, contains all the data to display in the view.
 * 
 * @author Pascal <pascal.scheiben@gmail.com>
 *
 */
public class DirectoryModel extends PluginModel implements IDirectoryModel{
	/**Listeners to this model */ 
	IDirectoryView mListeners = (IDirectoryView) getListeners();
	
	/**List of <code>Person</code>'s obtained from the ldap search**/
	private List<Person> mResult;
	/**List of suggestions for the search view*/
	private List<String> mAutoCompleteSuggestions;
	/**Used to specify the displayed <code>Person</code> in the results view*/
	private Person mSelectedPerson;

	/**
	 * Returns the interface of the linked view
	 */
	@Override
	protected Class<? extends IView> getViewInterface() {
		return IDirectoryView.class;
	}

	/**
	 * Getter for the results of the request
	 * @return List of person
	 */
	@Override
	public List<Person> getResults() {
		return mResult;
	}
	
	/**
	 * Setter for the results of a request
	 * @param results the result for the specific search
	 */
	public void setResults(List<Person> results){
		mResult = results;
		Log.v("Directory","result set via the model");
		mListeners.resultsUpdated();
		
	}

	/**
	 * Gets the currently selected <code>Person</code>
	 */
	public Person getSelectedPerson() {
		return mSelectedPerson;
	}
	
	/**
	 * Sets which <code>Person</code> is currentyl displayed.
	 */
	public void selectPerson(Person choosen_one){
		mSelectedPerson = choosen_one;
	}
	
	/**
	 * Called when the server refuses to return a list due to his intern limitation.
	 * @param nb number of maximum results you are allowed to have.
	 */
	public void notifyTooManyResults(int nb){
		mListeners.tooManyResults(nb);
	}

	/**
	 * Sets the url of a profile.
	 * @param result Url of the picture.
	 */
	public void setProfilePicture(String result) {
		mSelectedPerson.pictureUrl = result;
		mListeners.pictureUpdated();
	}

	/**
	 * Gets the suggestions for the autocomplete
	 */
	@Override
	public List<String> getAutocompleteSuggestions() {
		return mAutoCompleteSuggestions;
	}
	
	/**
	 * Sets the suggestions to the autocomplete and notifies the listeners.
	 * @param suggestions
	 */
	public void setAutocompleteSuggestions(List<String> suggestions){
		mAutoCompleteSuggestions = suggestions;
		mListeners.autoCompletedUpdated();
	}



}
