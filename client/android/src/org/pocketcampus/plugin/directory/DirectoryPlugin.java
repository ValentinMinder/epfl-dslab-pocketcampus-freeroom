package org.pocketcampus.plugin.directory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.core.communication.DataRequest;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.pocketcampus.shared.plugin.directory.Person;

public class DirectoryPlugin extends PluginBase implements OnClickListener{

	Button search_button;
	EditText first_name;
	EditText last_name;
	EditText sciper;
	
	LinkedList<Person> resultsList;
	State state;
	private Person displayedPerson;
	
	private enum State {SEARCH, RESULT_LIST, RESULT_DETAIL};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		displaySearch();
		

		search_button = (Button)findViewById(R.id.directory_search_button);
		search_button.setOnClickListener(this);
		first_name = (EditText)findViewById(R.id.directory_first_name_input);
		last_name = (EditText)findViewById(R.id.directory_last_name_input);
		sciper = (EditText)findViewById(R.id.directory_sciper_input);
		
		resultsList = new LinkedList<Person>();
		

	}


	@Override
	public void onClick(View v) {
		if( v.getId() == R.id.directory_search_button){
			String last = last_name.getText().toString();
			String first = first_name.getText().toString();
			String sci = sciper.getText().toString();

			search(first, last, sci);
		}
		
//		switch(v.getId()){
//			case R.id.directory_Person_Detail:
//			case R.id.directory_phone_person:
//				performDial();
//				break;
//		
//			case R.id.directory_search_button:
//				String last = last_name.getText().toString();
//				String first = first_name.getText().toString();
//				String sci = sciper.getText().toString();
//
//				search(first, last, sci);
//				break;
//		}
	}
	
//	@Override
//	public void onBackPressed(){
//		switch(state){
//			case RESULT_DETAIL:
//				displayResultList();				
//				break;
//				
//			case RESULT_LIST:
//				displaySearch();
//				break;
//				
//			case SEARCH:
//				super.onBackPressed();
//				break;
//		}
//	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
//		LinearLayout l1 = (LinearLayout)findViewById(R.id.directory_ll1);
//		LinearLayout l2 = (LinearLayout)findViewById(R.id.directory_ll2);
		LinearLayout l3 = (LinearLayout)findViewById(R.id.directory_ll3);
		
		if(l3.getVisibility() == LinearLayout.INVISIBLE){
			l3.setVisibility(LinearLayout.VISIBLE);
		}else{
			l3.setVisibility(LinearLayout.INVISIBLE);
		}
		return super.onPrepareOptionsMenu(menu);
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	
		if (keyCode == KeyEvent.KEYCODE_CALL && state == State.RESULT_DETAIL) {
			Toast.makeText(getApplicationContext(), "riiiiing",Toast.LENGTH_SHORT).show();
			performDial();
			return true;
		}
	    return false;
	  }

	  public void performDial(){
	    if(displayedPerson != null){
	      try {
	        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + displayedPerson.phone_number)));
	      } catch (Exception e) {
	    	  System.out.println("pas ring");
	        //dial problem
	      }
	    }
	  }
	
	
	private void search(String first_name, String last_name, String sciper) {
		
		// Create a class for your request with...
		class DirectoryRequest extends DataRequest {
			
			// ...what to do when the result is ready
			@Override
			protected void doInUiThread(String result) {
				if(result != null) {
					System.out.println(result);
					Gson gson = new Gson();
					
					Type listType = new TypeToken<LinkedList<Person>>(){}.getType();
					resultsList = new LinkedList<Person>();
					
					try{
//						result = "[{\"first_name\":\"Pascal\",\"last_name\":\"Scheiben\",\"mail\":\"pascal.scheiben@epfl.ch\"}]";
						resultsList = gson.fromJson(result, listType);
						System.out.println("yeeeeeeeeeeeeeeees trop bien" + resultsList);
					} catch (JsonSyntaxException e) {
						System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaa");
						return;
					} catch(Exception e){
						System.out.println("FUUUUUUUUUUUUUUUUU");
						e.printStackTrace();
					}
					
					displayResultList();
					
				} else {
					Toast.makeText(getApplicationContext(), "too fast?",Toast.LENGTH_SHORT).show();
				}
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
		
		//TODO add username and pwd via configuration
		reqParams.addParameter("username", "scheiben");
		reqParams.addParameter("password", "pocketcampus"); 
		//TODO mettre vos userame pour test
		
		getRequestHandler().execute(new DirectoryRequest(), "bla", reqParams);
	}
	
	private void displaySearch() {
		setContentView(R.layout.directory_search);
		state = State.SEARCH;
		setupActionBar(true);
	}
	
	public void displayResultList(){
		if(resultsList.isEmpty())
			Toast.makeText(getApplicationContext(), "No one found",Toast.LENGTH_SHORT).show();

		setContentView(R.layout.directory_result);
		state = State.RESULT_LIST;
		setupActionBar(true);
		
		ListView l = (ListView) findViewById(R.id.directory_result_list);
		l.setAdapter(new ArrayAdapter<Person>(this, R.layout.directory_peopleentry, resultsList));
		l.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {

				Person found = ((Person)(parent.getAdapter().getItem(position)));
				displayResult(found);

			}
		});
	}

	public void displayResult(Person person){
		setContentView(R.layout.directory_person_detail);
		state = State.RESULT_DETAIL;
		displayedPerson = person;
		setupActionBar(true);
		
		TextView t = (TextView) findViewById(R.id.directory_Person_Detail);
		t.setText(person.fullInfoToString());
		
		Button call = (Button) findViewById(R.id.directory_phone_person);
		call.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				performDial();
				
			}
		});

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
