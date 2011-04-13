package org.pocketcampus.plugin.transport;

import java.util.ArrayList;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.core.ui.ActionBar;
import org.pocketcampus.core.ui.ActionBar.Action;

import android.os.Bundle;
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
		adapter_ = new TransportSummaryListAdapter(this, getRequestHandler(), actionBar_);
		mainList_.setAdapter(adapter_);
		
		// TODO load this from a list editable in the preferences
		summaryList_ = new ArrayList<TransportSummaryAdapter>();
		summaryList_.add(new TransportSummaryAdapter(this, "Ecublens VD, EPFL", "Lausanne, Flon"));
		summaryList_.add(new TransportSummaryAdapter(this, "Lausanne, Vigie", "Ecublens VD, EPFL"));

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
	public PluginInfo getPluginInfo() {
		return new TransportInfo();
	}

	@Override
	public PluginPreference getPluginPreference() {
		return null;
	}
}
