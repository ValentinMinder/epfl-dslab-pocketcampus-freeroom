package org.pocketcampus.plugin.directory.android;

import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.adapter.LabeledArrayAdapter;
import org.pocketcampus.android.platform.sdk.ui.labeler.ILabeler;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.android.platform.sdk.ui.list.LabeledListViewElement;
import org.pocketcampus.plugin.directory.android.iface.IDirectoryModel;
import org.pocketcampus.plugin.directory.android.iface.IDirectoryView;
import org.pocketcampus.plugin.directory.android.ui.PersonDetailsDialog;
import org.pocketcampus.plugin.directory.shared.Person;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;


/**
 * The View that contains the result of the search view if there is more than one result.
 * @author Pascal <pascal.scheiben@gmail.com>
 *
 */
public class DirectoryResultListView extends PluginView implements IDirectoryView{

	/** Controller of this plugin*/
	private DirectoryController mController;
	/** Model of this plugin */
	private IDirectoryModel mModel;
	
	/** List that will contain only the first and last name of the results*/ 
	private LabeledListViewElement mList;
	/**Designed to contain the results of the search, containing <code>Person</code> with full info*/
	private List<Person> mPersons;
	/** Global layout for this view*/
	private StandardTitledLayout mMainLayout;
	
	/** Dialog to show the details of a specific person*/
	private PersonDetailsDialog dialog;
	/** The index in <code>mPersons</code> of the person who is displayed in the <code>dialog</code>*/
	private int shownPersonIndex;
	
	/**
	 * Defines what the main controller is for this view. This is optional, some view may not need
	 * a controller (see for example the dashboard).
	 * 
	 * This is only a shortcut for what is done in <code>getOtherController()</code> below: if you know you'll
	 * need a controller before doing anything else in this view, you can define it as you're main controller so you
	 * know it'll be ready as soon as <code>onDisplay()</code> is called.
	 */
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return DirectoryController.class;
	}
	
	
	/**
	 * Called once the view is connected to the controller.
	 * If you don't implement <code>getMainControllerClass()</code> 
	 * then the controller given here will simply be <code>null</code>.
	 */
	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		//Tracker
		Tracker.getInstance().trackPageView("directory/ResultView");
		
		// Get and cast the controller and model
		mController = (DirectoryController) controller;
		mModel = (DirectoryModel) controller.getModel();
		
		mMainLayout = new StandardTitledLayout(this); 
		setContentView(mMainLayout);
		
		mMainLayout.setTitle(getString(R.string.directory_results_listView_title));
		
		resultsUpdated();
		
	}

	/**
	 * We could also have gotten the controller this way.
	 * With this we can connect the view to multiple controllers at once.
	 * 
	 * IMPORTANT for each controller to connect to, we must be able to handle the messages
	 * we'll get back from its model, ie we'll need to implement a specific interface.
	 * The program may crash if you don't, as it will call non-existing method of the view.
	 */
	@SuppressWarnings("unused")
	private void getOtherController() {
		ViewBoundCallback callback = new ViewBoundCallback() {
			@Override
			public void onViewBound(PluginController controller) {
				mController = (DirectoryController) controller;
				mModel = (DirectoryModel) mController.getModel();
				releaseController(mController);
			}
		};

		getController(DirectoryController.class, callback);
	}
	


	/**
	 * Called if there was a network problem while fetching the url of the picture
	 */
	@Override
	public void networkErrorHappened() {
		//Tracker
		Tracker.getInstance().trackPageView("directory/ResultView/network_error");
		
		Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.directory_network_error), Toast.LENGTH_SHORT);
		toast.show();
	}

	/**
	 * Called by the model when the results of the search request are back from the server.
	 * Display them, if the list of results isn't empty. else display an error message.
	 */
	@Override
	public void resultsUpdated() {
		mPersons = mModel.getResults();
		
		if(mList == null){
			mList = new LabeledListViewElement(this, mPersons, labeler);
			mList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> adapter, View arg1, int pos, long arg3) {
					Person p = (Person) adapter.getItemAtPosition(pos);
					mModel.selectPerson(p);
					shownPersonIndex = pos;
					mController.getProfilePicture(p.sciper);
					Log.v("Directory", p.toString());
					showPersonsDetails();
				}
			});
			
			mMainLayout.addFillerView(mList);
		}else{
			mList.setAdapter(new LabeledArrayAdapter(this, mPersons, labeler));
		}
		
		//no results, display it on a centered textview
		if (mList.getAdapter().getCount() == 0)
			mMainLayout.setText(getString(R.string.directory_no_results_found));
		else{
			
			//emptying the centered textview
			mMainLayout.setText("");
			int count = mList.getAdapter().getCount() ;
		
			if(count > 1)
				Toast.makeText(this,count +" "+ getString(R.string.directory_results_found), Toast.LENGTH_LONG).show();
			else{
				Toast.makeText(this,count +" "+ getString(R.string.directory_result_found), Toast.LENGTH_LONG).show();
				Person p = (Person) mList.getAdapter().getItem(0);
				mModel.selectPerson(p);
				shownPersonIndex = 0;
				mController.getProfilePicture(p.sciper);
				Log.v("Directory", p.toString());
				showPersonsDetails();
			}
		}
		
	}

	/**
	 * Shows the details of the person selected in the model.
	 */
	protected void showPersonsDetails() {
		//Tracker
		Tracker.getInstance().trackPageView("directory/ResultView/person/" + mModel.getSelectedPerson().sciper);
	
		dialog = new PersonDetailsDialog(this, mModel.getSelectedPerson());
		//TODO make fade in if necessary		
		//http://stackoverflow.com/questions/4817014/animate-a-custom-dialog/5591827#5591827
		dialog.show();
	}
	
	
	private ILabeler<Person> labeler = new ILabeler<Person>(){
		@Override
		public String getLabel(Person obj) {
			String nice;
			nice = obj.getFirstName() + " " + obj.getLastName(); 
			return nice;
		}
	};

	/**
	 * Inherited method not used here
	 */
	@Override
	public void tooManyResults(int nb) {
		System.out.println(getString(R.string.directory_too_many_results_warning));
		
	}


	/**
	 * Called by the model when the url of the picture has been updated
	 */
	@Override
	public void pictureUpdated() {
		dialog.loadPicture();
		
	}
	
	/**
	 * Shows the next person in the list if a dialog is already open
	 */
	public void showNextPerson(){
		
		//SECURE THIS
		shownPersonIndex++;
		if(shownPersonIndex> mList.getCount()-1)
			shownPersonIndex--;
		else{
			//todo try to fade out
			dialog.dismiss();
			
			Person p = (Person) mList.getItemAtPosition(shownPersonIndex);
			
			mModel.selectPerson(p);
			mController.getProfilePicture(p.sciper);
			showPersonsDetails();
		}
		
	}
	
	/**
	 * Shows the previous person in the list if a dialog is already open
	 */
	public void showPreviousPerson(){
		
		//SECURE THIS
		shownPersonIndex--;
		if(shownPersonIndex<0)
			shownPersonIndex=0;
		else{
			//todo try to fade out
			dialog.dismiss();
			
			Person p = (Person) mList.getItemAtPosition(shownPersonIndex);
			mModel.selectPerson(p);
			mController.getProfilePicture(p.sciper);
			showPersonsDetails();
		}
	}

	/**
	 * Inherited method not used here
	 */
	@Override
	public void autoCompletedUpdated() {
		// should not happen here
		
	}
	
}
