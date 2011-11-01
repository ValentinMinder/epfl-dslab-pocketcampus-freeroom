package org.pocketcampus.plugin.directory.android;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.plugin.directory.android.iface.IDirectoryModel;
import org.pocketcampus.plugin.directory.android.iface.IDirectoryView;
import org.pocketcampus.plugin.directory.android.ui.DirectorySearchLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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
		
		mLayout = new DirectorySearchLayout(this);
		
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				String name = mLayout.getName();
				
				
				// There's also a setFoo method in the model. Don't use it from here!
				// The views should never modify the model directly.
				mController.search(name);
			}
		};
		
		// The ActionBar is added automatically when you call setContentView
		setContentView(mLayout);

		
		Button searchButton = (Button) findViewById(R.id.directory_search_button);
		searchButton.setOnClickListener(listener);
		
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
}
