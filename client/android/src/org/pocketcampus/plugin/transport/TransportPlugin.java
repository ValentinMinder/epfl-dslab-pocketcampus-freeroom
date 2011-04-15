package org.pocketcampus.plugin.transport;

import java.util.ArrayList;
import java.util.Map;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.core.ui.ActionBar;
import org.pocketcampus.core.ui.ActionBar.Action;
import org.pocketcampus.shared.plugin.transport.Destination;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class TransportPlugin extends PluginBase {
	private static final String REFERENCE_DESTINATION = "Ecublens VD, EPFL";
	private ActionBar actionBar_;
	private ListView mainList_;

	private TransportSummaryListAdapter adapter_;

	private SharedPreferences commonDestPrefs_;
	private Map<String, String> commonDestinations_;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transport_main);

		actionBar_ = (ActionBar) findViewById(R.id.actionbar);
		setupActionBar(true);

		mainList_ = (ListView) findViewById(R.id.transport_mainlist);
		adapter_ = new TransportSummaryListAdapter(this, getRequestHandler(), actionBar_);
		mainList_.setAdapter(adapter_);

		commonDestPrefs_ = getSharedPreferences("CommonDestPrefs", 0);
		setupSummuryList();
	}

	@Override
	protected void onResume() {
		super.onResume();
		setupSummuryList();
	}

	private void setupSummuryList() {
		Map<String, String> commonDestinationsInPrefs = (Map<String, String>) commonDestPrefs_.getAll();
		
		if(commonDestinationsInPrefs.equals(commonDestinations_)) {
			return;
		}
		
		commonDestinations_ = commonDestinationsInPrefs;
		adapter_.clearSections();
		
		for(String destination : commonDestinations_.values()) {
			TransportSummaryAdapter adapter = new TransportSummaryAdapter(this, REFERENCE_DESTINATION, destination);
			adapter_.addSection(adapter);
		}

		adapter_.loadSummaryList();
	}

	@Override
	protected void setupActionBar(boolean addHomeButton) {

		actionBar_ = (ActionBar) findViewById(R.id.actionbar);
		actionBar_.addAction(new Action() {

			@Override
			public void performAction(View view) {
				adapter_.loadSummaryList();
			}

			@Override
			public int getDrawable() {
				return R.drawable.refresh;
			}
		});

		super.setupActionBar(addHomeButton);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.transport, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;

		switch (item.getItemId()) {
		case R.id.transport_menu_settings:
			intent = new Intent(this, TransportPreference.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			return true;

		case R.id.transport_menu_detailed:
			intent = new Intent(this, TransportDetailed.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public PluginInfo getPluginInfo() {
		return new TransportInfo();
	}

	@Override
	public PluginPreference getPluginPreference() {
		return new TransportPreference();
	}
}
