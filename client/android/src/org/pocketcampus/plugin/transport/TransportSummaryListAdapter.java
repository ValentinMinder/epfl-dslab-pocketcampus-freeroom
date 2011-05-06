package org.pocketcampus.plugin.transport;

import java.lang.reflect.Type;

import org.pocketcampus.R;
import org.pocketcampus.core.communication.RequestHandler;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.core.communication.DataRequest;
import org.pocketcampus.core.parser.Json;
import org.pocketcampus.core.parser.JsonException;
import org.pocketcampus.core.ui.ActionBar;
import org.pocketcampus.shared.plugin.transport.QueryConnectionsResult;

import com.google.gson.reflect.TypeToken;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TransportSummaryListAdapter extends SeparatedListAdapter {
	private LayoutInflater inflater_;
	private RequestHandler requestHandler_;
	private ActionBar actionBar_;
	private int progressCount_ = 0;
	
	public TransportSummaryListAdapter(Context ctx, RequestHandler requestHandler, ActionBar actionBar) {
		inflater_ = LayoutInflater.from(ctx);
		requestHandler_ = requestHandler;
		actionBar_ = actionBar;
	}
	
	/**
	 * Loads all the summaries.
	 */
	public void loadSummaryList() {
		for(Section section : sections_) {
			TransportSummaryAdapter adapter = (TransportSummaryAdapter)section.adapter;
			loadSummary(adapter);
		}
		
		notifyDataSetChanged();
	}
	
	private void loadSummary(final TransportSummaryAdapter adapter) {
		incrementProgressCounter();
		
		class ConnectionsRequest extends DataRequest {
			
			@Override
			protected int expirationDelay() {
				// 5 minutes
				return 60 * 5;
			}
			
			@Override
			protected int timeoutDelay() {
				// longer timeout as CFF website is often slow
				return 10;
			}
			
			@Override
			protected void doInUiThread(String result) {
				Type SummaryListType = new TypeToken<QueryConnectionsResult>(){}.getType();
				QueryConnectionsResult summary = null;
				
				try {
					summary = Json.fromJson(result, SummaryListType);
				} catch (JsonException e) {
					onCancelled();
					return;
				}

				adapter.setSummary(summary);
				notifyDataSetChanged();
				
				decrementProgressCounter();
			}
			
			@Override
			protected void onCancelled() {
				decrementProgressCounter();
				adapter.setSummaryError();
				notifyDataSetChanged();
			}
			
		} 

		RequestParameters params = new RequestParameters();
		params.addParameter("from", adapter.getDeparture());
		params.addParameter("to", adapter.getArrival());

		requestHandler_.execute(new ConnectionsRequest(), "connections", params);
	}
	
	// TODO reuse from map
	private synchronized void incrementProgressCounter() {
		progressCount_++;
		actionBar_.setProgressBarVisibility(View.VISIBLE);
	}
	
	// TODO reuse from map
	private synchronized void decrementProgressCounter() {
		progressCount_--;
		if(progressCount_ < 0) { //Should never happen!
			Log.e("MapPlugin", "ERROR progresscount is negative!");
		}
		
		if(progressCount_ <= 0) {
			actionBar_.setProgressBarVisibility(View.GONE);
		}
	}
	
	@Override
	protected View getHeaderView(String caption, int index, View convertView, ViewGroup parent) {
		View view = convertView;
		view = inflater_.inflate(R.layout.food_list_header, null);
		
		TextView timeTextView = (TextView) view.findViewById(R.id.list_header_title);
		timeTextView.setText(caption);
		
		return view;
	}

}
