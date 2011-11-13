package org.pocketcampus.plugin.directory.android;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardLayout;
import org.pocketcampus.android.platform.sdk.ui.list.CheckBoxesListViewElement;
import org.pocketcampus.plugin.directory.android.iface.IDirectoryModel;
import org.pocketcampus.plugin.directory.android.iface.IDirectoryView;

import android.app.Service;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class DirectoryFilterOUView extends PluginView implements IDirectoryView {
	private DirectoryController mController;
	private IDirectoryModel mModel;

	private StandardLayout mLayout;
	private CheckBoxesListViewElement mListView;

	private List<String> mOUList;

	@Override
	protected Class<? extends Service> getMainControllerClass() {
		return DirectoryController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState,	PluginController controller){
		mController = (DirectoryController)controller;
		mModel = (DirectoryModel)controller.getModel();

		// The StandardLayout is a RelativeLayout with a TextView in its center.
		mLayout = new StandardLayout(this);

		//Get the tags from the controller
		mOUList = mController.getOUTags();
		Collections.sort(mOUList);

		//Add them to the listView
		mListView = new CheckBoxesListViewElement(this, mOUList);

		//Set onClickListeners
		setOnListViewClickListener();

		//Set the layout
		mLayout.addView(mListView);

		// The ActionBar is added automatically when you call setContentView
		setContentView(mLayout);
	}

	@Override
	public void networkErrorHappened() {
		Toast.makeText(getApplicationContext(), "Network error!", Toast.LENGTH_SHORT).show();
	}

	private void setOnListViewClickListener() {

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				//arg2 = position;
				Toast.makeText(getApplicationContext(), mOUList.get(arg2).toString(), 
						Toast.LENGTH_SHORT).show();
				mController.addOuToKeep( mOUList.get(arg2));
				
			}
			
		});

	}

	@Override
	public void resultsUpdated() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tooManyResults(int nb) {
		// not used
		
	}

	
}
