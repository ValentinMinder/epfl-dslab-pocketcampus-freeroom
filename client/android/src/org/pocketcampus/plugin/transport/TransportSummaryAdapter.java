package org.pocketcampus.plugin.transport;

import java.text.SimpleDateFormat;

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
	private QueryConnectionsResult summaryList_;

	public TransportSummaryAdapter(Context ctx) {
		inflater_ = LayoutInflater.from(ctx);
	}
	
	void setTransportSummaries(QueryConnectionsResult summaryList) {
		summaryList_ = summaryList;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		if(summaryList_==null || summaryList_.connections==null) {
			return 0;
		}
		
		return summaryList_.connections.size();
	}

	@Override
	public Object getItem(int position) {
		return summaryList_.connections.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = inflater_.inflate(R.layout.transport_summaryentry, null);
		}
		
		Connection connection = (Connection) getItem(position);
		
		SimpleDateFormat formatter = new SimpleDateFormat();
		formatter.applyPattern("k:m");
		String s1 = "Departure at " + formatter.format(connection.departureTime);
		
		String s2 = "Arrival at " + formatter.format(connection.arrivalTime) + ", " + (connection.parts.size()-1) + " changes.";
		
		TextView timeTextView = (TextView) view.findViewById(R.id.travel_summary_time);
		timeTextView.setText(s1);
		
		TextView transpTextView = (TextView) view.findViewById(R.id.travel_summary_transporter);
		transpTextView.setText(s2);
		
		return view;
	}

	

}






