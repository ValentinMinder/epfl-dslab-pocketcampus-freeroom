package org.pocketcampus.plugin.directory.android;

import java.util.List;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.directory.android.iface.IDirectoryModel;
import org.pocketcampus.plugin.directory.android.iface.IDirectoryView;
import org.pocketcampus.plugin.directory.shared.Person;

import android.widget.Toast;

public class DirectoryModel extends PluginModel implements IDirectoryModel{
	IDirectoryView mListeners = (IDirectoryView) getListeners();
	
	private List<Person> mResult;
	private Person mSelectedPerson;

	private List<String> organisationalUnitList;

	
	@Override
	protected Class<? extends IView> getViewInterface() {
		return IDirectoryView.class;
	}

	public void setOUList(List<String> organisationalUnitList) {
		this.organisationalUnitList = organisationalUnitList;
	}

	public List<String> getOUList() {
		return organisationalUnitList;
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
	}
	
	public void notifyTooManyResults(int nb){
		mListeners.tooManyResults(nb);
	}

	public void setProfilePicture(String result) {
		mSelectedPerson.pictureUrl = result;
		mListeners.pictureUpdated();
	}

	public void setAutoCompletedGN(List<String> result) {
		// TODO Auto-generated method stub
		
	}

	public void setAutoCompletedSN(List<String> result) {
		// TODO Auto-generated method stub
		
	}



}
