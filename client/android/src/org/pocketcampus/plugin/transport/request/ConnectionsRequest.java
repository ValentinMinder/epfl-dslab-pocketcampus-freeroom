package org.pocketcampus.plugin.transport.request;

import java.lang.reflect.Type;

import org.pocketcampus.core.communication.DataRequest;
import org.pocketcampus.core.parser.Json;
import org.pocketcampus.core.parser.JsonException;
import org.pocketcampus.shared.plugin.transport.QueryConnectionsResult;

import com.google.gson.reflect.TypeToken;

public abstract class ConnectionsRequest extends DataRequest {
	private QueryConnectionsResult summary;
	
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
	protected void doInBackgroundThread(String result) {
		Type SummaryListType = new TypeToken<QueryConnectionsResult>(){}.getType();
		summary = null;
		
		try {
			summary = Json.fromJson(result, SummaryListType);
		} catch (JsonException e) {
			cancel(false);
			return;
		}
	}
	
	@Override
	protected void doInUiThread(String result) {
		handleConnections(summary);
	}
	
	protected abstract void handleConnections(QueryConnectionsResult summary);
	
} 