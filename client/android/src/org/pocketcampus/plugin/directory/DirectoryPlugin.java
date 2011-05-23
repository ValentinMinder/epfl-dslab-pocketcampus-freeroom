package org.pocketcampus.plugin.directory;

import java.lang.reflect.Type;
import java.util.LinkedList;

import org.pocketcampus.R;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.core.communication.DataRequest;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.core.ui.ActionBar;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.pocketcampus.shared.plugin.directory.Person;
import org.pocketcampus.utils.Notification;

public class DirectoryPlugin extends PluginBase{
	LinkedList<Person> resultsList_;
	PersonSearchDialog searchDial_;
	
	private int progressCount_ = 0;
	private ActionBar actionBar_;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//preparing the list for the result
		setContentView(R.layout.directory_result);
		setupActionBar(true);
		actionBar_ = (ActionBar) findViewById(R.id.actionbar);

		
		//creating search dialog
		searchDial_ = new PersonSearchDialog(this);
		searchDial_.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				search(searchDial_.getFirstName(), 
						searchDial_.getLastName(),
						searchDial_.getSciper(),
						searchDial_.isSearchAccurate());
				
				searchDial_.dismiss();
			}
		});
		
		//test if another activity called us with parameters else show the search dialog
//		String fname = (String)savedInstanceState.get("first_name");
//		String lname = (String)savedInstanceState.get("last_name");
//		String sciper = (String)savedInstanceState.get("sciper_name");
//		
//		if(fname != null || lname != null || sciper != null)
//			search(fname, lname, sciper);
//		else
		resultsList_ = new LinkedList<Person>();
		searchDial_.show();

	}


	private void search(String first_name, String last_name, String sciper, boolean accurateSearch) {
		if(first_name.equals("") && last_name.equals("") && sciper.equals("")) {
			return;
		}
		
		incrementProgressCounter();
		class DirectoryRequest extends DataRequest {
			
			
			@Override
			protected int expirationDelay() {
				// 24 hours
				return 24 * 60 * 60;
			}
			
			@Override
			protected int timeoutDelay() {
				return 5;
			}
			
			@Override
			protected void doInBackgroundThread(String result) {
				if(result != null) {
					Gson gson = new Gson();
					
					Type listType = new TypeToken<LinkedList<Person>>(){}.getType();
					resultsList_ = new LinkedList<Person>();
					
					try{
						resultsList_ = gson.fromJson(result, listType);
					} catch (JsonSyntaxException e) {
						resultsList_ = null;
						e.printStackTrace();
					}
				}
			}
			
			@Override
			protected void doInUiThread(String result) {
				displayResultList();
				decrementProgressCounter();
			}
			
			@Override
			protected void onCancelled() {
				resultsList_ = null;
				decrementProgressCounter();
			}	
			
		}
		
		// Create a RequestParameters object containing the parameters
		RequestParameters reqParams = new RequestParameters();
		if(! first_name.equals(""))
			reqParams.addParameter("firstName", first_name);
		if(! last_name.equals(""))
			reqParams.addParameter("lastName", last_name);
		if(! sciper.equals(""))
			reqParams.addParameter("sciper", sciper);
		
//		// mettre vos userame pour test
		if(accurateSearch) {
			getRequestHandler().execute(new DirectoryRequest(), "bla", reqParams);
		} else {
			getRequestHandler().execute(new DirectoryRequest(), "idrkhn", reqParams);
		}
	}
	
	public void displayResultList(){
		if(resultsList_ == null) {
			toast("An error occured.");
			return;
		}
		
		ListView resultListView = (ListView) findViewById(R.id.directory_result_list);
		TextView emptyListTextView = (TextView) findViewById(R.id.directory_emptylist);
		
		if(resultsList_.isEmpty()) {
			resultListView.setVisibility(View.GONE);
			emptyListTextView.setVisibility(View.VISIBLE);
			
		} else {
			resultListView.setVisibility(View.VISIBLE);
			emptyListTextView.setVisibility(View.GONE);
		}
		
		
		ArrayAdapter<Person> adapter = new ArrayAdapter<Person>(this, R.layout.directory_peopleentry, resultsList_);
		resultListView.setAdapter(adapter);
		
		adapter.notifyDataSetChanged();
		
		resultListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {

				Person found = ((Person)(parent.getAdapter().getItem(position)));
				displayResult(found);

			}
		});
	}

	public void displayResult(Person person){
		PersonDetailsDialog detailDialog = new PersonDetailsDialog(this, person);
		detailDialog.show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, Menu.NONE, "New Search").setIcon(android.R.drawable.ic_menu_search);
		menu.setGroupEnabled(0, true);

		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()){
			case 1: searchDial_.show();
					break;
				
			case 2:
					break;
		}
		return true;
	}
	
	
	
	// TODO reuse from map
	private synchronized void incrementProgressCounter() {
		progressCount_++;
		actionBar_.setProgressBarVisibility(View.VISIBLE);
	}
	
	// TODO reuse from map
	private synchronized void decrementProgressCounter() {
		progressCount_--;
		if(progressCount_ < 0) { //Should never happen!
			Log.e("MapPlugin", "ERROR progresscount is negative!");
		}
		
		if(progressCount_ <= 0) {
			actionBar_.setProgressBarVisibility(View.GONE);
		}
	}
	
	
	
	
	
	private void toast(String message){
		Notification.showToast(getApplicationContext(), message);
	}

	@Override
	public PluginInfo getPluginInfo() {
		return new DirectoryInfo();
	}

	@Override
	public PluginPreference getPluginPreference() {
		return null;//new DirectoryPreference();
	}



}
