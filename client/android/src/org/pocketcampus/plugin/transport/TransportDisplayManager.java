package org.pocketcampus.plugin.transport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TransportDisplayManager implements OnClickListener {
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
	
	
	
	public TransportDisplayManager(TransportPlugin ownerActivity, RequestHandler requestHandler){
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
	
	private void afficheUneJoliPetiteFenetreAvecLesDetailsDuTrajet(final Connection c) {
		//Toast.makeText(activityContext_, c.toString(), Toast.LENGTH_LONG).show();
		

		AlertDialog.Builder builder = new AlertDialog.Builder(ownerActivity_);
		builder.setTitle("Details");

		LayoutInflater inflater = (LayoutInflater) ownerActivity_.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.transport_details_dialog, null);
		builder.setView(layout);
		
		ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map;
		for(Connection.Part p : c.parts){
			map = new HashMap<String, String>();
			map.put("dept", "hh:mm");
			map.put("from", p.departure.name);
			map.put("arrt", "hh:mm");
			map.put("to", p.arrival.name);
			
			mylist.add(map);
		}
		
		String[] keys = {"dept", "from", "arrt", "to"}; 
		int[] ids = {R.id.transport_details_dialog_dep_time, R.id.transport_details_dialog_dep_place,
				R.id.transport_details_dialog_arr_time, R.id.transport_details_dialog_arr_place};
		
		SimpleAdapter mSchedule = new SimpleAdapter(ownerActivity_, mylist, R.layout.transport_details_dialog_row, keys, ids);
		

		builder.setNeutralButton("Share", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {				
				dialog.dismiss();
				
				Intent shareIntent = new Intent(Intent.ACTION_SEND); 
		        shareIntent.putExtra(Intent.EXTRA_TEXT, c.toString() + "\n\n sent via pocketcampus android app");
		        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "timetables");
		        shareIntent.setType("text/plain");
		        ownerActivity_.startActivity(shareIntent); 
				
			}
		});
		
		builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {				
				dialog.dismiss();
			}
		});

		AlertDialog alert = builder.create();
		alert.setCanceledOnTouchOutside(true);
		
		alert.show();
		
		ListView list = (ListView)alert.findViewById(R.id.transport_details_dialog_list);
		list.setAdapter(mSchedule);
	
		
	}
	
	private void shareToEverybodyOnThePlanetYourSuperTravelPlane(Connection c){
//		AlertDialog.Builder builder = new AlertDialog.Builder(ownerActivity_);
//		builder.setTitle("Share travel plan");
//
//		LayoutInflater inflater = (LayoutInflater) ownerActivity_.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		View layout = inflater.inflate(R.layout.transport_share_dialog, null);
//		builder.setView(layout);
//
//		builder.setNeutralButton("Share", new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int id) {				
//				dialog.dismiss();
				Intent sendIntent = new Intent(Intent.ACTION_VIEW);
				sendIntent.putExtra("sms_body", c.toString()); 
				sendIntent.setType("vnd.android-dir/mms-sms");
				ownerActivity_.startActivity(sendIntent); 
//			}
//		});
//		
//		builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int id) {				
//				dialog.dismiss();
//			}
//		});
//		
//		
//
//		AlertDialog alert = builder.create();
//		alert.setCanceledOnTouchOutside(true);
//		alert.show();
	}

	protected void setupSummaryList(Map<String, String> commonDestinations, boolean noDestination) {
		
		mainList_.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {

			TransportSummaryListAdapter adapter = (TransportSummaryListAdapter)parent.getAdapter();
			Connection travel =  (Connection)adapter.getItem(position);
			
			afficheUneJoliPetiteFenetreAvecLesDetailsDuTrajet(travel);
			}

			
		});
		
		TextView msgEmpty = (TextView) ownerActivity_.findViewById(R.id.msg_empty);
		
		if(noDestination) {
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
		
		ownerActivity_.setupActionBar(addHomeButton);

		

	}
	
}
