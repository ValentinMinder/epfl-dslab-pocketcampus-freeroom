package org.pocketcampus.plugin.map.android.search;

import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardLayout;
import org.pocketcampus.plugin.map.android.MapMainController;
import org.pocketcampus.plugin.map.android.MapMainView;
import org.pocketcampus.plugin.map.android.MapModel;
import org.pocketcampus.plugin.map.android.iface.IMapView;
import org.pocketcampus.plugin.map.shared.MapItem;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * A class used to search map elements and display the result of the search Look
 * at http://developer.android.com/guide/topics/search/search-dialog.html
 * 
 * @author Florian <florian.laurent@epfl.ch>
 * @author Johan <johan.leuenberger@epfl.ch>
 * @author Jonas <jonas.schmid@epfl.ch>
 * 
 */
public class MapSearchActivity extends PluginView implements IMapView {
	private ProgressDialog progressDialog_;

	private MapMainController mController;
	private MapModel mModel;

	private StandardLayout mLayout;

	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return MapMainController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {
		mController = (MapMainController) controller;
		mModel = (MapModel) controller.getModel();

		mLayout = new StandardLayout(this);
		mLayout.setText(getString(R.string.map_searching));

		setContentView(mLayout);

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mLayout.addView(inflater.inflate(R.layout.map_search_result, null));
	}

	/**
	 * Verify the Intent's action and get the query
	 * 
	 * @param intent
	 */
	protected void handleIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			// Tracker
//			Tracker.getInstance().trackPageView("map/search/" + query);
			query = query.trim();
			mController.search(query);
		}
	}

	/**
	 * Parses the result from JSON and then displays the list of results
	 * 
	 * @param results
	 *            A list containing the results title
	 * @param results2
	 *            Beans of the items
	 */
	private void parseAndDisplayResult(ArrayAdapter<String> results,
			final List<MapItem> results2) {

		if (results != null && results.getCount() == 1 && results2 != null
				&& results2.size() > 0) {
			startMapActivity(results2.get(0));
			finish();
		}

		ListView lv = getListView();
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (results2 != null && results2.size() > 0) {
					try {
						MapItem meb = results2.get(position);
						startMapActivity(meb);
					} catch (Exception e) {
					}
				}
			}
		});

		mLayout.hideText();
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
	 * 
	 * @param mapItem
	 *            a map element to be displayed.
	 */
	private void startMapActivity(MapItem mapItem) {
		Intent startMapActivity = new Intent(this, MapMainView.class);
		startMapActivity.putExtra("MapElement", mapItem);
		startMapActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(startMapActivity);
	}

	@Override
	public void searchResultsUpdated() {
		List<MapItem> results = mModel.getSearchResults();

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.map_list);

		if (results.size() > 0) {
			for (MapItem meb : results) {
				adapter.add(meb.getTitle());
			}

			parseAndDisplayResult(adapter, results);

		} else {
			mLayout.setText(getString(R.string.map_search_no_results));
		}
	}

	@Override
	public void networkErrorHappened() {
	}

	@Override
	public void layersUpdated() {
	}

	@Override
	public void layerItemsUpdated() {
	}
}
