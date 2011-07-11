package org.pocketcampus.plugin.labs;

import java.util.Vector;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.Core;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.core.ui.ActionBar;
import org.pocketcampus.plugin.logging.Tracker;
import org.pocketcampus.plugin.mainscreen.PluginsAdapter;
import org.pocketcampus.plugin.scanner.ScannerPlugin;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class LabsPlugin extends PluginBase {
	private Context ctx_;
	private ActionBar actionBar_;
	private int progressCount_ = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.labs_main);
		actionBar_ = (ActionBar) findViewById(R.id.actionbar);
		
		ctx_ = this;
		
		setupActionBar(true);

		Tracker.getInstance().trackPageView("labs/home");
		
		setupPluginList();
	}
	
	private void setupPluginList() {
		// TODO put list in XML file
		//String[] pluginsString  = getResources().getStringArray(R.array.labs_plugins);
		
		Vector<PluginBase> plugins = new Vector<PluginBase>();
		plugins.add(new ScannerPlugin());
		
		TextView centralMsg = (TextView) findViewById(R.id.labs_central_msg);
		ListView pluginsList = (ListView) findViewById(R.id.labs_mainlist);
		
		if(plugins.size() != 0) {
			centralMsg.setVisibility(TextView.GONE);
			
			final ListAdapter adapter = new PluginsAdapter(ctx_, plugins);
			pluginsList.setAdapter(adapter);
			
			pluginsList.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Core.getInstance().displayPlugin(ctx_, (PluginBase) adapter.getItem(position));
				}
			});
			
		} else {
			centralMsg.setVisibility(TextView.VISIBLE);
			
		}
	}

	@Override
	public PluginInfo getPluginInfo() {
		return new LabsInfo();
	}

	@Override
	public PluginPreference getPluginPreference() {
		return null;
	}
	
	private synchronized void incrementProgressCounter() {
		progressCount_ ++;
		actionBar_.setProgressBarVisibility(View.VISIBLE);
	}

	private synchronized void decrementProgressCounter() {
		progressCount_--;
		if(progressCount_ < 0) { //Should never happen!
			Log.e(this.getClass().toString(), "ERROR progresscount is negative!");
		}

		if(progressCount_ <= 0) {
			actionBar_.setProgressBarVisibility(View.GONE);
		}
	}
}
