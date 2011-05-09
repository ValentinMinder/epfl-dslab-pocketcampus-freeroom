package org.pocketcampus.plugin.transport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.pocketcampus.R;
import org.pocketcampus.plugin.logging.Tracker;
import org.pocketcampus.shared.plugin.transport.Connection;
import org.pocketcampus.shared.plugin.transport.Connection.Footway;
import org.pocketcampus.shared.plugin.transport.Connection.Trip;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ConnectionDetailsDialog extends Dialog{
	private Connection connection_;
	private Context ctx_;

	public ConnectionDetailsDialog(TransportPlugin tp, Connection connection) {
		super(tp);
		connection_ = connection;
		ctx_ = tp.getApplicationContext();

		// Setups dialog.
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.transport_details_dialog);
		getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		setCanceledOnTouchOutside(true);
		
		Tracker.getInstance().trackPageView("transport/detail");
		
		setDialogContent();
	}

	private void setDialogContent() {
		ArrayList<HashMap<String, String>> connectionParts = new ArrayList<HashMap<String, String>>();

		SimpleDateFormat formatter = new SimpleDateFormat();
		formatter.applyPattern("k:mm");

		HashMap<String, String> partRow;
		for(Connection.Part part : connection_.parts){
			partRow = new HashMap<String, String>();
			String departure = "";
			String arrival = "";
			
			if(part instanceof Trip) {
				departure = formatter.format(((Trip)part).departureTime);
				arrival = formatter.format(((Trip)part).arrivalTime);
				
			} else if(part instanceof Footway) {
				departure = ((Footway)part).min + " " + ctx_.getResources().getString(R.string.transport_walk_in_min);
			}
			
//			if(((Trip)part).departureTime != null) {
//				departure = formatter.format(((Trip)part).departureTime);
//				arrival = formatter.format(((Trip)part).arrivalTime);
//			}
			
			partRow.put("dept", departure);
			partRow.put("from", part.departure.name);

			partRow.put("arrt", arrival);
			partRow.put("to", part.arrival.name);

			connectionParts.add(partRow);
		}

		String[] keys = {"dept", "from", "arrt", "to"};

		int[] ids = {
				R.id.transport_details_dialog_dep_time, 
				R.id.transport_details_dialog_dep_place,
				R.id.transport_details_dialog_arr_time, 
				R.id.transport_details_dialog_arr_place
		};

		SimpleAdapter mSchedule = new SimpleAdapter(getContext(), connectionParts, R.layout.transport_details_dialog_row, keys, ids);

		//		builder.setNeutralButton(activityContext_.getResources().getString(R.string.Share), new DialogInterface.OnClickListener() {
		//			public void onClick(DialogInterface dialog, int id) {				
		//				dialog.dismiss();
		//				
		//				Intent shareIntent = new Intent(Intent.ACTION_SEND); 
		//		        shareIntent.putExtra(Intent.EXTRA_TEXT, stringifier(c) + activityContext_.getResources().getString(R.string.transport_sentViaPCtransport));
		//		        shareIntent.putExtra(Intent.EXTRA_SUBJECT, activityContext_.getResources().getString(R.string.transport_timetables));
		//		        shareIntent.setType("text/plain");
		//		        ownerActivity_.startActivity(shareIntent); 
		//				
		//			}
		//		});

		ListView list = (ListView) findViewById(R.id.transport_details_dialog_list);
		list.setAdapter(mSchedule);

		TextView title = (TextView) findViewById(R.id.transport_title_dialog);
		title.setText(TransportPlugin.REFERENCE_DESTINATION_SHORTNAME + " " + ctx_.getResources().getString(R.string.transport_to)+ " " + connection_.to.name); 


	}

}
