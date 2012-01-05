package org.pocketcampus.plugin.directory.android;

import java.util.ArrayList;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.element.InputBarElement;
import org.pocketcampus.android.platform.sdk.ui.element.OnKeyPressedListener;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.android.platform.sdk.ui.list.LabeledListViewElement;
import org.pocketcampus.plugin.directory.android.iface.IDirectoryModel;
import org.pocketcampus.plugin.directory.android.iface.IDirectoryView;
import org.pocketcampus.plugin.directory.android.ui.PersonDetailsDialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class DirectorySearchView extends PluginView implements IDirectoryView{

	private DirectoryController mController;
	private IDirectoryModel mModel;
	
	private StandardTitledLayout mLayout;
	private InputBarElement mInputBar;
	private LabeledListViewElement mListView;
	ArrayAdapter<String> mAdapter;
	
	PersonDetailsDialog mDialog;
	
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
		Tracker.getInstance().trackPageView("directory/SearchView");
		
		// Get and cast the controller and model
		mController = (DirectoryController) controller;
		mModel = (DirectoryModel) controller.getModel();
		
		displayView();
		createSuggestionsList();
		
	}
	
	private void displayView() {
		/* Layout */
		mLayout = new StandardTitledLayout(this);
		mLayout.setTitle(getString(R.string.directory_searchView_title));

		/* Input bar */
		mInputBar = new InputBarElement(this, null,getString(R.string.directory_searchView_hint));
		mInputBar.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
		mInputBar.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_SEARCH){
					String query = mInputBar.getInputText();
					search(query);
				}
				
				return true;
			}
		});
		
		mInputBar.setOnButtonClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String query = mInputBar.getInputText();
				search(query);
			}
		});

		mInputBar.setOnKeyPressedListener(new OnKeyPressedListener() {
			@Override
			public void onKeyPressed(String text) {
				
				
				if(mInputBar.getInputText().length() == 0){
					mInputBar.setButtonText(null);
					mAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.sdk_list_entry, R.id.sdk_list_entry_text, new ArrayList<String>());

					mListView.setAdapter(mAdapter);
					mListView.invalidate();
					
				}else{
					mInputBar.setButtonText("");
					mController.getAutoCompleted(text);
				}
			}
		});

		mLayout.addFillerView(mInputBar);
		setContentView(mLayout);
		
	
	}
	
	private void createSuggestionsList() {
		mListView = new LabeledListViewElement(this);
		mInputBar.addView(mListView);

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
				String query = adapter.getItemAtPosition(pos).toString();
				if(query.contains(" "))
					search(query);
				else{
					mInputBar.setInputText(query + " ");
					mInputBar.setCursorAtEnd();
				}
			}
		});
		
	}
	
	@Override
	public void autoCompletedUpdated() {
		mAdapter = new ArrayAdapter<String>(this, R.layout.sdk_list_entry, R.id.sdk_list_entry_text, mModel.getAutocompleteSuggestions());

		mListView.setAdapter(mAdapter);
		mListView.invalidate();
		
		mLayout.hideText();
		
	}
	
	@Override
	public void networkErrorHappened() {
		//Tracker
		Tracker.getInstance().trackPageView("directory/searchView/network_error");
		
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mInputBar.getWindowToken(), 0);
		
		mLayout.setText(getString(R.string.directory_network_error));
//		Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.directory_network_error), Toast.LENGTH_SHORT);
//		toast.show();
	}

	@Override
	public void resultsUpdated() {
		mLayout.hideText();
		
		if(mModel.getResults().size() == 1){
			mModel.selectPerson(mModel.getResults().get(0));
			mController.getProfilePicture(mModel.getResults().get(0).sciper);
			mDialog = new PersonDetailsDialog(this, mModel.getSelectedPerson());
			mDialog.show();
		}else
			startActivity(new Intent(getApplicationContext(), DirectoryResultListView.class));
		
	}

	@Override
	public void tooManyResults(int nb) {
		mLayout.setText( getString(R.string.directory_too_many_results_warning) );
		
	}
	
	@Override
	public void pictureUpdated() {
		if(mDialog != null)
			mDialog.loadPicture();
		
	}
	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if(keyCode == KeyEvent.KEYCODE_SEARCH && mInputBar.getInputText().length()> 0){
//			String query = mInputBar.getInputText();
//			search(query);
//		}
//			
//		return super.onKeyDown(keyCode, event);
//	}
	
	private void search(String query){
		//Tracker
		Tracker.getInstance().trackPageView("directory/SearchView/search/" + query);
		
		mController.search(query);
	}

	
}
