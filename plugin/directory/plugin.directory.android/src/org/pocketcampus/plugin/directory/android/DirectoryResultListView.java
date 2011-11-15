package org.pocketcampus.plugin.directory.android;

import java.util.HashSet;
import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.adapter.LabeledArrayAdapter;
import org.pocketcampus.android.platform.sdk.ui.labeler.ILabeler;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardLayout;
import org.pocketcampus.android.platform.sdk.ui.list.LabeledListViewElement;
import org.pocketcampus.plugin.directory.android.iface.IDirectoryModel;
import org.pocketcampus.plugin.directory.android.iface.IDirectoryView;
import org.pocketcampus.plugin.directory.android.ui.PersonDetailsDialog;
import org.pocketcampus.plugin.directory.shared.Person;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class DirectoryResultListView extends PluginView implements IDirectoryView{

	private DirectoryController mController;
	private IDirectoryModel mModel;
	
	private LabeledListViewElement mList;
	private List<Person> mPersons;
	private StandardLayout mMainLayout;
	
	private PersonDetailsDialog dialog;
	
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
		// Get and cast the controller and model
		mController = (DirectoryController) controller;
		mModel = (DirectoryModel) controller.getModel();
		
		mMainLayout = new StandardLayout(this); 
		setContentView(mMainLayout);
		
		mMainLayout.setText("Loading..");
		
		resultsUpdated();
		
	}


	
	private void displayData() {
		mPersons = mModel.getResults();
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
				System.out.println(mModel.getResults());
				releaseController(mController);
			}
		};

		getController(DirectoryController.class, callback);
	}
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuInflater inflater = getMenuInflater();
//	    inflater.inflate(R.menu.directory_resultlist_menu, menu);
//	    return true;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(android.view.MenuItem item) {
//		switch (item.getItemId()) {
//	    	case R.id.directory_resultList_filtre:
//	    		
//	    		mController.filterResults();
//	    		
//	    		
//	    		
//	    		// = getOuSelection(allOU);
//		    	
//				//Toast.makeText(this, "filtre! " + toast, Toast.LENGTH_LONG).show();
//				
//				//filterResult(ouToKeep);
//		        return true;
//		}
//		return true;
//	}

	private HashSet<String> getOuSelection() {
		HashSet<String> keeper = new HashSet<String>();
		
		
		
		
		return keeper;
	}





	@Override
	public void networkErrorHappened() {
		Toast toast = Toast.makeText(getApplicationContext(), "Network error!", Toast.LENGTH_SHORT);
		toast.show();
	}


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
					mController.getProfilePicture(p.sciper);
					System.out.println(p);
					showPersonsDetails();
				}
			});
			
			mMainLayout.addView(mList);
		}else{
			mList.setAdapter(new LabeledArrayAdapter(this, mPersons, labeler));
		}
		
		
		if (mList.getAdapter().getCount() == 0)
			mMainLayout.setText("No result found");
		else{
			mMainLayout.setText("");
			Toast.makeText(this, mList.getAdapter().getCount() + "result(s) found", Toast.LENGTH_LONG).show();
		}
		
	}


	protected void showPersonsDetails() {
	
		dialog = new PersonDetailsDialog(this, mModel.getSelectedPerson());
		dialog.show();
	}
	
	
	ILabeler<Person> labeler = new ILabeler<Person>(){
		@Override
		public String getLabel(Person obj) {
			String nice;
			nice = obj.getFirstName() + " " + obj.getLastName(); 
			return nice;
		}
	};

	@Override
	public void tooManyResults(int nb) {
		System.out.println("oh aye");
		
	}


	@Override
	public void pictureUpdated() {
		dialog.loadPicture();
		
	}
	
}
