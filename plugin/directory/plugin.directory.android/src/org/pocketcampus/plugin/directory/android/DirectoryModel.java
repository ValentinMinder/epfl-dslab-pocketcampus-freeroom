package org.pocketcampus.plugin.directory.android;

import java.util.List;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.directory.android.iface.IDirectoryModel;
import org.pocketcampus.plugin.directory.android.iface.IDirectoryView;
import org.pocketcampus.plugin.directory.shared.Person;

public class DirectoryModel extends PluginModel implements IDirectoryModel{
	IDirectoryView mListeners = (IDirectoryView) getListeners();
	
	private List<Person> mResult;
	private Person mSelectedPerson;
	//TODO use this variable to come back at the same index in the list if the selected person isn't the good one
	//private int indexOfList;
	
	@Override
	protected Class<? extends IView> getViewInterface() {
		return IDirectoryView.class;
	}

	@Override
	public List<Person> getResults() {
		return mResult;
	}
	
	public void setResults(List<Person> results){
		mResult = results;
		System.out.println("result set via the model");
		mListeners.resultsUpdated();
		
	}

	@Override
	public Person getSelectedPerson() {
		return mSelectedPerson;
	}
	
	public void selectPerson(Person choosen_one){
		mSelectedPerson = choosen_one;
		mListeners.personChoosed();
	}



}
