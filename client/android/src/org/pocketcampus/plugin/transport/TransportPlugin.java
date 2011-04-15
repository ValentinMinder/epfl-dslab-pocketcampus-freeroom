package org.pocketcampus.plugin.transport;

import java.util.ArrayList;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.core.ui.ActionBar;
import org.pocketcampus.core.ui.ActionBar.Action;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class TransportPlugin extends PluginBase {
	private ActionBar actionBar_;
	private ListView mainList_;
	
	private ArrayList<TransportSummaryAdapter> summaryList_;
	private TransportSummaryListAdapter adapter_;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transport_main);

		actionBar_ = (ActionBar) findViewById(R.id.actionbar);
		setupActionBar(true);
		
		mainList_ = (ListView) findViewById(R.id.transport_mainlist);
		//mainList_.setItemsCanFocus(false);
		adapter_ = new TransportSummaryListAdapter(this, getRequestHandler(), actionBar_);
		mainList_.setAdapter(adapter_);
		
		// TODO load this from a list editable in the preferences
		summaryList_ = new ArrayList<TransportSummaryAdapter>();
		summaryList_.add(new TransportSummaryAdapter(this, "Ecublens VD, EPFL", "Lausanne, Flon"));
		summaryList_.add(new TransportSummaryAdapter(this, "Lausanne, Vigie", "Ecublens VD, EPFL"));
		summaryList_.add(new TransportSummaryAdapter(this, "Ecublens VD, EPFL", "Renens VD"));
		//summaryList_.add(new TransportSummaryAdapter(this, "Paris", "Berlin"));

		for(TransportSummaryAdapter summary : summaryList_) {
			adapter_.addSection(summary);
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
