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
import org.pocketcampus.shared.plugin.transport.Location;
import org.pocketcampus.shared.plugin.transport.QueryConnectionsResult;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 
 * @author Pascal
 * @author Florian
 *
 */
public class TransportPlugin extends PluginBase{
	private static final String REFERENCE_DESTINATION = "Ecublens VD, EPFL";
	

	private static RequestHandler requestHandler_;
	private TransportDisplayHander transportDisplayHandler_;
	
	
	private SharedPreferences commonDestPrefs_;
	private Map<String, String> commonDestinations_;
	
	
	public TransportPlugin() {
		requestHandler_ = getRequestHandler();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transport_main);

//		actionBar_ = (ActionBar) findViewById(R.id.actionbar);
//		setupActionBar(true);
//
//		mainList_ = (ListView) findViewById(R.id.transport_mainlist);
//		adapter_ = new TransportSummaryListAdapter(this, getRequestHandler(), actionBar_);
//		mainList_.setAdapter(adapter_);
//		
//		autoCompleteGoFrom_ = (AutoCompleteTextView)findViewById(R.id.transport_autoCompleteFrom);
//		ArrayAdapter<Location> adapterFrom = new LocationAdapter(this, android.R.layout.simple_dropdown_item_1line, autoCompleteGoFrom_, requestHandler_);
//		autoCompleteGoFrom_.setAdapter(adapterFrom);
//		
//		switcharoo_ = (Button)findViewById(R.id.transport_switchDirection);
//		switcharoo_.setOnClickListener(this);
//		
//		autoCompleteGoTo_ = (AutoCompleteTextView)findViewById(R.id.transport_autoCompleteTo);
//		ArrayAdapter<Location> adapterTo = new LocationAdapter(this, android.R.layout.simple_dropdown_item_1line, autoCompleteGoTo_, requestHandler_);
//		autoCompleteGoTo_.setAdapter(adapterTo);
//		//autoCompleteGoTo_.setCompletionHint("where you wanna go"); //TODO remove this
//		//autoCompleteGoTo_.setThreshold(3);
//		
//		go_ = (Button) findViewById(R.id.transport_go);
//		go_.setOnClickListener(this);
		
		transportDisplayHandler_ = new TransportDisplayHander(this, requestHandler_);
		
		commonDestPrefs_ = getSharedPreferences("CommonDestPrefs", 0);
		boolean vis = (commonDestPrefs_.getAll().size() > 0);
		System.out.println("visi: " + vis);
		transportDisplayHandler_.setupSummaryList((Map<String, String>) commonDestPrefs_.getAll(), vis);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		@SuppressWarnings("unchecked")
		Map<String, String> commonDestinationsInPrefs = (Map<String, String>) commonDestPrefs_.getAll();
		
		if(commonDestinationsInPrefs.equals(commonDestinations_)) {
			return;
		}else{
			commonDestinations_ = commonDestinationsInPrefs;
			boolean vis = (commonDestinations_.size() > 0);
			System.out.println("visi: " + vis);
			transportDisplayHandler_.setupSummaryList(commonDestinations_, vis);
		}
	}

	

	@Override
	protected void setupActionBar(boolean addHomeButton) {
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

	protected static String getReferenceDestination() {
		return REFERENCE_DESTINATION;
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
