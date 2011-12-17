package org.pocketcampus.plugin.map.android.search;

import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.plugin.map.android.MapMainController;
import org.pocketcampus.plugin.map.android.MapMainView;
import org.pocketcampus.plugin.map.android.MapModel;
import org.pocketcampus.plugin.map.android.MapPlugin;
import org.pocketcampus.plugin.map.android.iface.IMapView;
import org.pocketcampus.plugin.map.shared.MapElementBean;
import org.pocketcampus.plugin.map.shared.MapItem;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * A class used to search map elements and display the result of the search
 * Look at http://developer.android.com/guide/topics/search/search-dialog.html
 * 
 * @author Johan, Jonas
 *
 */
public class MapSearchActivity extends PluginView implements IMapView {
	private ProgressDialog progressDialog_;

	private MapMainController mController;
	private MapModel mModel;

	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return MapMainController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		mController = (MapMainController) controller;
		mModel = (MapModel) controller.getModel();

		setContentView(R.layout.map_search_result);
		//handleIntent(getIntent());
	}

	/**
	 * Verify the Intent's action and get the query
	 * @param intent
	 */
	protected void handleIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			query = query.trim();
			mController.search(query);
		}
	}

	//	@Override
	//	protected void onNewIntent(Intent intent) {
	//		super.onNewIntent(intent);
	//		setIntent(intent);
	//		handleIntent(intent);
	//	}

	/**
	 * Searches the elements containing the string "query" as title or description
	 * @param query the text to search
	 */
	//	private void searchMap(String query) {
	//		
	//		class MapSearchRequest extends DataRequest {
	//			
	//			private MapSearchActivity a_;
	//			private ArrayAdapter<String> results_;
	//			private List<MapElementBean> items_;
	//			
	//			public MapSearchRequest(MapSearchActivity a) {
	//				a_ = a;
	//			}
	//			
	//			@Override
	//			protected void doInBackgroundThread(String result) {
	//				if(result == null) {
	//					return;
	//				}
	//				Log.d("MapSearchActivity", "Response received " + result);
	//				
	//				//Deserializes the response
	//				Type mapElementType = new TypeToken<List<MapElementBean>>(){}.getType();
	//				items_ = new ArrayList<MapElementBean>();
	//
	//				try {
	//					items_ = Json.fromJson(result, mapElementType);
	//				} catch (Exception e) {
	//					Log.e("MapSearchActivity", e.toString());
	//					return;
	//				}
	//				if(items_ == null) {
	//					return;
	//				}
	//
	//
	//				results_ = new ArrayAdapter<String>(a_, R.layout.map_list);
	//				if(items_.size() <= 0) {
	//					results_.add(getResources().getString(R.string.search_no_results));
	//				} else {
	//					for(MapElementBean meb : items_) {
	//						results_.add(meb.getTitle());
	//					}
	//				}
	//				
	//			}
	//
	//			@Override
	//			protected void doInUiThread(String result) {
	//				Log.d("MapSearchActivity", "doInUiThread-> " + result);
	//				if(progressDialog_ != null && progressDialog_.isShowing()) {
	//					progressDialog_.dismiss();
	//				}
	//				
	//				if(results_ == null) {
	//					try {
	//						Notification.showToast(getApplicationContext(), R.string.server_connection_error);
	//					} catch(Exception e) {
	//						Log.e("MapSearchActivity", e.toString());
	//					}
	//					finish();
	//					return;
	//				}
	//				
	//				parseAndDisplayResult(results_, items_);
	//			}
	//			
	//			@Override
	//			protected void onCancelled() {
	//				super.onCancelled();
	//				if(progressDialog_ != null && progressDialog_.isShowing()) {
	//					progressDialog_.dismiss();
	//				}
	//				try {
	//					Notification.showToast(getApplicationContext(), R.string.server_connection_error);
	//				} catch(Exception e) {
	//					Log.e("MapSearchActivity", e.toString());
	//				}
	//				finish();
	//			}
	//		}
	//		
	//		progressDialog_ = new ProgressDialog(this);
	//		progressDialog_.setTitle(getResources().getString(R.string.please_wait));
	//		progressDialog_.setMessage(getResources().getString(R.string.map_searching));
	//		progressDialog_.setCancelable(true);
	//		
	//		progressDialog_.setOnCancelListener(new OnCancelListener() {
	//			@Override
	//			public void onCancel(DialogInterface dialog) {
	//				finish();
	//				
	//			}
	//		});
	//		
	//		progressDialog_.show();
	//		
	//		
	//		
	//		RequestParameters params = new RequestParameters();
	//		params.addParameter("q", query);
	//		
	//		RequestHandler rh = new RequestHandler(new MapInfo());
	//		rh.execute(new MapSearchRequest(this), "search", params);
	//	}

	/**
	 * Parses the result from JSON and then displays the list of results
	 * @param results A list containing the results title
	 * @param results2 Beans of the items
	 */
	private void parseAndDisplayResult(ArrayAdapter<String> results, final List<MapItem> results2) {

		if(results != null && results.getCount() == 1 && results2 != null && results2.size() > 0) {
			startMapActivity(results2.get(0));
			finish();
		}

		ListView lv = getListView();
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(results2 != null && results2.size() > 0) {
					try {
						MapItem meb = results2.get(position); 
						startMapActivity(meb);
					} catch (Exception e) {}
				}
			}
		});

		setListAdapter(results);

	}

	private void setListAdapter(ArrayAdapter<String> results) {
		getListView().setAdapter(results);
	}

	private ListView getListView() {
		return (ListView) findViewById(R.id.map_result_list);
	}

	/**
	 * Launch the map with the map element to be displayed
	 * @param mapItem a map element to be displayed.
	 */
	private void startMapActivity(MapItem mapItem) {
		Intent startMapActivity = new Intent(this, MapMainView.class);
		startMapActivity.putExtra("MapElement", mapItem);
		//startMapActivity.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP); //had a problem of outofmemory if too many consecutive searches
		//startMapActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //possible if we dont want the already displayed layers
		startMapActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(startMapActivity);
	}

	@Override
	public void networkErrorHappened() {
		// TODO Auto-generated method stub

	}

	@Override
	public void layersUpdated() {
		// TODO Auto-generated method stub

	}

	@Override
	public void layerItemsUpdated() {
		// TODO Auto-generated method stub

	}

	@Override
	public void searchResultsUpdated() {
		List<MapItem> results = mModel.getSearchResults();

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.map_list);

		if(results.size() <= 0) {
			adapter.add("No results.");
		} else {
			for(MapItem meb : results) {
				adapter.add(meb.getTitle());
			}
		}

		//setListAdapter(adapter);
		parseAndDisplayResult(adapter, results);
	}

}
