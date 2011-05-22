package org.pocketcampus.plugin.transport;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.pocketcampus.R;
import org.pocketcampus.shared.plugin.transport.Connection;
import org.pocketcampus.shared.plugin.transport.QueryConnectionsResult;
import org.pocketcampus.shared.utils.DateUtils;
import org.pocketcampus.shared.utils.StringUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Adapter to diplay the summaries of the travel.
 * @author Florian
 * @status working
 */
public class TransportSummaryAdapter extends BaseAdapter {
	private enum SummaryState {EMPTY, VALID, ERROR};
	
	private QueryConnectionsResult summary_;
	private SummaryState state_;
	private LayoutInflater inflater_;
	private String departure_;
	private String arrival_;
	private int nbMaxItems_ = 3;
	private String to_StringRessource;
	private String from_StringRessource;
	private String lessThanAMinute_StringRessource;
	private String inCapital_StringRessource;
	private String atCapital_StringRessource;
	private String arrival_StringRessource;
	private String departures_StringRessource;
	private String at_StringRessource;
	private String change_StringRessource;

	public TransportSummaryAdapter(Context ctx, String departure, String arrival) {
		state_ = SummaryState.EMPTY;
		inflater_ = LayoutInflater.from(ctx);
		departure_ = departure;
		arrival_ = arrival;
		
		to_StringRessource = 				(String) ctx.getResources().getText(R.string.transport_to);
		from_StringRessource = 				(String) ctx.getResources().getText(R.string.transport_from);
		lessThanAMinute_StringRessource = 	(String) ctx.getResources().getText(R.string.transport_lessThanAMinute);
		departures_StringRessource = 		(String) ctx.getResources().getText(R.string.transport_departures);
		arrival_StringRessource = 			(String) ctx.getResources().getText(R.string.transport_arrival);
		change_StringRessource = 			(String) ctx.getResources().getText(R.string.transport_change);
		at_StringRessource = 				(String) ctx.getResources().getText(R.string.transport_at);
		
		atCapital_StringRessource = 			StringUtils.capitalize((String) ctx.getResources().getText(R.string.transport_at));
		inCapital_StringRessource = 			StringUtils.capitalize((String) ctx.getResources().getText(R.string.transport_in));
	}
	
	/**
	 * Sets the summary content.
	 * @param summary
	 */
	public void setSummary(QueryConnectionsResult summary) {
		if(summary==null || summary.connections==null) {
			setSummaryError();
			return;
		}
		
		summary_ = summary;
		state_ = SummaryState.VALID;
		notifyDataSetChanged();
	}
	
	public QueryConnectionsResult getSummary(){
		return summary_;		
	}
	
	/**
	 * Indicates that there has been an error while retrieving the data.
	 */
	void setSummaryError() {
		summary_ = null;
		state_ = SummaryState.ERROR;
		notifyDataSetChanged();
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view;
		
		if(state_ == SummaryState.EMPTY) {
			view = inflater_.inflate(R.layout.transport_loadingentry, null);
			return view;
		}
		
		if(state_ == SummaryState.ERROR) {
			view = inflater_.inflate(R.layout.transport_errorentry, null);
			return view;
		}
		
		view = inflater_.inflate(R.layout.transport_summaryentry, null);
		view = fillView(position, view);
		
		return view;
	}
	
	/**
	 * Complete the view with the relevant travel info.
	 * @param position
	 * @param view
	 * @return
	 */
	private View fillView(int position, View view) {
		Connection connection = (Connection) getItem(position);
		
		TextView timeTextView = (TextView) view.findViewById(R.id.travel_summary_time);
		//TextView transpTextView = (TextView) view.findViewById(R.id.travel_summary_transporter);
		
		// nb of changes
		String changesD = "";
		if(connection.parts.size() > 1) {
			changesD = ", " + (connection.parts.size()-1) + " " + change_StringRessource;
		}
		
		// date formatter
		SimpleDateFormat formatter = new SimpleDateFormat();
		formatter.applyPattern("k:mm");
		
		// fill in the views
		String s1 = inCapital_StringRessource + " " + DateUtils.formatDateDelta(new Date(), connection.departureTime, lessThanAMinute_StringRessource);
		timeTextView.setText(s1);
		
		return view;
	}

	@Override
	public int getCount() {
		if(summary_ == null) {
			return 1;
		}
		
		if(summary_.connections == null) {
			return 0;
		}
		
		return Math.min(summary_.connections.size(), nbMaxItems_);
	}

	@Override
	public Object getItem(int position) {
		if(summary_ != null && summary_.connections != null)
			return summary_.connections.get(position);
		else
			return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	
	/**
	 * Travel summary caption, replacing reference by its shortname.
	 * @return
	 */
	public String getCaption() {
		String referenceDest = TransportPlugin.REFERENCE_DESTINATION_SHORTNAME;
		
		if(arrival_.equals(TransportPlugin.REFERENCE_DESTINATION)) {
			return departure_ +" "+  to_StringRessource +" "+ referenceDest;
		}
		
		if(departure_.equals(TransportPlugin.REFERENCE_DESTINATION)) {
			return referenceDest +" "+  to_StringRessource +" "+ arrival_;
		}
		
		return departure_ +" "+  to_StringRessource +" "+ arrival_;
		
		//return departure_ + " " + to_StringRessource + " " + arrival_;
//		if(!arrival_.equals(TransportPlugin.REFERENCE_DESTINATION))
//			return departures_StringRessource +" "+ to_StringRessource +" "+ arrival_;
//		else
//			return departures_StringRessource +" "+ from_StringRessource +" "+ departure_ +" "+ to_StringRessource +" "+ TransportPlugin.REFERENCE_DESTINATION_SHORTNAME;
	}
	
	/**
	 * Departure accessor.
	 * @return
	 */
	public String getDeparture() {
		return departure_;
	}
	
	/**
	 * Arrival accessor.
	 * @return
	 */
	public String getArrival() {
		return arrival_;
	}

}






