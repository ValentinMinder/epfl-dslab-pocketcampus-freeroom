package org.pocketcampus.plugin.transport;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.pocketcampus.R;
import org.pocketcampus.shared.plugin.transport.Connection;
import org.pocketcampus.shared.plugin.transport.QueryConnectionsResult;
import org.pocketcampus.shared.utils.DateUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

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

	public TransportSummaryAdapter(Context ctx, String departure, String arrival) {
		state_ = SummaryState.EMPTY;
		inflater_ = LayoutInflater.from(ctx);
		departure_ = departure;
		arrival_ = arrival;
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
		String changes = "";
		if(connection.parts.size() > 1) {
			changes = ", " + (connection.parts.size()-1) + " changes";
		}
		
		// date formatter
		SimpleDateFormat formatter = new SimpleDateFormat();
		formatter.applyPattern("k:mm");
		
		// fill in the views
		String s1, s2;
		s1 = "In " + DateUtils.formatDateDelta(new Date(), connection.departureTime, "less than a minute");
		s2 = "At " + formatter.format(connection.departureTime) + ", arrival at " + formatter.format(connection.arrivalTime) + changes + ".";
		
		timeTextView.setText(s1);
		//transpTextView.setText(s2);
		
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
		return summary_.connections.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	
	/**
	 * Caption accessor.
	 * @return
	 */
	public String getCaption() {
		return departure_ + " to " + arrival_;
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






