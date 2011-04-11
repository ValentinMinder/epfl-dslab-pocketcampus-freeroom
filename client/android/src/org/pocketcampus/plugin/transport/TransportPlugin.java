package org.pocketcampus.plugin.transport;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.core.communication.ServerRequest;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.core.ui.ActionBar;
import org.pocketcampus.shared.plugin.transport.Location;
import org.pocketcampus.shared.plugin.transport.QueryConnectionsResult;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class TransportPlugin extends PluginBase {
	private String defaultDestination = "Lausanne, Flon";
	private ListView mainList_;
	private TransportSummaryAdapter adapter_;
	private ActionBar actionBar_;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transport_main);
		
		actionBar_ = (ActionBar) findViewById(R.id.actionbar);
		setupActionBar(true);
		
		
		mainList_ = (ListView) findViewById(R.id.transport_mainlist);
		
		adapter_ = new TransportSummaryAdapter(this);
		mainList_.setAdapter(adapter_);
		
		getTransportSummaries();
		
		showToast("From EPFL to Flon");
	}
	
	private void getTransportSummaries() {
		class ConnectionsRequest extends ServerRequest {

			@Override
			protected void onPostExecute(String result) {
				System.out.println(result);
				
				Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss Z").create();
				
				Type SummaryListType = new TypeToken<QueryConnectionsResult>(){}.getType();
				QueryConnectionsResult summaryList = gson.fromJson(result, SummaryListType);
				
				System.out.println(summaryList);
				
				adapter_.setTransportSummaries(summaryList);
				hideSipnner();
			}
			
		} 
		
		RequestParameters params = new RequestParameters();
		params.addParameter("from", "Ecublens VD, EPFL");
		params.addParameter("to", defaultDestination);
		
		getRequestHandler().execute(new ConnectionsRequest(), "connections", params);
		showSpinner();
	}
	
	private void showSpinner() {
		actionBar_.setProgressBarVisibility(View.VISIBLE);
	}
	
	private void hideSipnner() {
		actionBar_.setProgressBarVisibility(View.GONE);
	}
	
	private void showToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public PluginInfo getPluginInfo() {
		return new TransportInfo();
	}

	@Override
	public PluginPreference getPluginPreference() {
		return null;
	}
}
