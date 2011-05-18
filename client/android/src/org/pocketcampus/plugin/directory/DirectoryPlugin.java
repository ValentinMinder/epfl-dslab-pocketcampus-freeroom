package org.pocketcampus.plugin.directory;

import java.lang.reflect.Type;
import java.util.LinkedList;

import org.pocketcampus.R;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.core.communication.DataRequest;
import org.pocketcampus.core.parser.Json;
import org.pocketcampus.core.parser.JsonException;
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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.pocketcampus.shared.plugin.directory.Person;
import org.pocketcampus.shared.plugin.transport.QueryConnectionsResult;
import org.pocketcampus.utils.Notification;

public class DirectoryPlugin extends PluginBase{

	
	
	LinkedList<Person> resultsList;
	PersonSearchDialog searchDial;
	
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
		searchDial = new PersonSearchDialog(this);
		searchDial.search_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				search(searchDial.first_name.getText().toString(), 
						searchDial.last_name.getText().toString(),
						searchDial.sciper.getText().toString(),
						searchDial.accurateSearch);
				
				searchDial.dismiss();
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
		resultsList = new LinkedList<Person>();
			searchDial.show();

	}


	private void search(String first_name, String last_name, String sciper, boolean accurateSearch) {
		
		// Create a class for your request with...
		incrementProgressCounter();
		class DirectoryRequest extends DataRequest {
			
			
			@Override
			protected int expirationDelay() {
				// 5 minutes
				return 100000;
			}
			
			@Override
			protected int timeoutDelay() {
				return 3;
			}
			
			@Override
			protected void doInBackgroundThread(String result) {
				if(result != null) {
					System.out.println(result);
					Gson gson = new Gson();
					
					Type listType = new TypeToken<LinkedList<Person>>(){}.getType();
					resultsList = new LinkedList<Person>();
					
					try{
						resultsList = gson.fromJson(result, listType);
					} catch (JsonSyntaxException e) {
						return;
					} catch(Exception e){
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
		
//		reqParams.addParameter("username", "scheiben");
//		reqParams.addParameter("password", "xxxxxxxx"); 
//		// mettre vos userame pour test
		if(accurateSearch)
			getRequestHandler().execute(new DirectoryRequest(), "bla", reqParams);
		else
			getRequestHandler().execute(new DirectoryRequest(), "idrkhn", reqParams);
	}
	
	public void displayResultList(){
		if(resultsList.isEmpty())
			toast("No one found");

		
		ListView l = (ListView) findViewById(R.id.directory_result_list);
		ArrayAdapter<Person> aadapter = new ArrayAdapter<Person>(this, R.layout.directory_peopleentry, resultsList);
		l.setAdapter(aadapter);
		aadapter.notifyDataSetChanged();
		
		l.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {

				Person found = ((Person)(parent.getAdapter().getItem(position)));
				displayResult(found);

			}
		});
	}

	public void displayResult(Person person){
		PersonDetailsDialog detailDialog = new PersonDetailsDialog(this, person);
		detailDialog.setCanceledOnTouchOutside(true);
		detailDialog.show();
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, Menu.NONE, "New Search").setIcon(R.drawable.directory_search);
		menu.setGroupEnabled(0, true);

		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()){
			case 1: searchDial.show();
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
