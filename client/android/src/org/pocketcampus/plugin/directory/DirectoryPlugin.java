package org.pocketcampus.plugin.directory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.core.communication.ServerRequest;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;

import android.os.Bundle;
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
import com.google.gson.reflect.TypeToken;

public class DirectoryPlugin extends PluginBase implements OnClickListener{

	Button search_button;
	EditText first_name;
	EditText last_name;
	EditText sciper;
	
	List<Person> resultsList;
	State state;
	
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
		

	}


	@Override
	public void onClick(View v) {
		if( v.getId() == R.id.directory_search_button){
			String last = last_name.getText().toString();
			String first = first_name.getText().toString();
			String sci = sciper.getText().toString();

			search(first, last, sci);
//			resultsList = new ArrayList<Person>();
//			for(String s : COUNTRIES)
//			{
//				resultsList.add(new Person(s));
//			}
	
			
			displayResultList();

		}
	}
	
	@Override
	public void onBackPressed(){
		Toast.makeText(getApplicationContext(), "tu peux pas back, test la petite maison en haut",Toast.LENGTH_SHORT).show();
		switch(state){
			case RESULT_DETAIL:
				displayResultList();				
				break;
				
			case RESULT_LIST:
				displaySearch();
				break;
				
			case SEARCH:
				super.onBackPressed();
				break;
		}
	}
	
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
	
	
	private void search(String first_name, String last_name, String sciper) {
		
		// Create a class for your request with...
		class DirectoryRequest extends ServerRequest {
			
			// ...what to do when the result is ready
			@SuppressWarnings("unchecked")
			@Override
			protected void onPostExecute(String result) {
				if(result != null) {
					resultsList = new ArrayList<Person>();
					
					Gson gson = new Gson();
					
					Type listType = new TypeToken<ArrayList<Person>>()  {}.getType();

					if(listType == null || result == null)
						Toast.makeText(getApplicationContext(), "a",Toast.LENGTH_SHORT).show();
					
					resultsList = (ArrayList<Person>)gson.fromJson(result, listType);
					
				} else {
					Toast.makeText(getApplicationContext(), "too fast?",Toast.LENGTH_SHORT).show();
				}
			}
		}
		
		// Create a RequestParameters object containing the parameters
		RequestParameters reqParams = new RequestParameters();
		reqParams.addParameter("firstName", first_name);
		reqParams.addParameter("lastName", last_name);
		reqParams.addParameter("sciper", sciper);
		
		//TODO add username and pwd via configuration
		reqParams.addParameter("username", "scheiben");
		reqParams.addParameter("password", "b"); //TODO mettre vos userame pour test
		
		
		
		
		// Use a RequestHandler to execute your request.
		// You don't have to worry about which Servlet your talking to, it will automatically be the
		// one corresponding to the plugin you're in.
		// If you need to do a request from another class/Activity you can give it a RequestHandler instance.
		getRequestHandler().execute(new DirectoryRequest(), reqParams);
		
		
		// To handle the loading another way, you can just use the RequestHandler to give you the complete
		// request URL, including the server and servlet address.
		//System.out.println(getRequestHandler().getRequestUrl(reqParams));
	}
	
	private void displaySearch() {
		setContentView(R.layout.directory_search);
		state = State.SEARCH;
		setupActionBar(true);
	}
	
	public void displayResultList(){
		if(resultsList == null)
			return;

		setContentView(R.layout.directory_result);
		state = State.RESULT_LIST;
		setupActionBar(true);
		
		ListView l = (ListView) findViewById(R.id.directory_result_list);
		l.setAdapter(new ArrayAdapter<Person>(this, R.layout.directory_peopleentry, resultsList));
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
		state = State.RESULT_DETAIL;
		setupActionBar(true);
		
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






	static final String[] COUNTRIES = new String[] {
	    "Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra",
	    "Angola", "Anguilla", "Antarctica", "Antigua and Barbuda", "Argentina",
	    "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan",
	    "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium",
	    "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia",
	    "Bosnia and Herzegovina", "Botswana", "Bouvet Island", "Brazil", "British Indian Ocean Territory",
	    "British Virgin Islands", "Brunei", "Bulgaria", "Burkina Faso", "Burundi",
	    "Cote d'Ivoire", "Cambodia", "Cameroon", "Canada", "Cape Verde",
	    "Cayman Islands", "Central African Republic", "Chad", "Chile", "China",
	    "Christmas Island", "Cocos (Keeling) Islands", "Colombia", "Comoros", "Congo",
	    "Cook Islands", "Costa Rica", "Croatia", "Cuba", "Cyprus", "Czech Republic",
	    "Democratic Republic of the Congo", "Denmark", "Djibouti", "Dominica", "Dominican Republic",
	    "East Timor", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea",
	    "Estonia", "Ethiopia", "Faeroe Islands", "Falkland Islands", "Fiji", "Finland",
	    "Former Yugoslav Republic of Macedonia", "France", "French Guiana", "French Polynesia",
	    "French Southern Territories", "Gabon", "Georgia", "Germany", "Ghana", "Gibraltar",
	    "Greece", "Greenland", "Grenada", "Guadeloupe", "Guam", "Guatemala", "Guinea", "Guinea-Bissau",
	    "Guyana", "Haiti", "Heard Island and McDonald Islands", "Honduras", "Hong Kong", "Hungary",
	    "Iceland", "India", "Indonesia", "Iran", "Iraq", "Ireland", "Israel", "Italy", "Jamaica",
	    "Japan", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Kuwait", "Kyrgyzstan", "Laos",
	    "Latvia", "Lebanon", "Lesotho", "Liberia", "Libya", "Liechtenstein", "Lithuania", "Luxembourg",
	    "Macau", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands",
	    "Martinique", "Mauritania", "Mauritius", "Mayotte", "Mexico", "Micronesia", "Moldova",
	    "Monaco", "Mongolia", "Montserrat", "Morocco", "Mozambique", "Myanmar", "Namibia",
	    "Nauru", "Nepal", "Netherlands", "Netherlands Antilles", "New Caledonia", "New Zealand",
	    "Nicaragua", "Niger", "Nigeria", "Niue", "Norfolk Island", "North Korea", "Northern Marianas",
	    "Norway", "Oman", "Pakistan", "Palau", "Panama", "Papua New Guinea", "Paraguay", "Peru",
	    "Philippines", "Pitcairn Islands", "Poland", "Portugal", "Puerto Rico", "Qatar",
	    "Reunion", "Romania", "Russia", "Rwanda", "Sqo Tome and Principe", "Saint Helena",
	    "Saint Kitts and Nevis", "Saint Lucia", "Saint Pierre and Miquelon",
	    "Saint Vincent and the Grenadines", "Samoa", "San Marino", "Saudi Arabia", "Senegal",
	    "Seychelles", "Sierra Leone", "Singapore", "Slovakia", "Slovenia", "Solomon Islands",
	    "Somalia", "South Africa", "South Georgia and the South Sandwich Islands", "South Korea",
	    "Spain", "Sri Lanka", "Sudan", "Suriname", "Svalbard and Jan Mayen", "Swaziland", "Sweden",
	    "Switzerland", "Syria", "Taiwan", "Tajikistan", "Tanzania", "Thailand", "The Bahamas",
	    "The Gambia", "Togo", "Tokelau", "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey",
	    "Turkmenistan", "Turks and Caicos Islands", "Tuvalu", "Virgin Islands", "Uganda",
	    "Ukraine", "United Arab Emirates", "United Kingdom",
	    "United States", "United States Minor Outlying Islands", "Uruguay", "Uzbekistan",
	    "Vanuatu", "Vatican City", "Venezuela", "Vietnam", "Wallis and Futuna", "Western Sahara",
	    "Yemen", "Yugoslavia", "Zambia", "Zimbabwe"
	  };
}
