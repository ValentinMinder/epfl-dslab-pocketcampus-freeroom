package org.pocketcampus.plugin.transport;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.pocketcampus.R;
import org.pocketcampus.shared.plugin.transport.Connection;
import org.pocketcampus.shared.plugin.transport.QueryConnectionsResult;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TransportSummaryAdapter extends BaseAdapter {
	private LayoutInflater inflater_;
	private QueryConnectionsResult summary_;
	private String departure_;
	private String arrival_;

	public TransportSummaryAdapter(Context ctx, String departure, String arrival) {
		inflater_ = LayoutInflater.from(ctx);
		departure_ = departure;
		arrival_ = arrival;
	}
	
	public void setSummary(QueryConnectionsResult summary) {
		summary_ = summary;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		if(summary_ == null) {
			return 1;
		}
		
		if(summary_.connections == null) {
			return 0;
		}
		
		return summary_.connections.size();
	}

	@Override
	public Object getItem(int position) {
		return summary_.connections.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if(summary_==null) {
			View view = inflater_.inflate(R.layout.transport_loadingentry, null);
			return view;
		}
		
		View view = inflater_.inflate(R.layout.transport_summaryentry, null);
		
		Connection connection = (Connection) getItem(position);
		
		TextView timeTextView = (TextView) view.findViewById(R.id.travel_summary_time);
		TextView transpTextView = (TextView) view.findViewById(R.id.travel_summary_transporter);
		String s1, s2;
		
		int minutesLeft = (int) (connection.departureTime.getTime() - new Date().getTime()) / (60 * 1000);
		
		String changes = "";
		if(connection.parts.size() > 1) {
			changes = ", " + (connection.parts.size()-1) + " changes";
		}
		
		String transporter = "";
//		transporter = ", by " + connection.link;
		
		SimpleDateFormat formatter = new SimpleDateFormat();
		formatter.applyPattern("k:mm");
		
		s1 = "In " + minutesLeft + " minute" + ((minutesLeft>1)?"s":"");
		s2 = formatter.format(connection.departureTime) + ", arrival at " + formatter.format(connection.arrivalTime) + changes + transporter;
		
		timeTextView.setText(s1);
		transpTextView.setText(s2);
		
		return view;
	}

	public String getCaption() {
		return departure_ + " to " + arrival_;
	}
	
	public String getDeparture() {
		return departure_;
	}
	
	public String getArrival() {
		return arrival_;
	}

}






