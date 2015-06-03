package org.pocketcampus.plugin.directory.android;

import java.util.LinkedList;
import java.util.List;

import org.pocketcampus.platform.android.core.IView;
import org.pocketcampus.platform.android.core.PluginModel;
import org.pocketcampus.plugin.directory.android.iface.IDirectoryModel;
import org.pocketcampus.plugin.directory.android.iface.IDirectoryView;
import org.pocketcampus.plugin.directory.shared.Person;

/**
 * The model for the Directory plugin, contains all the data to display in the view.
 * 
 * @author Amer C <amer@pocketcampus.org>
 *
 */
public class DirectoryModel extends PluginModel implements IDirectoryModel{
	/**Listeners to this model */ 
	IDirectoryView mListeners = (IDirectoryView) getListeners();
	
	/**List of <code>Person</code>'s obtained from the ldap search**/
	private List<Person> mResult;

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
	public List<Person> getResults() {
		return mResult;
	}
	
	/**
	 * Setter for the results of a request
	 * @param results the result for the specific search
	 */
	public void setResults(List<Person> results){
		mResult = results;
		mListeners.resultListUpdated();
		
	}
	
	/**
	 * Clear result set
	 */
	public void clearResults() {
		setResults(new LinkedList<Person>());
	}

}
