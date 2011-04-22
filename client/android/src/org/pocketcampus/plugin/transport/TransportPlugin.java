package org.pocketcampus.plugin.transport;

import java.lang.reflect.Type;
import java.util.Map;

import org.pocketcampus.R;
import org.pocketcampus.core.communication.DataRequest;
import org.pocketcampus.core.communication.RequestHandler;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.core.ui.ActionBar;
import org.pocketcampus.core.ui.ActionBar.Action;
import org.pocketcampus.shared.plugin.directory.Person;
import org.pocketcampus.shared.plugin.transport.Location;
import org.pocketcampus.shared.plugin.transport.QueryConnectionsResult;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class TransportPlugin extends PluginBase implements OnClickListener{
	private static final String REFERENCE_DESTINATION = "Ecublens VD, EPFL";
	private static RequestHandler requestHandler_;
	private ActionBar actionBar_;
	private ListView mainList_;
	private AutoCompleteTextView autoCompleteGoTo_;
	private AutoCompleteTextView autoCompleteGoFrom_;
	private Button switcharoo_;
	private Button go_;
	
	
	private TransportSummaryListAdapter adapter_;

	private SharedPreferences commonDestPrefs_;
	private Map<String, String> commonDestinations_;
	
	
	public TransportPlugin() {
		requestHandler_ = getRequestHandler();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transport_main);

		actionBar_ = (ActionBar) findViewById(R.id.actionbar);
		setupActionBar(true);

		mainList_ = (ListView) findViewById(R.id.transport_mainlist);
		adapter_ = new TransportSummaryListAdapter(this, getRequestHandler(), actionBar_);
		mainList_.setAdapter(adapter_);
		
		autoCompleteGoFrom_ = (AutoCompleteTextView)findViewById(R.id.transport_autoCompleteFrom);
		ArrayAdapter<Location> adapterFrom = new LocationAdapter(this, android.R.layout.simple_dropdown_item_1line, autoCompleteGoFrom_, requestHandler_);
		autoCompleteGoFrom_.setAdapter(adapterFrom);
		
		switcharoo_ = (Button)findViewById(R.id.transport_switchDirection);
		switcharoo_.setOnClickListener(this);
		
		autoCompleteGoTo_ = (AutoCompleteTextView)findViewById(R.id.transport_autoCompleteTo);
		ArrayAdapter<Location> adapterTo = new LocationAdapter(this, android.R.layout.simple_dropdown_item_1line, autoCompleteGoTo_, requestHandler_);
		autoCompleteGoTo_.setAdapter(adapterTo);
		autoCompleteGoTo_.setCompletionHint("where you wanna go"); //TODO remove this
		//autoCompleteGoTo_.setThreshold(3);
		
		go_ = (Button) findViewById(R.id.transport_go);
		go_.setOnClickListener(this);
		
		
		
		commonDestPrefs_ = getSharedPreferences("CommonDestPrefs", 0);
		setupSummaryList();
	}

	@Override
	protected void onResume() {
		super.onResume();
		setupSummaryList();
	}

	private void setupSummaryList() {
		Map<String, String> commonDestinationsInPrefs = (Map<String, String>) commonDestPrefs_.getAll();
		
		if(commonDestinationsInPrefs.equals(commonDestinations_)) {
			return;
		}
		
		final ListView listView = (ListView) findViewById(R.id.transport_mainlist);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {

			System.out.println("*****************" );
				
			afficheUneJoliPetiteFenetreAvecLesDetailsDuTrajet();
			}

			
		});
		
		TextView msgEmpty = (TextView) findViewById(R.id.msg_empty);
		
		if(commonDestinationsInPrefs.size() == 0) {
			listView.setVisibility(View.GONE);
			msgEmpty.setVisibility(View.VISIBLE);
			
		} else {
			listView.setVisibility(View.VISIBLE);
			msgEmpty.setVisibility(View.GONE);
		}
		
		commonDestinations_ = commonDestinationsInPrefs;
		adapter_.clearSections();
		
		for(String destination : commonDestinations_.values()) {
			TransportSummaryAdapter adapter = new TransportSummaryAdapter(this, REFERENCE_DESTINATION, destination);
			adapter_.addSection(adapter);
		}

		adapter_.loadSummaryList();
	}

	@Override
	protected void setupActionBar(boolean addHomeButton) {

		actionBar_ = (ActionBar) findViewById(R.id.actionbar);
		actionBar_.addAction(new Action() {

			@Override
			public void performAction(View view) {
				adapter_.loadSummaryList();
			}

			@Override
			public int getDrawable() {
				return R.drawable.refresh;
			}
		});

		super.setupActionBar(addHomeButton);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.transport, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;

		switch (item.getItemId()) {
		case R.id.transport_menu_settings:
			intent = new Intent(this, TransportPreference.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			return true;

		case R.id.transport_menu_detailed:
			intent = new Intent(this, TransportDetailed.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public PluginInfo getPluginInfo() {
		return new TransportInfo();
	}

	@Override
	public PluginPreference getPluginPreference() {
		return new TransportPreference();
	}

	public static RequestHandler getTransportRequestHandler() {
		return requestHandler_;
	}

	@Override
	public void onClick(View v) {
		if( v.getId() == switcharoo_.getId() ){
			//canceling autocompletion
			autoCompleteGoFrom_.setAdapter((ArrayAdapter<String>)null);
			autoCompleteGoTo_.setAdapter((ArrayAdapter<String>)null);
			
			//switching values
			String tmp = autoCompleteGoFrom_.getText().toString();
			autoCompleteGoFrom_.setText( autoCompleteGoTo_.getText());
			autoCompleteGoTo_.setText(tmp);
			
			//re creating autocomletion
			ArrayAdapter<Location> adapterFrom = new LocationAdapter(this, android.R.layout.simple_dropdown_item_1line, autoCompleteGoFrom_, requestHandler_);
			autoCompleteGoFrom_.setAdapter(adapterFrom);
			ArrayAdapter<Location> adapterTo = new LocationAdapter(this, android.R.layout.simple_dropdown_item_1line, autoCompleteGoTo_, requestHandler_);
			autoCompleteGoTo_.setAdapter(adapterTo);
		
		
		}else if (v.getId() == go_.getId()){
			String to = autoCompleteGoTo_.getText().toString();
			String from = autoCompleteGoFrom_.getText().toString();
			
			requestHandler_.toString();
			
			class ConnectionsRequest extends DataRequest {
				
				@Override
				protected int expirationDelay() {
					// 5 minutes
					return 60 * 5;
				}
				
				@Override
				protected void doInUiThread(String result) {
					Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss Z").create();

					Type SummaryListType = new TypeToken<QueryConnectionsResult>(){}.getType();
					QueryConnectionsResult summary = gson.fromJson(result, SummaryListType);
					
					afficheUnSuperTrajetTropCoolDeLaMort(summary);

				}
				
				@Override
				protected void onCancelled() {
				}
				
			} 

			RequestParameters params = new RequestParameters();
			params.addParameter("from", from);
			params.addParameter("to", to);

			requestHandler_.execute(new ConnectionsRequest(), "connections", params);
			
			
			
		}
		
	}
	
	private void afficheUnSuperTrajetTropCoolDeLaMort(QueryConnectionsResult summary){
		System.out.println(summary.toString());
	}
	
	private void afficheUneJoliPetiteFenetreAvecLesDetailsDuTrajet() {
		// TODO Auto-generated method stub
		
	}
	
//	@Override
//	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
//		// TODO show detailed transport schedule
//		System.out.println(mainList_.getItemAtPosition(pos));
//		Toast.makeText(getApplicationContext(),mainList_.getItemAtPosition(pos) + " " + ((TextView) arg1).getText(),
//		          Toast.LENGTH_SHORT).show();
//	}
//
//	@Override
//	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
//			long arg3) {
//		// TODO send schedule via mail/sms to a poor person whitout android
//		return false;
//	}

	
}
