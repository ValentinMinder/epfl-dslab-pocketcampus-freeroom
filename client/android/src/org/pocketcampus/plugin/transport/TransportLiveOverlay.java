package org.pocketcampus.plugin.transport;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.lang.reflect.Type;
import java.util.Timer;
import java.util.TimerTask;

import org.pocketcampus.R;
import org.pocketcampus.core.communication.DataRequest;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.plugin.map.MapPlugin;
import org.pocketcampus.shared.plugin.transport.QueryConnectionsResult;
import org.pocketcampus.shared.plugin.transport.Railway;
import org.pocketcampus.shared.plugin.transport.RailwayNode;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

/**
 * Animated overlay showing the transporters on the map.
 * @status WIP 
 * @author Florian
 */
public class TransportLiveOverlay {
	private Context ctx_;
	private Railway path_;
	private int nbRunningReq_;
	private ConnectionPool pool_;
	private MapPlugin mapPlugin_;

	public TransportLiveOverlay(Context ctx) {
		ctx_ = ctx;
		path_ = readRailwayFromFile(R.raw.railway_m1);
		nbRunningReq_ = 0;
		pool_ = new ConnectionPool(ctx_, path_);
	}
	
	class TimeToArrivalRequest extends DataRequest {
		@Override
		protected int timeoutDelay() {
			return 5;
		}
		
		@Override
		protected int expirationDelay() {
			return 15 * 60;
		}
		
		@Override
		protected void doInBackgroundThread(String result) {
			Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss Z").create();

			Type QueryConnectionsResultType = new TypeToken<QueryConnectionsResult>(){}.getType();
			QueryConnectionsResult summary = gson.fromJson(result, QueryConnectionsResultType);
			
			if(summary!=null && summary.connections!=null && summary.connections.get(0)!=null) {
				//System.out.println(node.getTag("uic_name"));
				
				for (int i = 0; i < summary.connections.size(); i++) {
					if(summary.connections.get(i).parts.size()==1) { // TODO somewhow limit to metro!
						//System.out.println(" -> "+summary.connections.get(i).toString());
						pool_.addConnection(summary.connections.get(i));
					}
					
				}
				
				//System.out.println("\n---");
				
			} else {
				//System.out.println(node.getTag("uic_name") + " -> Error!");
			}
			
			nbRunningReq_--;
			
			if(nbRunningReq_ == 0) {
				//mapPlugin.setRailwayOverlay(pool.crunch(0.0003, 3.2));
				
//				double bestVariation = 10000.0;
//				double variation;
//				double bestSpeed = 0;
//				double bestStopTime = 0;
//				
//				for (double i = 0; i < 15.0; i+=.1) {
//					for (double j = 0.0001; j < 0.01; j+=0.0001) {
//						variation = pool.crunch(j, i);
//						System.out.println("stop: "+i+"s, speed: "+j+"u/s, \tvariation: " + variation);
//						
//						if(variation < bestVariation) {
//							bestSpeed = j;
//							bestStopTime = i;
//							bestVariation = variation;
//						}
//					}
//				}
//				
//				System.out.println("BEST stop: "+bestStopTime+"s, speed: "+bestSpeed+"u/s, \tvariation: " + bestVariation);
				
				// 3.2, 0.0003
			}
			
		}
		
		@Override
		protected void doInUiThread(String result) {
			if(nbRunningReq_ == 0) {
				mapPlugin_.setRailwayOverlay(pool_.crunch(0.0003, 3.2));
				
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {
					private Handler handler = new Handler(Looper.getMainLooper());

					public void run() {
						handler.post(new Runnable() {
							public void run() {
								refreshOverlay(mapPlugin_);
							}
						});
					}

				}, 500);
			}
		}
	}
	
	public void refreshOverlay(final MapPlugin mapPlugin) {
		mapPlugin_ = mapPlugin;
		
		for(final RailwayNode node : path_.getNodes().values()) {
			if(node.getTag("uic_ref")!=null && !node.getTag("uic_ref").equals("8530749")) {
				
				String uicRef = node.getTag("uic_ref");
				
				RequestParameters params = new RequestParameters();
				params.addParameter("fromID", uicRef);
				params.addParameter("toID", "8501181"); //lausanne-flon
				
				TransportPlugin.getTransportRequestHandler().execute(new TimeToArrivalRequest(), "connectionsFromStationsIDs", params);
				
				nbRunningReq_++;
			}
		}
	}
	
	public void requestOverlay(final MapPlugin mapPlugin) {
		refreshOverlay(mapPlugin);
	}
	
	private Railway readRailwayFromFile(int fileId) {
		try{
			InputStream fileIs = ctx_.getResources().openRawResource(fileId);
			InputStream buffer = new BufferedInputStream(fileIs);
			ObjectInput input = new ObjectInputStream (buffer);
			try{
				Railway path = (Railway)input.readObject();
				return path;
			}
			finally {
				input.close();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return null;
	}

//	public void requestOverlay(final MapPlugin mapPlugin) {
//		mapPlugin.setRailwayOverlay(getOverlay());
//		
//		class TimeToArrivalRequest extends DataRequest {
//			@Override
//			protected void doInUiThread(String result) {
//				mapPlugin.setRailwayOverlay(getOverlay());
//			}
//		}
//		
//		RequestParameters params = new RequestParameters();
//		params.addParameter("fromID", "8501211");
//		params.addParameter("toID", "8501207");
//		TransportPlugin.getTransportRequestHandler().execute(new TimeToArrivalRequest(), "connectionsFromStationsIDs", params);
//	}
}














