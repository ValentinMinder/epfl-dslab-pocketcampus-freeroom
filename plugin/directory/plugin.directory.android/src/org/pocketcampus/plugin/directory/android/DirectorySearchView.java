package org.pocketcampus.plugin.directory.android;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.element.OnKeyPressedListener;
import org.pocketcampus.plugin.directory.android.iface.IDirectoryModel;
import org.pocketcampus.plugin.directory.android.iface.IDirectoryView;
import org.pocketcampus.plugin.directory.android.ui.DirectorySearchLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class DirectorySearchView extends PluginView implements IDirectoryView{

	private DirectoryController mController;
	private IDirectoryModel mModel;
	
	private DirectorySearchLayout mLayout;
	
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
		
		mLayout = new DirectorySearchLayout(this, mController);
		
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				search();
			}
		};
		
		
		
		// The ActionBar is added automatically when you call setContentView
		setContentView(mLayout);

		
		Button searchButton = (Button) findViewById(R.id.directory_search_button);
		searchButton.setOnClickListener(listener);
		
		OnEditorActionListener oeal = new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
		            search();
		            return true;
		        }
		        return false;
			}
		};
		mLayout.setOnEditorActionListener(oeal);
		
		// We need to force the display before asking the controller for the data, 
		// as the controller may take some time to get it.
		//displayData();
	}
	
	@Override
	public void networkErrorHappened() {
		Toast toast = Toast.makeText(getApplicationContext(), "Network error!", Toast.LENGTH_SHORT);
		toast.show();
	}

	@Override
	public void resultsUpdated() {
		startActivity(new Intent(this, DirectoryResultListView.class));
		
	}

	@Override
	public void tooManyResults(int nb) {
		Toast.makeText(this, "Too many results, try to be more specific", Toast.LENGTH_LONG).show();
		
	}
	
	@Override
	public void pictureUpdated() {
		//should not happen here
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_SEARCH){
			search();

//			String name = "ironman";
//			Toast.makeText(this, "looking for Ironman", Toast.LENGTH_SHORT).show();
//			mController.search(name);
		}
			
		return super.onKeyDown(keyCode, event);
	}
	
	private void search(){
		if(mLayout != null){
			String name = mLayout.getName();
			mController.search(name);
		}
	}

	
}
