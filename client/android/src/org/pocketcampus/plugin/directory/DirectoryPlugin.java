package org.pocketcampus.plugin.directory;

import java.util.ArrayList;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;

import com.unboundid.ldap.sdk.LDAPException;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class DirectoryPlugin extends PluginBase implements OnClickListener{

	Button search_button;
	EditText first_name;
	EditText last_name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.directory_search);
		setupActionBar(true);

		search_button = (Button)findViewById(R.id.directory_search_button);
		search_button.setOnClickListener(this);
		first_name = (EditText)findViewById(R.id.directory_first_name_input);
		last_name = (EditText)findViewById(R.id.directory_last_name_input);

	}

	@Override
	public void onClick(View v) {
		if( v.getId() == R.id.directory_search_button){
			String last = last_name.getText().toString();
			String first = first_name.getText().toString();

			ArrayList<Person> result = null;


			try {
				result = DirectoryQuery.search(first, last);

				displayResultList(result);
			} catch (LDAPException e) {
				Toast.makeText(getApplicationContext(), e.getMessage(),Toast.LENGTH_SHORT).show();
			}


		}
	}

	public void displayResultList(ArrayList<Person> list){
		if(list == null)
			return;

		setContentView(R.layout.directory_result);
		ListView l = (ListView) findViewById(R.id.directory_result_list);
		l.setAdapter(new ArrayAdapter<Person>(this, R.layout.directory_peopleentry, list));
		l.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {

				Person found = ((Person)(parent.getAdapter().getItem(position)));
				Toast.makeText(getApplicationContext(), found.phone_number,Toast.LENGTH_SHORT).show();
				displayResult(found);

			}
		});
	}

	public void displayResult(Person person){
		setContentView(R.layout.directory_person_detail);
		TextView t = (TextView) findViewById(R.id.directory_Person_Detail);
		t.setText(person.fullInfoToString());

	}

	@Override
	public PluginInfo getPluginInfo() {
		return new DirectoryInfo();
	}

	@Override
	public PluginPreference getPluginPreference() {

		return null; //TODO new DirectoryPreference();
	}







}
