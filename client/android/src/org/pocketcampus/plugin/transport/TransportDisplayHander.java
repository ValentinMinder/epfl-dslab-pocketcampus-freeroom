package org.pocketcampus.plugin.transport;

import java.lang.reflect.Type;
import java.util.Map;

import org.pocketcampus.R;
import org.pocketcampus.core.communication.DataRequest;
import org.pocketcampus.core.communication.RequestHandler;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.core.ui.ActionBar;
import org.pocketcampus.core.ui.ActionBar.Action;
import org.pocketcampus.shared.plugin.transport.Connection;
import org.pocketcampus.shared.plugin.transport.Location;
import org.pocketcampus.shared.plugin.transport.QueryConnectionsResult;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TransportDisplayHander implements OnClickListener {
	private TransportPlugin ownerActivity_;
	private Context activityContext_;
	private RequestHandler requestHandler_;
	
	private ActionBar actionBar_;
	
	private ListView mainList_;
	private AutoCompleteTextView autoCompleteGoTo_;
	private AutoCompleteTextView autoCompleteGoFrom_;
	private Button switcharoo_;
	private Button go_;
	
	
	private TransportSummaryListAdapter adapter_;
	
	
	
	public TransportDisplayHander(TransportPlugin ownerActivity, RequestHandler requestHandler){
		ownerActivity_ = ownerActivity;
		activityContext_ = ownerActivity_.getApplicationContext();
		requestHandler_ = requestHandler;
		
		
		actionBar_ = (ActionBar) ownerActivity.findViewById(R.id.actionbar);
		setupActionBar(true);

		mainList_ = (ListView) ownerActivity.findViewById(R.id.transport_mainlist);
		adapter_ = new TransportSummaryListAdapter(ownerActivity, requestHandler_, actionBar_);
		mainList_.setAdapter(adapter_);
		
		autoCompleteGoFrom_ = (AutoCompleteTextView)ownerActivity.findViewById(R.id.transport_autoCompleteFrom);
		ArrayAdapter<Location> adapterFrom = new LocationAdapter(ownerActivity_, android.R.layout.simple_dropdown_item_1line, autoCompleteGoFrom_, requestHandler_);
		autoCompleteGoFrom_.setAdapter(adapterFrom);
		
		switcharoo_ = (Button)ownerActivity.findViewById(R.id.transport_switchDirection);
		switcharoo_.setOnClickListener(this);
		
		autoCompleteGoTo_ = (AutoCompleteTextView)ownerActivity_.findViewById(R.id.transport_autoCompleteTo);
		ArrayAdapter<Location> adapterTo = new LocationAdapter(ownerActivity_, android.R.layout.simple_dropdown_item_1line, autoCompleteGoTo_, requestHandler_);
		autoCompleteGoTo_.setAdapter(adapterTo);
		//autoCompleteGoTo_.setCompletionHint("where you wanna go"); //TODO remove this
		//autoCompleteGoTo_.setThreshold(3);
		
		go_ = (Button) ownerActivity_.findViewById(R.id.transport_go);
		go_.setOnClickListener(this);
		
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
			ArrayAdapter<Location> adapterFrom = new LocationAdapter(ownerActivity_, android.R.layout.simple_dropdown_item_1line, autoCompleteGoFrom_, requestHandler_);
			autoCompleteGoFrom_.setAdapter(adapterFrom);
			ArrayAdapter<Location> adapterTo = new LocationAdapter(ownerActivity_, android.R.layout.simple_dropdown_item_1line, autoCompleteGoTo_, requestHandler_);
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
	
	private void afficheUneJoliPetiteFenetreAvecLesDetailsDuTrajet(Connection c) {
		Toast.makeText(activityContext_, c.toString(), Toast.LENGTH_LONG).show();
		
		
	}

	protected void setupSummaryList(Map<String, String> commonDestinations, boolean visibility) {
		
		mainList_.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {

			TransportSummaryListAdapter y = (TransportSummaryListAdapter)parent.getAdapter();
			Connection s =  (Connection)y.getItem(position);
			
			afficheUneJoliPetiteFenetreAvecLesDetailsDuTrajet(s);
			}

			
		});
		
		TextView msgEmpty = (TextView) ownerActivity_.findViewById(R.id.msg_empty);
		
		if(!visibility) {
			mainList_.setVisibility(View.GONE);
			msgEmpty.setVisibility(View.VISIBLE);
			
		} else {
			mainList_.setVisibility(View.VISIBLE);
			msgEmpty.setVisibility(View.GONE);
		}
		
		adapter_.clearSections();
		
		for(String destination : commonDestinations.values()) {
			TransportSummaryAdapter adapter = new TransportSummaryAdapter(ownerActivity_, TransportPlugin.getReferenceDestination(), destination);
			adapter_.addSection(adapter.getCaption(), adapter);
		}

		adapter_.loadSummaryList();
	}

	protected void setupActionBar(boolean addHomeButton) {

		actionBar_ = (ActionBar) ownerActivity_.findViewById(R.id.actionbar);
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

		

	}
	
}
